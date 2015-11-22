package org.hibernate.cfg.reveng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.JDBCException;
import org.hibernate.cfg.JDBCBinderException;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.sql.Alias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimaryKeyProcessor {

	private static final Logger log = LoggerFactory.getLogger(PrimaryKeyProcessor.class);

	public static void processPrimaryKey(
			MetaDataDialect metaDataDialect, 
			ReverseEngineeringStrategy revengStrategy, 
			String defaultSchema, 
			String defaultCatalog, 
			DatabaseCollector dbs, 
			Table table) {
				
		List columns = new ArrayList();
		PrimaryKey key = null;
		Iterator<Map<String, Object>> primaryKeyIterator = null;
		try {
			Map primaryKeyRs = null;	
			primaryKeyIterator = metaDataDialect.getPrimaryKeys(getCatalogForDBLookup(table.getCatalog(), defaultCatalog), getSchemaForDBLookup(table.getSchema(), defaultSchema), table.getName() );		
		
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
					metaDataDialect.close(primaryKeyIterator);
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
	      	List<String> userPrimaryKey = RevEngUtils.getPrimaryKeyInfoInRevengStrategy(revengStrategy, table, defaultCatalog, defaultSchema);	      	if(userPrimaryKey!=null && !userPrimaryKey.isEmpty()) {
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

	      Iterator suggestedPrimaryKeyStrategyName = metaDataDialect.getSuggestedPrimaryKeyStrategyName( getCatalogForDBLookup(table.getCatalog(), defaultCatalog), getSchemaForDBLookup(table.getSchema(), defaultSchema), table.getName() );
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
						metaDataDialect.close(suggestedPrimaryKeyStrategyName);
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
	    		  Column col = getColumn(metaDataDialect, table, name);
	    		  key.addColumn(col);
	    	  }
	    	  log.debug("primary key for " + table + " -> "  + key);
	      } 
	     	      
	}

	private static String getCatalogForDBLookup(String catalog, String defaultCatalog) {
		return catalog==null?defaultCatalog:catalog;			
	}

	private static String getSchemaForDBLookup(String schema, String defaultSchema) {
		return schema==null?defaultSchema:schema;
	}

	private static Column getColumn(MetaDataDialect metaDataDialect, Table table, String columnName) {
		Column column = new Column();
		column.setName(quote(metaDataDialect, columnName));
		Column existing = table.getColumn(column);
		if(existing!=null) {
			column = existing;
		}
		return column;
	}

	private static String quote(MetaDataDialect metaDataDialect, String columnName) {
		   if(columnName==null) return columnName;
		   if(metaDataDialect.needQuote(columnName)) {
			   if(columnName.length()>1 && columnName.charAt(0)=='`' && columnName.charAt(columnName.length()-1)=='`') {
				   return columnName; // avoid double quoting
			   }
			   return "`" + columnName + "`";
		   } else {
			   return columnName;
		   }		
	}

}
