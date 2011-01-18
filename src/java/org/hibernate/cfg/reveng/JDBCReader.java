package org.hibernate.cfg.reveng;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.JDBCException;
import org.hibernate.MappingException;
import org.hibernate.cfg.JDBCBinderException;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.sql.Alias;
import org.hibernate.util.StringHelper;

public class JDBCReader {

	private static final Log log = LogFactory.getLog(JDBCReader.class);
	
	private final ReverseEngineeringStrategy revengStrategy;
	
	private MetaDataDialect metadataDialect;

	private final ConnectionProvider provider;

	private final SQLExceptionConverter sec;

	private final String defaultSchema;
	private final String defaultCatalog;
	
	public JDBCReader(MetaDataDialect dialect, ConnectionProvider provider, SQLExceptionConverter sec, String defaultCatalog, String defaultSchema, ReverseEngineeringStrategy reveng) {
		this.metadataDialect = dialect;
		this.provider = provider;
		this.sec = sec;
		this.revengStrategy = reveng;
		this.defaultCatalog = defaultCatalog;
		this.defaultSchema = defaultSchema;
		if(revengStrategy==null) {
			throw new IllegalStateException("Strategy cannot be null");
		}
	}
		
	public List readDatabaseSchema(DatabaseCollector dbs, String catalog, String schema, ProgressListener progress) {
		try {
			ReverseEngineeringRuntimeInfo info = new ReverseEngineeringRuntimeInfo(provider, sec, dbs);
			getMetaDataDialect().configure(info);
			revengStrategy.configure(info);
			
			Set hasIndices = new HashSet();
			
			List schemaSelectors = revengStrategy.getSchemaSelections();
			List foundTables = new ArrayList();
			if(schemaSelectors==null) {
				foundTables.addAll( processTables(dbs, new SchemaSelection(catalog, schema), hasIndices, progress) );
			} else {
				for (Iterator iter = schemaSelectors.iterator(); iter.hasNext();) {
					SchemaSelection selection = (SchemaSelection) iter.next();
					foundTables.addAll( processTables(dbs, selection, hasIndices, progress) );
				}
			}
			
			Iterator tables = foundTables.iterator(); // not dbs.iterateTables() to avoid "double-read" of columns etc.
			while ( tables.hasNext() ) {
				Table table = (Table) tables.next();
				processBasicColumns(table, progress);
				processPrimaryKey(dbs, table);
				if(hasIndices.contains(table)) {
					processIndices(table);
				}
			}
			
			tables = foundTables.iterator(); //dbs.iterateTables();
			Map oneToManyCandidates = resolveForeignKeys( dbs, tables, progress );
			
			dbs.setOneToManyCandidates(oneToManyCandidates);
			
			return foundTables;
		} finally {
			getMetaDataDialect().close();
			revengStrategy.close();
		}
	}

	/**
	 * Iterates the tables and find all the foreignkeys that refers to something that is available inside the DatabaseCollector.
	 * @param dbs
	 * @param progress
	 * @param tables
	 * @return
	 */
	private Map resolveForeignKeys(DatabaseCollector dbs, Iterator tables, ProgressListener progress) {
		List fks = new ArrayList();
		while ( tables.hasNext() ) {
			Table table = (Table) tables.next();
			// Done here after the basic process of collections as we might not have touched 
			// all referenced tables (this ensure the columns are the same instances througout the basic JDBC derived model.
			// after this stage it should be "ok" to divert from keeping columns in sync as it can be required if the same 
			//column is used with different aliases in the ORM mapping.
			ForeignKeysInfo foreignKeys = processForeignKeys(dbs, table, progress);
			fks.add( foreignKeys );				  	   
		}
		
		Map oneToManyCandidates = new HashMap();			
		for (Iterator iter = fks.iterator(); iter.hasNext();) {
			ForeignKeysInfo element = (ForeignKeysInfo) iter.next();
			Map map = element.process( revengStrategy ); // the actual foreignkey is created here.
			mergeMultiMap( oneToManyCandidates, map );
		}
		return oneToManyCandidates;
	}
	
	static class ForeignKeysInfo {
		
		final Map dependentTables;
		final Map dependentColumns;
		final Map referencedColumns;
		private final Table referencedTable;
		
