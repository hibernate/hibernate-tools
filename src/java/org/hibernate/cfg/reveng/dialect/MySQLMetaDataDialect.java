package org.hibernate.cfg.reveng.dialect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MySQLMetaDataDialect extends JDBCMetaDataDialect {

	/**
	 * Based on info from http://dev.mysql.com/doc/refman/5.0/en/show-table-status.html
	 * Should work on pre-mysql 5 too since it uses the "old" SHOW TABLE command instead of SELECT from infotable.
	 */
	public Iterator getSuggestedPrimaryKeyStrategyName(String catalog, String schema, String table) {
		String sql = null;
			try {			
				catalog = caseForSearch( catalog );
				schema = caseForSearch( schema );
				table = caseForSearch( table );
				
				log.debug("geSuggestedPrimaryKeyStrategyName(" + catalog + "." + schema + "." + table + ")");
				
				sql = "show table status " + (catalog==null?"":" from " + catalog + " ") + (table==null?"":" like '" + table + "' ");
				PreparedStatement statement = getConnection().prepareStatement( sql );
				
				final String sc = schema;
				final String cat = catalog;
				return new ResultSetIterator(statement.executeQuery(), getSQLExceptionConverter()) {
					
					Map element = new HashMap();
					protected Object convertRow(ResultSet tableRs) throws SQLException {
						element.clear();
						element.put("TABLE_NAME", tableRs.getString("NAME"));
						element.put("TABLE_SCHEM", sc);
						element.put("TABLE_CAT", cat);
						
						String string = tableRs.getString("AUTO_INCREMENT");
						if(string==null) {
							element.put("HIBERNATE_STRATEGY", null);
						} else {
							element.put("HIBERNATE_STRATEGY", "identity");
						}
						return element;					
					}
					protected Throwable handleSQLException(SQLException e) {
						// schemaRs and catalogRs are only used for error reporting if
						// we get an exception
						throw getSQLExceptionConverter().convert( e,
								"Could not get list of suggested identity strategies from database. Probably a JDBC driver problem. ", null);					
					}
				};
			} catch (SQLException e) {
				throw getSQLExceptionConverter().convert(e, "Could not get list of suggested identity strategies from database. Probably a JDBC driver problem. ", sql);		         
			} 		
		}
	}
	
