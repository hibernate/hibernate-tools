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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.JDBCException;
import org.hibernate.MappingException;
import org.hibernate.cfg.JDBCBinderException;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.sql.Alias;
import org.hibernate.internal.util.StringHelper;

public class JDBCReader {

	private static final Logger log = LoggerFactory.getLogger(JDBCReader.class);
	
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
				foundTables.addAll(TableProcessor.processTables(getMetaDataDialect(), revengStrategy, defaultSchema, defaultCatalog, dbs, new SchemaSelection(catalog, schema), hasIndices, progress));
			} else {
				for (Iterator iter = schemaSelectors.iterator(); iter.hasNext();) {
					SchemaSelection selection = (SchemaSelection) iter.next();
					foundTables.addAll(TableProcessor.processTables(getMetaDataDialect(), revengStrategy, defaultSchema, defaultCatalog, dbs, selection, hasIndices, progress));
				}
			}
			
			Iterator tables = foundTables.iterator(); // not dbs.iterateTables() to avoid "double-read" of columns etc.
			while ( tables.hasNext() ) {
				Table table = (Table) tables.next();
				BasicColumnProcessor.processBasicColumns(getMetaDataDialect(), revengStrategy, defaultSchema, defaultCatalog, table, progress);
				PrimaryKeyProcessor.processPrimaryKey(getMetaDataDialect(), revengStrategy, defaultSchema, defaultCatalog, dbs, table);
				if(hasIndices.contains(table)) {
					IndexProcessor.processIndices(getMetaDataDialect(), defaultSchema, defaultCatalog, table);
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
				String fkCatalog = getCatalogForModel((String) exportedKeyRs.get("FKTABLE_CAT"), defaultCatalog);
				String fkSchema = getSchemaForModel((String) exportedKeyRs.get("FKTABLE_SCHEM"), defaultSchema);
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
        
        List<ForeignKey> userForeignKeys = revengStrategy.getForeignKeys(TableIdentifier.create(referencedTable));
        if(userForeignKeys!=null) {
        	Iterator<ForeignKey> iterator = userForeignKeys.iterator();
        	while ( iterator.hasNext() ) {
        		ForeignKey element = iterator.next();
        		
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

	
	private boolean safeEquals(Object value, Object tf) {
		if(value==tf) return true;
		if(value==null) return false;
		return value.equals(tf);
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
		protected String getCatalogForModel(String catalog, String defaultCatalog) {
			if(catalog==null) return null;
			if(catalog.equals(defaultCatalog)) return null;
			return catalog;
		}

		/** If catalog is equal to defaultSchema then we return null so it will be null in the generated code. */
		protected String getSchemaForModel(String schema, String defaultSchema) {
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