		public ForeignKeysInfo(Table referencedTable, Map tables, Map columns, Map refColumns) {
			this.referencedTable = referencedTable;
			this.dependentTables = tables;
			this.dependentColumns = columns;
			this.referencedColumns = refColumns;
		}
		
		Map process(ReverseEngineeringStrategy revengStrategy) {
			Map oneToManyCandidates = new HashMap();
	        Iterator iterator = dependentTables.entrySet().iterator();
			while (iterator.hasNext() ) {
				Map.Entry entry = (Map.Entry) iterator.next();
				String fkName = (String) entry.getKey();
				Table fkTable = (Table) entry.getValue();			
				List columns = (List) dependentColumns.get(fkName);
				List refColumns = (List) referencedColumns.get(fkName);
				
				String className = revengStrategy.tableToClassName(TableIdentifier.create(referencedTable) );

				ForeignKey key = fkTable.createForeignKey(fkName, columns, className, refColumns);			
				key.setReferencedTable(referencedTable);

				addToMultiMap(oneToManyCandidates, className, key);				
			}
			// map<className, foreignkey>
			return oneToManyCandidates;
		}
	}
	
	protected ForeignKeysInfo processForeignKeys(DatabaseCollector dbs, Table referencedTable, ProgressListener progress) throws JDBCBinderException {
		// foreign key name to list of columns
		Map dependentColumns = new HashMap();
		// foreign key name to Table
		Map dependentTables = new HashMap();
		Map referencedColumns = new HashMap();
		
		short bogusFkName = 0;
		
		// first get all the relationships dictated by the database schema
		
		Iterator exportedKeyIterator = null;
		
        log.debug("Calling getExportedKeys on " + referencedTable);
        progress.startSubTask("Finding exported foreignkeys on " + referencedTable.getName());
        try {
        	Map exportedKeyRs = null;
        	exportedKeyIterator = getMetaDataDialect().getExportedKeys(getCatalogForDBLookup(referencedTable.getCatalog()), getSchemaForDBLookup(referencedTable.getSchema()), referencedTable.getName() );
        try {
			while (exportedKeyIterator.hasNext() ) {
				exportedKeyRs = (Map) exportedKeyIterator.next();
				String fkCatalog = getCatalogForModel((String) exportedKeyRs.get("FKTABLE_CAT"));
				String fkSchema = getSchemaForModel((String) exportedKeyRs.get("FKTABLE_SCHEM"));
				String fkTableName = (String) exportedKeyRs.get("FKTABLE_NAME");
				String fkColumnName = (String) exportedKeyRs.get("FKCOLUMN_NAME");
				String pkColumnName = (String) exportedKeyRs.get("PKCOLUMN_NAME");
				String fkName = (String) exportedKeyRs.get("FK_NAME");
				short keySeq = ((Short)exportedKeyRs.get("KEY_SEQ")).shortValue();
								
				Table fkTable = dbs.getTable(fkSchema, fkCatalog, fkTableName);
				if(fkTable==null) {
					//	filter out stuff we don't have tables for!
					log.debug("Foreign key " + fkName + " references unknown or filtered table " + Table.qualify(fkCatalog, fkSchema, fkTableName) );
					continue;
				} else {
					log.debug("Foreign key " + fkName);
				}
				
				// TODO: if there is a relation to a column which is not a pk
				//       then handle it as a property-ref
				
				if (keySeq == 0) {
					bogusFkName++;
				}
				
				if (fkName == null) {
					// somehow reuse hibernates name generator ?
					fkName = Short.toString(bogusFkName);
				}
				//Table fkTable = mappings.addTable(fkSchema, fkCatalog, fkTableName, null, false);
				
				
				List depColumns =  (List) dependentColumns.get(fkName);
				if (depColumns == null) {
					depColumns = new ArrayList();
					dependentColumns.put(fkName,depColumns);
					dependentTables.put(fkName, fkTable);
				} 
				else {
					Object previousTable = dependentTables.get(fkName);
					if(fkTable != previousTable) {
						throw new JDBCBinderException("Foreign key name (" + fkName + ") mapped to different tables! previous: " + previousTable + " current:" + fkTable);
					}
				}
				
				Column column = new Column(fkColumnName);
				Column existingColumn = fkTable.getColumn(column);
				column = existingColumn==null ? column : existingColumn;
				
				depColumns.add(column);
				
				List primColumns = (List) referencedColumns.get(fkName);
				if (primColumns == null) {
					primColumns = new ArrayList();
					referencedColumns.put(fkName,primColumns);					
				} 
				
				Column refColumn = new Column(pkColumnName);
				existingColumn = referencedTable.getColumn(refColumn);
				refColumn = existingColumn==null?refColumn:existingColumn;
				
				primColumns.add(refColumn);
				
			}
		} 
        finally {
        	try {
        		if(exportedKeyIterator!=null) {
        			getMetaDataDialect().close(exportedKeyIterator);
        		}
        	} catch(JDBCException se) {
        		log.warn("Exception while closing result set for foreign key meta data",se);
        	}
        }
        } catch(JDBCException se) {
        	//throw sec.convert(se, "Exception while reading foreign keys for " + referencedTable, null);
        	log.warn("Exception while reading foreign keys for " + referencedTable + " [" + se.toString() + "]", se);
        	// sybase (and possibly others has issues with exportedkeys) see HBX-411
        	// we continue after this to allow user provided keys to be added.
        }
        
        List userForeignKeys = revengStrategy.getForeignKeys(TableIdentifier.create(referencedTable));
        if(userForeignKeys!=null) {
        	Iterator iterator = userForeignKeys.iterator();
        	while ( iterator.hasNext() ) {
        		ForeignKey element = (ForeignKey) iterator.next();
        		
        		if(!equalTable(referencedTable, element.getReferencedTable() ) ) {
        			log.debug("Referenced table " + element.getReferencedTable().getName() + " is not " +  referencedTable + ". Ignoring userdefined foreign key " + element );
        			continue; // skip non related foreign keys
        		}
        		
        		String userfkName = element.getName();        		
        		Table userfkTable = element.getTable();
        		
        		List userColumns = element.getColumns();
        		List userrefColumns = element.getReferencedColumns();
        		
        		Table deptable = (Table) dependentTables.get(userfkName);
        		if(deptable!=null) { // foreign key already defined!?
        			throw new MappingException("Foreign key " + userfkName + " already defined in the database!");
        		}
        		
        		deptable = dbs.getTable(userfkTable.getSchema(), userfkTable.getCatalog(), userfkTable.getName() );
        		if(deptable==null) {
					//	filter out stuff we don't have tables for!
					log.debug("User defined foreign key " + userfkName + " references unknown or filtered table " + TableIdentifier.create(userfkTable) );
					continue;        			
        		}
        		
        		dependentTables.put(userfkName, deptable);
        		
        		List depColumns = new ArrayList(userColumns.size() );
        		Iterator colIterator = userColumns.iterator();
        		while(colIterator.hasNext() ) {
        			Column jdbcColumn = (Column) colIterator.next();
        			Column column = new Column(jdbcColumn.getName() );
    				Column existingColumn = deptable.getColumn(column);
    				column = existingColumn==null ? column : existingColumn;
    				depColumns.add(column);
        		}
        		
        		List refColumns = new ArrayList(userrefColumns.size() );
        		colIterator = userrefColumns.iterator();
        		while(colIterator.hasNext() ) {
        			Column jdbcColumn = (Column) colIterator.next();
        			Column column = new Column(jdbcColumn.getName() );
    				Column existingColumn = referencedTable.getColumn(column);
    				column = existingColumn==null ? column : existingColumn;
    				refColumns.add(column);
        		}
        		
        		referencedColumns.put(userfkName, refColumns );
        		dependentColumns.put(userfkName, depColumns );
        	}
        }
        
        
        return new ForeignKeysInfo(referencedTable, dependentTables, dependentColumns, referencedColumns);
        
       }

	
	/**
	 * @param dbs 
	 * @param catalog
	 * @param schema
	 * @param table
	 * @param primaryKeys
	 * @return
	 * @throws SQLException
	 */
	private void processPrimaryKey(DatabaseCollector dbs, Table table) {
				
		List columns = new ArrayList();
		PrimaryKey key = null;
		Iterator primaryKeyIterator = null;
		try {
			Map primaryKeyRs = null;	
			primaryKeyIterator = getMetaDataDialect().getPrimaryKeys(getCatalogForDBLookup(table.getCatalog()), getSchemaForDBLookup(table.getSchema()), table.getName() );		
		
			while (primaryKeyIterator.hasNext() ) {
				primaryKeyRs = (Map) primaryKeyIterator.next();
				
				/*String ownCatalog = primaryKeyRs.getString("TABLE_CAT");
				 String ownSchema = primaryKeyRs.getString("TABLE_SCHEM");
				 String ownTable = primaryKeyRs.getString("TABLE_NAME");*/
				
				String columnName = (String) primaryKeyRs.get("COLUMN_NAME");
				short seq = ((Short)primaryKeyRs.get("KEY_SEQ")).shortValue();
				String name = (String) primaryKeyRs.get("PK_NAME");
				
				if(key==null) {
					key = new PrimaryKey();
					key.setName(name);
					key.setTable(table);
					if(table.getPrimaryKey()!=null) {
						throw new JDBCBinderException(table + " already has a primary key!"); //TODO: ignore ?
					}
					table.setPrimaryKey(key);
				} 
				else {
					if(!(name==key.getName() ) && name!=null && !name.equals(key.getName() ) ) {
						throw new JDBCBinderException("Duplicate names found for primarykey. Existing name: " + key.getName() + " JDBC name: " + name + " on table " + table);
					}	      		
				}
				
				columns.add(new Object[] { new Short(seq), columnName});
			}
		} finally {
			if (primaryKeyIterator!=null) {
				try {
					getMetaDataDialect().close(primaryKeyIterator);
				} catch(JDBCException se) {
					log.warn("Exception when closing resultset for reading primary key information",se);
				}
			}
		}
	      
	      // sort the columns accoring to the key_seq.
	      Collections.sort(columns,new Comparator() {
			public boolean equals(Object obj) {
				return super.equals(obj);
			}

			public int compare(Object o1, Object o2) {
				Short left = (Short) ( (Object[]) o1)[0];
				Short right = (Short) ( (Object[]) o2)[0];
				return left.compareTo(right);
			}
			
			public int hashCode() {
				return super.hashCode();
			}
	      });
	      
	      List t = new ArrayList(columns.size());
	      Iterator cols = columns.iterator();
	      while (cols.hasNext() ) {
			Object[] element = (Object[]) cols.next();
			t.add(element[1]);
	      }
	      columns = t;
	      
	      if(key==null) {
	      	log.warn("The JDBC driver didn't report any primary key columns in " + table.getName() + ". Asking rev.eng. strategy" );
	      	List userPrimaryKey = revengStrategy.getPrimaryKeyColumnNames(TableIdentifier.create(table));
	      	if(userPrimaryKey!=null && !userPrimaryKey.isEmpty()) {
	      		key = new PrimaryKey();
	      		key.setName(new Alias(15, "PK").toAliasString( table.getName()));
	      		key.setTable(table);
	      		if(table.getPrimaryKey()!=null) {
	      			throw new JDBCBinderException(table + " already has a primary key!"); //TODO: ignore ?
	      		}
	      		table.setPrimaryKey(key);
	      		columns = new ArrayList(userPrimaryKey);
	      	} else {
	      		log.warn("Rev.eng. strategy did not report any primary key columns for " + table.getName());
	      	}	      	
	      }

	      Iterator suggestedPrimaryKeyStrategyName = getMetaDataDialect().getSuggestedPrimaryKeyStrategyName( getCatalogForDBLookup(table.getCatalog()), getSchemaForDBLookup(table.getSchema()), table.getName() );
	      try {
	      if(suggestedPrimaryKeyStrategyName.hasNext()) {
	    	  Map m = (Map) suggestedPrimaryKeyStrategyName.next();
	    	  String suggestion = (String) m.get( "HIBERNATE_STRATEGY" );
	    	  if(suggestion!=null) {
	    		  dbs.addSuggestedIdentifierStrategy( table.getCatalog(), table.getSchema(), table.getName(), suggestion );
	    	  }
	      }
	      } finally {
	    	  if(suggestedPrimaryKeyStrategyName!=null) {
					try {
						getMetaDataDialect().close(suggestedPrimaryKeyStrategyName);
					} catch(JDBCException se) {
						log.warn("Exception while closing iterator for suggested primary key strategy name",se);
					}
				}	    	  
	      }
	      	      
	      if(key!=null) {
	    	  cols = columns.iterator();
	    	  while (cols.hasNext() ) {
	    		  String name = (String) cols.next();
	    		  // should get column from table if it already exists!
	    		  Column col = getColumn(table, name);
	    		  key.addColumn(col);
	    	  }
	    	  log.debug("primary key for " + table + " -> "  + key);
	      } 
	     	      
	}

