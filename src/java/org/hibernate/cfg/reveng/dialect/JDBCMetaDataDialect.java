package org.hibernate.cfg.reveng.dialect;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.mapping.Table;

/**
 * MetaData dialect that uses standard JDBC for reading metadata.
 * 
 * @author Max Rydahl Andersen
 *
 */
public class JDBCMetaDataDialect extends AbstractMetaDataDialect {
	
	public Iterator getTables(String xcatalog, String xschema, String xtable) {
		try {			
			final String catalog = caseForSearch( xcatalog );
			final String schema = caseForSearch( xschema );
			final String table = caseForSearch( xtable );
			
			log.debug("getTables(" + catalog + "." + schema + "." + table + ")");
			
			ResultSet tableRs = getMetaData().getTables(catalog , schema , table, new String[] { "TABLE", "VIEW" });
			
			return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {
				
				Map element = new HashMap();
				protected Object convertRow(ResultSet tableResultSet) throws SQLException {
					element.clear();
					putTablePart( element, tableResultSet );
					element.put("TABLE_TYPE", tableResultSet.getString("TABLE_TYPE"));
					element.put("REMARKS", tableResultSet.getString("REMARKS"));
					return element;					
				}
				protected Throwable handleSQLException(SQLException e) {
					// schemaRs and catalogRs are only used for error reporting if
					// we get an exception
					String databaseStructure = getDatabaseStructure( catalog, schema );
					throw getSQLExceptionConverter().convert( e,
							"Could not get list of tables from database. Probably a JDBC driver problem. "
									+ databaseStructure, null );					
				}
			};
		} catch (SQLException e) {
			// schemaRs and catalogRs are only used for error reporting if we get an exception
			String databaseStructure = getDatabaseStructure(xcatalog,xschema);
			throw getSQLExceptionConverter().convert(e, "Could not get list of tables from database. Probably a JDBC driver problem. " + databaseStructure, null);		         
		} 		
	}
	
	public Iterator getIndexInfo(final String xcatalog, final String xschema, final String xtable) {
		try {
			final String catalog = caseForSearch( xcatalog );
			final String schema = caseForSearch( xschema );
			final String table = caseForSearch( xtable );
			
			log.debug("getIndexInfo(" + catalog + "." + schema + "." + table + ")");
			ResultSet tableRs = getMetaData().getIndexInfo(catalog , schema , table, false, true);
			
			return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {
				
				Map element = new HashMap();
				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					putTablePart(element, rs);
					element.put("INDEX_NAME", rs.getString("INDEX_NAME"));
					element.put("COLUMN_NAME", rs.getString("COLUMN_NAME"));
					element.put("NON_UNIQUE", Boolean.valueOf(rs.getBoolean("NON_UNIQUE")));
					element.put("TYPE", new Short(rs.getShort("TYPE")));					 
					return element;					
				}
				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(e, "Exception while getting index info for " + Table.qualify(catalog, schema, table), null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(e, "Exception while getting index info for " + Table.qualify(xcatalog, xschema, xtable), null);
		} 		
	}

	protected void putTablePart(Map element, ResultSet tableRs) throws SQLException {
		element.put("TABLE_NAME", tableRs.getString("TABLE_NAME"));
		element.put("TABLE_SCHEM", tableRs.getString("TABLE_SCHEM"));
		element.put("TABLE_CAT", tableRs.getString("TABLE_CAT"));
	}

	public Iterator getColumns(final String xcatalog, final String xschema, final String xtable, String xcolumn) {
		try {			  
			final String catalog = caseForSearch( xcatalog );
			final String schema = caseForSearch( xschema );
			final String table = caseForSearch( xtable );
			final String column = caseForSearch( xcolumn );
			
			log.debug("getColumns(" + catalog + "." + schema + "." + table + "." + column + ")");
			ResultSet tableRs = getMetaData().getColumns(catalog, schema, table, column);
			
			return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {
				
				Map element = new HashMap();
				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					putTablePart(element, rs);
					element.put("DATA_TYPE", new Integer(rs.getInt("DATA_TYPE")));
					element.put("TYPE_NAME", rs.getString("TYPE_NAME"));
					element.put("COLUMN_NAME", rs.getString("COLUMN_NAME"));
					element.put("NULLABLE", new Integer(rs.getInt("NULLABLE")));
					element.put("COLUMN_SIZE", new Integer(rs.getInt("COLUMN_SIZE")));
					element.put("DECIMAL_DIGITS", new Integer(rs.getInt("DECIMAL_DIGITS")));
					element.put("REMARKS", rs.getString("REMARKS"));
					return element;					
				}
				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(e, "Error while reading column meta data for " + Table.qualify(catalog, schema, table), null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(e, "Error while reading column meta data for " + Table.qualify(xcatalog, xschema, xtable), null);
		}	
	}

	public Iterator getPrimaryKeys(final String xcatalog, final String xschema, final String xtable) {
		try {
			final String catalog = caseForSearch( xcatalog );
			final String schema = caseForSearch( xschema );
			final String table = caseForSearch( xtable );
			
			log.debug("getPrimaryKeys(" + catalog + "." + schema + "." + table + ")");
			ResultSet tableRs = getMetaData().getPrimaryKeys(catalog, schema, table);
			
			return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {
				
				Map element = new HashMap();
				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					putTablePart(element, rs);
					element.put("COLUMN_NAME", rs.getString("COLUMN_NAME"));
					element.put("KEY_SEQ", new Short(rs.getShort("KEY_SEQ")));
					element.put("PK_NAME", rs.getString("PK_NAME"));
					return element;					
				}
				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(e, "Error while reading primary key meta data for " + Table.qualify(catalog, schema, table), null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(e, "Error while reading primary key meta data for " + Table.qualify(xcatalog, xschema, xtable), null);
		}	
	}

	public Iterator getExportedKeys(final String xcatalog, final String xschema, final String xtable) {
		try {
			final String catalog = caseForSearch( xcatalog );
			final String schema = caseForSearch( xschema );
			final String table = caseForSearch( xtable );
			
			log.debug("getExportedKeys(" + catalog + "." + schema + "." + table + ")");
			ResultSet tableRs = getMetaData().getExportedKeys(catalog, schema, table);
			
			return new ResultSetIterator(tableRs, getSQLExceptionConverter()) {
				
				Map element = new HashMap();
				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					putExportedKeysPart( element, rs );					
					return element;					
				}
				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(e, "Error while reading exported keys meta data for " + Table.qualify(catalog, schema, table), null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(e, "Error while reading exported keys meta data for " + Table.qualify(xcatalog, xschema, xtable), null);
		}	
	}
	
	protected void putExportedKeysPart(Map element, ResultSet rs) throws SQLException {
		element.put( "PKTABLE_NAME", rs.getString("PKTABLE_NAME"));
		element.put( "PKTABLE_SCHEM", rs.getString("PKTABLE_SCHEM"));
		element.put( "PKTABLE_CAT", rs.getString("PKTABLE_CAT"));
		element.put( "FKTABLE_CAT", rs.getString("FKTABLE_CAT"));
		element.put( "FKTABLE_SCHEM",rs.getString("FKTABLE_SCHEM"));
		element.put( "FKTABLE_NAME", rs.getString("FKTABLE_NAME"));
		element.put( "FKCOLUMN_NAME", rs.getString("FKCOLUMN_NAME"));
		element.put( "PKCOLUMN_NAME", rs.getString("PKCOLUMN_NAME"));
		element.put( "FK_NAME", rs.getString("FK_NAME"));
		element.put( "KEY_SEQ", new Short(rs.getShort("KEY_SEQ")));
	}
	
	
}