	private boolean safeEquals(Object value, Object tf) {
		if(value==tf) return true;
		if(value==null) return false;
		return value.equals(tf);
	}

	private Collection processTables(DatabaseCollector dbs, SchemaSelection schemaSelection, Set hasIndices, ProgressListener progress) {
		Map tableRs = null;
		Iterator tableIterator = null;
		List tables = new ArrayList();
		boolean multiSchema = false; // TODO: the code below detects if the reveng is multischema'ed, but not used for anything yet. should be used to remove schema/catalog info from output if only one schema/catalog used.
		
		  try {			  
		     progress.startSubTask("Finding tables in " + schemaSelection);
		     
		     tableIterator = getMetaDataDialect().getTables(StringHelper.replace(schemaSelection.getMatchCatalog(),".*", "%"), 
		    		                                        StringHelper.replace(schemaSelection.getMatchSchema(),".*", "%"), 
		    		                                        StringHelper.replace(schemaSelection.getMatchTable(),".*", "%"));
		     String[] lastQualifier = null;
		     String[] foundQualifier = new String[2];
		     
		     while (tableIterator.hasNext() ) {
		        tableRs = (Map) tableIterator.next();
		        String tableName = (String) tableRs.get("TABLE_NAME");
				String schemaName = (String) tableRs.get("TABLE_SCHEM");
		        String catalogName = (String) tableRs.get("TABLE_CAT");
		        
		        TableIdentifier ti = new TableIdentifier(catalogName, schemaName, tableName);		        
				if(revengStrategy.excludeTable(ti) ) {
					log.debug("Table " + ti + " excluded by strategy");
		        	continue;
		        }
				
				if(!multiSchema) {
					foundQualifier[0] = catalogName;
					foundQualifier[1] = schemaName;
					if(lastQualifier==null) {
						lastQualifier=new String[2];
						lastQualifier[0] = foundQualifier[0];
						lastQualifier[1] = foundQualifier[1];					
					}
					if((!safeEquals(lastQualifier[0],foundQualifier[0])) || (!safeEquals(lastQualifier[1],foundQualifier[1]))) {
						multiSchema = true;
					}
				}
				
				tables.add(new HashMap(tableRs));
		     }
		  } 
		  finally {
			  try {
				  if (tableIterator!=null) getMetaDataDialect().close(tableIterator);
			  } 
			  catch (Exception ignore) {
			  }
		  }
		  
		  List processedTables = new ArrayList();
		  tableIterator = tables.iterator();
		  while (tableIterator.hasNext() ) {
			  tableRs = (Map) tableIterator.next();
			  String tableName = (String) tableRs.get("TABLE_NAME");
			  String schemaName = (String) tableRs.get("TABLE_SCHEM");
			  String catalogName = (String) tableRs.get("TABLE_CAT");
			  
			  /*TableIdentifier ti = new TableIdentifier(catalogName, schemaName, tableName);
			   if(revengStrategy.excludeTable(ti) ) {
			   log.debug("Table " + ti + " excluded by strategy");
			   continue;
			   }*/
			  
			  String comment = (String) tableRs.get("REMARKS");
			  String tableType = (String) tableRs.get("TABLE_TYPE");
			  
			  if(dbs.getTable(schemaName, catalogName, tableName)!=null) {
				  log.debug("Ignoring " + tableName + " since it has already been processed");
				  continue;
			  } else {
				  if ( ("TABLE".equalsIgnoreCase(tableType) || "VIEW".equalsIgnoreCase(tableType) /*|| "SYNONYM".equals(tableType) */) ) { //||
					  // ("SYNONYM".equals(tableType) && isOracle() ) ) { // only on oracle ? TODO: HBX-218
					  // it's a regular table or a synonym
					  
					  // ensure schema and catalogname is truly empty (especially mysql returns null schema, "" catalog)
					  if(schemaName!=null && schemaName.trim().length()==0) {
						  schemaName = null;
					  }                     
					  if(catalogName!=null && catalogName.trim().length()==0) {
						  catalogName=null;
					  }
					  log.debug("Adding table " + tableName + " of type " + tableType);
					  progress.startSubTask("Found " + tableName);
					  Table table = dbs.addTable(getSchemaForModel(schemaName), getCatalogForModel(catalogName), tableName);
					  table.setComment(comment);
					  if(tableType.equalsIgnoreCase("TABLE")) {
						  hasIndices.add(table);
					  }
					  processedTables.add( table );
				  }
				  else {
					  log.debug("Ignoring table " + tableName + " of type " + tableType);
				  }
			  }
		  }
		  
		  return processedTables;
	}

	private void processBasicColumns(Table table, ProgressListener progress) {
		// get the columns
		
		String qualify = Table.qualify(table.getCatalog(), table.getSchema(), table.getName() );
		Iterator columnIterator = null;
		
		try {
			Map columnRs = null;
			log.debug("Finding columns for " + qualify );
			progress.startSubTask("Finding columns for " + qualify);
			columnIterator = getMetaDataDialect().getColumns(getCatalogForDBLookup(table.getCatalog()), getSchemaForDBLookup(table.getSchema()), table.getName(), null);
			//dumpHeader(columnRs);
			while (columnIterator.hasNext() ) {
				//dumpRow(columnRs);
				columnRs = (Map) columnIterator.next();
				String tableName = (String) columnRs.get("TABLE_NAME");
				int sqlType = ((Integer)columnRs.get("DATA_TYPE")).intValue();
				//String sqlTypeName = (String) columnRs.get("TYPE_NAME");
				String columnName = (String) columnRs.get("COLUMN_NAME");
				
			
				
				
				String comment = (String) columnRs.get("REMARKS");
				
				TableIdentifier ti = TableIdentifier.create(table);
				if(revengStrategy.excludeColumn(ti, columnName)) {
					log.debug("Column " + ti + "." + columnName + " excluded by strategy");
					continue;
				}
				if(!tableName.equals(table.getName())) {
					log.debug("Table name " + tableName + " does not match requested " + table.getName() + ". Ignoring column " + columnName + " since it either is invalid or a duplicate" );
					continue;
				}
				
				//String columnDefaultValue = columnRs.getString("COLUMN_DEF"); TODO: only read if have a way to avoid issues with clobs/lobs and similar
				int dbNullability = ((Integer)columnRs.get("NULLABLE")).intValue();
				boolean isNullable = true;
				switch (dbNullability) {
				case DatabaseMetaData.columnNullable:
				case DatabaseMetaData.columnNullableUnknown:
					isNullable = true;
					break;
				case DatabaseMetaData.columnNoNulls:
					isNullable = false;
					break;
				default:
					isNullable = true;
				}
				
				int size = ((Integer)columnRs.get("COLUMN_SIZE")).intValue();
				int decimalDigits = ((Integer)columnRs.get("DECIMAL_DIGITS")).intValue();
				
				Column column = new Column();
				column.setName(quote(columnName));
				Column existing = table.getColumn(column);
				if(existing!=null) {
					// TODO: should we just pick it up and fill it up with whatever we get from the db instead ?
					throw new JDBCBinderException(column + " already exists in " + qualify);
				}
								
				//TODO: column.setSqlType(sqlTypeName); //this does not work 'cos the precision/scale/length are not retured in TYPE_NAME
				//column.setSqlType(sqlTypeName);
				column.setComment(comment);
				column.setSqlTypeCode(new Integer(sqlType) );
                if(intBounds(size) ) {
                	if(JDBCToHibernateTypeHelper.typeHasLength(sqlType) ) {
                		column.setLength(size);
                	} 
                	if(JDBCToHibernateTypeHelper.typeHasScaleAndPrecision(sqlType) ) {
                		column.setPrecision(size); 
                	}
				} 
                if(intBounds(decimalDigits) ) {
                	if(JDBCToHibernateTypeHelper.typeHasScaleAndPrecision(sqlType) ) {
                		column.setScale(decimalDigits);
                	}
				}
				
				column.setNullable(isNullable);

				// columnDefaultValue is useless for Hibernate
				// isIndexed  (available via Indexes)
				// unique - detected when getting indexes
				// isPk - detected when finding primary keys				
				
				table.addColumn(column);
			}
		}
		finally {
			
			if(columnIterator!=null) {
				try {
					getMetaDataDialect().close(columnIterator);
				} catch(JDBCException se) {
					log.warn("Exception while closing iterator for column meta data",se);
				}
			}
		}
				
	}


	private String quote(String columnName) {
		   if(columnName==null) return columnName;
		   if(getMetaDataDialect().needQuote(columnName)) {
			   if(columnName.length()>1 && columnName.charAt(0)=='`' && columnName.charAt(columnName.length()-1)=='`') {
				   return columnName; // avoid double quoting
			   }
			   return "`" + columnName + "`";
		   } else {
			   return columnName;
		   }		
	}

	public MetaDataDialect getMetaDataDialect() {
		return metadataDialect;
	}
	
	  
		/**
	     * @param size
	     * @return
	     */
	    private boolean intBounds(int size) {
	        return size>=0 && size!=Integer.MAX_VALUE;
	    }

	
	    private void processIndices(Table table) {
			
			Map indexes = new HashMap(); // indexname (String) -> Index
			Map uniquekeys = new HashMap(); // name (String) -> UniqueKey
			Map uniqueColumns = new HashMap(); // Column -> List<Index>
			
			Iterator indexIterator = null;
			try {
				Map indexRs = null;	
				indexIterator = getMetaDataDialect().getIndexInfo(getCatalogForDBLookup(table.getCatalog()), getSchemaForDBLookup(table.getSchema()), table.getName());
				
				while (indexIterator.hasNext() ) {
					indexRs = (Map) indexIterator.next();
					String indexName = (String) indexRs.get("INDEX_NAME");
					String columnName = (String) indexRs.get("COLUMN_NAME");
					boolean unique = !((Boolean)indexRs.get("NON_UNIQUE")).booleanValue();
					
					if (columnName != null || indexName != null) { // both can be non-null with statistical indexs which we don't have any use for.
						
						if(unique) {
							UniqueKey key = (UniqueKey) uniquekeys.get(indexName);
							if (key==null) {
								key = new UniqueKey();
								key.setName(indexName);
								key.setTable(table);
								table.addUniqueKey(key);							
								uniquekeys.put(indexName, key);
							}
					
							if(indexes.containsKey(indexName) ) {
								throw new JDBCBinderException("UniqueKey exists also as Index! ");
							}
							Column column = getColumn(table, columnName);
							key.addColumn(column);
							
							if (unique && key.getColumnSpan()==1) {
								// make list of columns that has the chance of being unique
								List l = (List) uniqueColumns.get(column);
								if (l == null) {
									l = new ArrayList();
									uniqueColumns.put(column, l);
								}
								l.add(key);
							}
						} 
						else {
							Index index = (Index) indexes.get(indexName);
							if(index==null) {
								index = new Index();
								index.setName(indexName);
								index.setTable(table);
								table.addIndex(index);
								indexes.put(indexName, index);					
							}
							
							if(uniquekeys.containsKey(indexName) ) {
								throw new JDBCBinderException("Index exists also as Unique! ");
							}
							Column column = getColumn(table, columnName);
							index.addColumn(column);
						}
						
					} 
					else {
						if(DatabaseMetaData.tableIndexStatistic != ((Short)indexRs.get("TYPE")).shortValue() ) {
							log.warn("Index was not statistical, but no column name was found in " + indexName);
						}
							
					}								
				}
			} 
			catch (JDBCException t) {
				log.warn("Exception while trying to get indexinfo on " + Table.qualify(table.getCatalog(), table.getSchema(), table.getName() ) +  "=" + t.getMessage() );
				// Bug #604761 Oracle getIndexInfo() needs major grants And other dbs sucks too ;)
				// http://sourceforge.net/tracker/index.php?func=detail&aid=604761&group_id=36044&atid=415990				
			} 
			finally {
				if (indexIterator != null) {
					try {
						getMetaDataDialect().close(indexIterator);
					} catch(JDBCException se) {
						log.warn("Exception while trying to close resultset for index meta data",se);
					}
				}
			}
			
			// mark columns that are unique TODO: multiple columns are not unique on their own.
			Iterator iterator = uniqueColumns.entrySet().iterator();
			while (iterator.hasNext() ) {
				Map.Entry entry = (Map.Entry) iterator.next();
				Column col = (Column) entry.getKey();
				Iterator keys = ( (List)entry.getValue() ).iterator();
				 while (keys.hasNext() ) {
					UniqueKey key = (UniqueKey) keys.next();
				
					if(key.getColumnSpan()==1) {
						col.setUnique(true);
					}
				}
			}
			
			iterator = uniquekeys.entrySet().iterator();
			while(iterator.hasNext()) {
				// if keyset has no overlaps with primary key (table.getPrimaryKey())
				// if only key matches then mark as setNaturalId(true);
				iterator.next();
			}
		}

	    private void mergeMultiMap(Map dest, Map src) {
	    	Iterator items = src.entrySet().iterator();
	    	
	    	while ( items.hasNext() ) {
	    		Map.Entry element = (Map.Entry) items.next();
	    		
	    		List existing = (List) dest.get( element.getKey() );
	    		if(existing == null) {
	    			dest.put( element.getKey(), element.getValue() );
	    		} 
	    		else {
	    			existing.addAll( (List)element.getValue() );
	    		}			
	    	}
	    	
	    }

	    private boolean equalTable(Table table1, Table table2) {
			return  table1.getName().equals(table2.getName()) 
					&& ( equal(table1.getSchema(), table2.getSchema() )
					&& ( equal(table1.getCatalog(), table2.getCatalog() ) ) );
		}

		private boolean equal(String str, String str2) {
			if(str==str2) return true;
			if(str!=null && str.equals(str2) ) return true;
			return false;
		}

		static private void addToMultiMap(Map multimap, String key, Object item) {
			List existing = (List) multimap.get(key);
			if(existing == null) {
				existing = new ArrayList();
				multimap.put(key, existing);
			}
			existing.add(item);
		}

		private Column getColumn(Table table, String columnName) {
			Column column = new Column();
			column.setName(quote(columnName));
			Column existing = table.getColumn(column);
			if(existing!=null) {
				column = existing;
			}
			return column;
		}

		static class NoopProgressListener implements ProgressListener {
			public void startSubTask(String name) {	// noop };
			}
		}
		
		public List readDatabaseSchema(DatabaseCollector dbs, String catalog, String schema) {
			return readDatabaseSchema(dbs, catalog, schema, new NoopProgressListener());
		}
		
		/** If catalog is equal to defaultCatalog then we return null so it will be null in the generated code. */
		protected String getCatalogForModel(String catalog) {
			if(catalog==null) return null;
			if(catalog.equals(defaultCatalog)) return null;
			return catalog;
		}

		/** If catalog is equal to defaultSchema then we return null so it will be null in the generated code. */
		protected String getSchemaForModel(String schema) {
			if(schema==null) return null;
			if(schema.equals(defaultSchema)) return null;
			return schema;
		}
		
		protected String getCatalogForDBLookup(String catalog) {
			return catalog==null?defaultCatalog:catalog;			
		}

		protected String getSchemaForDBLookup(String schema) {
			return schema==null?defaultSchema:schema;
		}

		public Set readSequences(String sql) {
			Set sequences = new HashSet();
			if (sql!=null) {
				Connection connection = null;
				try {
				
					connection = provider.getConnection();
					Statement statement = null;
					ResultSet rs = null;
					try {
						statement = connection.createStatement();
						rs = statement.executeQuery(sql);

						while ( rs.next() ) {
							sequences.add( rs.getString(1).toLowerCase().trim() );
						}
					}
					finally {
						if (rs!=null) rs.close();
						if (statement!=null) statement.close();
					}

				} catch (SQLException e) {
					sec.convert(e, "Problem while closing connection", null);
				}
				finally {
					if(connection!=null)
						try {
							provider.closeConnection( connection );
						}
						catch (SQLException e) {
							sec.convert(e, "Problem while closing connection", null);
						}
				} 
			}
			return sequences;
		}
}		

