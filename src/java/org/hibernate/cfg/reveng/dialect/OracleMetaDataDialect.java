
package org.hibernate.cfg.reveng.dialect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.mapping.Table;

/**
 * Oracle Specialised MetaData dialect that uses standard JDBC and querys on the
 * Data Dictionary for reading metadata.
 * 
 * @author David Channon
 * @author Eric Kershner (added preparedstatements HBX-817)
 *  
 */

public class OracleMetaDataDialect extends AbstractMetaDataDialect {

	
	
	public OracleMetaDataDialect() {
		super();
	}
	
	/* ******* TABLE QUERIES ******* */
	private static final String SQL_TABLE_BASE = 
		    "select  a.table_name, a.owner, b.comments, 'TABLE' "
			+ "from all_tables a left join all_tab_comments b "
			+ "on (a.owner=b.owner and a.table_name=b.table_name) ";

	private static final String SQL_TABLE_VIEW = 
		" union all select view_name, owner, NULL, 'VIEW' from all_views ";

	private static final String SQL_TABLE_NONE = SQL_TABLE_BASE	+ SQL_TABLE_VIEW;

	private static final String SQL_TABLE_SCHEMA = SQL_TABLE_BASE
			+ "where a.owner = ? " + SQL_TABLE_VIEW + " where owner = ?";

	private static final String SQL_TABLE_TABLE = SQL_TABLE_BASE
			+ "where a.table_name = ?" + SQL_TABLE_VIEW + "where view_name = ?";

	private static final String SQL_TABLE_SCHEMA_AND_TABLE = SQL_TABLE_BASE
			+ "where a.owner = ? and a.table_name = ?" + SQL_TABLE_VIEW
			+ "where owner = ? and view_name = ?";

	private PreparedStatement prepTableNone;

	private PreparedStatement prepTableSchema;

	private PreparedStatement prepTableTable;

	private PreparedStatement prepTableSchemaAndTable;

	/* ***************************** */
	/* ******* INDEX QUERIES ******* */
	/* ***************************** */
	private static final String SQL_INDEX_BASE = "select a.column_name, "
			+ "decode(b.uniqueness,'UNIQUE','false','true'), "
			+ "a.index_owner, a.index_name, a.table_name "
			+ "from all_ind_columns a left join all_indexes b on "
			+ "(a.table_name = b.table_name "
			+ " AND a.table_owner = b.table_owner "
			+ " AND a.index_name  = b.index_name) ";

	private static final String SQL_INDEX_ORDER = " order by a.table_name, a.column_position";

	private static final String SQL_INDEX_NONE = SQL_INDEX_BASE
			+ SQL_INDEX_ORDER;

	private static final String SQL_INDEX_SCHEMA = SQL_INDEX_BASE
			+ "where a.table_owner = ? " + SQL_INDEX_ORDER;

	private static final String SQL_INDEX_TABLE = SQL_INDEX_BASE
			+ "where a.table_name = ? " + SQL_INDEX_ORDER;

	private static final String SQL_INDEX_SCHEMA_AND_TABLE = SQL_INDEX_BASE
			+ "where a.table_owner = ? and a.table_name = ? " + SQL_INDEX_ORDER;

	private PreparedStatement prepIndexNone;

	private PreparedStatement prepIndexSchema;

	private PreparedStatement prepIndexTable;

	private PreparedStatement prepIndexSchemaAndTable;

	/* ****** COLUMN QUERIES ******* */	
	private static final String SQL_COLUMN_BASE = "select a.column_name as COLUMN_NAME, a.owner as TABLE_SCHEM, "
			+ "decode(a.nullable,'N',0,1) as NULLABLE, "
			+ "decode(a.data_type, 'FLOAT',decode(a.data_precision,null, "
			+ "a.data_length, a.data_precision), 'NUMBER', decode(a.data_precision,null, "
			+ "a.data_length, a.data_precision), a.data_length) as COLUMN_SIZE, "
			+ "decode(a.data_type,'CHAR',1, 'DATE',91, 'FLOAT',6, "
			+ "'LONG',-1, 'NUMBER',2, 'VARCHAR2',12, 'BFILE',-13, "
			+ "'BLOB',2004, 'CLOB',2005, 'MLSLABEL',1111, 'NCHAR',1, 'NCLOB',2005, 'NVARCHAR2',12, "
			+ "'RAW',-3, 'ROWID',1111, 'UROWID',1111, 'LONG RAW', -4, "
			+ "'TIMESTAMP', 93, 'XMLTYPE',2005, 1111) as DATA_TYPE, "
			+ "a.table_name as TABLE_NAME, a.data_type as TYPE_NAME, "
			+ "decode(a.data_scale, null, 0 ,a.data_scale) as DECIMAL_DIGITS, b.comments "
			+ "from all_tab_columns a left join all_col_comments b on "
			+ "(a.owner=b.owner and a.table_name=b.table_name and a.column_name=b.column_name) ";

	private static final String SQL_COLUMN_ORDER = " order by column_id ";

	private static final String SQL_COLUMN_NONE = SQL_COLUMN_BASE
			+ SQL_COLUMN_ORDER;

	private static final String SQL_COLUMN_SCHEMA = SQL_COLUMN_BASE
			+ "where a.owner = ? " + SQL_COLUMN_ORDER;

	private static final String SQL_COLUMN_TABLE = SQL_COLUMN_BASE
			+ "where a.table_name = ? " + SQL_COLUMN_ORDER;

	private static final String SQL_COLUMN_COLUMN = SQL_COLUMN_BASE
			+ "where a.column_name = ? " + SQL_COLUMN_ORDER;

	private static final String SQL_COLUMN_SCHEMA_AND_TABLE = SQL_COLUMN_BASE
			+ "where a.owner = ? and a.table_name = ? " + SQL_COLUMN_ORDER;

	private static final String SQL_COLUMN_SCHEMA_AND_COLUMN = SQL_COLUMN_BASE
			+ "where a.owner = ? and a.column_name = ? " + SQL_COLUMN_ORDER;

	private static final String SQL_COLUMN_TABLE_AND_COLUMN = SQL_COLUMN_BASE
			+ "where a.table_name = ? and a.column_name = ? "
			+ SQL_COLUMN_ORDER;

	private static final String SQL_COLUMN_SCHEMA_AND_TABLE_AND_COLUMN = SQL_COLUMN_BASE
			+ "where a.owner = ? and a.table_name = ? and a.column_name = ? "
			+ SQL_COLUMN_ORDER;

	private PreparedStatement prepColumnNone;

	private PreparedStatement prepColumnSchema;

	private PreparedStatement prepColumnTable;

	private PreparedStatement prepColumnColumn;

	private PreparedStatement prepColumnSchemaAndTable;

	private PreparedStatement prepColumnSchemaAndColumn;

	private PreparedStatement prepColumnTableAndColumn;

	private PreparedStatement prepColumnSchemaAndTableAndColumn;

	/* ***************************** */
	/* ******** PK QUERIES ********* */
	/* ***************************** */
	private static final String SQL_PK_BASE = "select c.table_name, c.column_name, c.position,  c.constraint_name, "
			+ "c.owner from all_cons_columns c left join all_constraints k on "
			+ "(k.owner = c.owner AND k.table_name = c.table_name AND k.constraint_name = c.constraint_name) "
			+ "where  k.constraint_type = 'P' ";

	private static final String SQL_PK_ORDER = " order by c.table_name, c.constraint_name, c.position desc ";

	private static final String SQL_PK_NONE = SQL_PK_BASE + SQL_PK_ORDER;

	private static final String SQL_PK_SCHEMA = SQL_PK_BASE
			+ " and c.owner = ? " + SQL_PK_ORDER;

	private static final String SQL_PK_TABLE = SQL_PK_BASE
			+ " and c.table_name = ? " + SQL_PK_ORDER;

	private static final String SQL_PK_SCHEMA_AND_TABLE = SQL_PK_BASE
			+ " and c.owner = ? and c.table_name = ? " + SQL_PK_ORDER;

	private PreparedStatement prepPkNone;

	private PreparedStatement prepPkSchema;

	private PreparedStatement prepPkTable;

	private PreparedStatement prepPkSchemaAndTable;

	/* ***************************** */
	/* ******** FK QUERIES ********* */
	/* ***************************** */
	private static final String SQL_FK_BASE = "select p.table_name, p.owner, f.owner, f.table_name, "
			+ "fc.column_name, pc.column_name, f.constraint_name, fc.position "
			+ "from all_constraints p left join all_cons_columns pc on "
			+ "(pc.owner = p.owner and pc.constraint_name = p.constraint_name and pc.table_name = p.table_name) "
			+ "left join all_constraints f on (p.owner = f.r_owner and p.constraint_name = f.r_constraint_name) "
			+ "left join all_cons_columns fc on "
			+ "(fc.owner = f.owner and fc.constraint_name = f.constraint_name and fc.table_name = f.table_name and"
			+ " fc.position = pc.position) where f.constraint_type = 'R' AND  p.constraint_type = 'P' ";

	private static final String SQL_FK_ORDER = " order by f.table_name, f.constraint_name, fc.position ";

	private static final String SQL_FK_NONE = SQL_FK_BASE + SQL_FK_ORDER;

	private static final String SQL_FK_SCHEMA = SQL_FK_BASE
			+ " and p.owner = ? " + SQL_FK_ORDER;

	private static final String SQL_FK_TABLE = SQL_FK_BASE
			+ " and p.table_name = ? " + SQL_FK_ORDER;

	private static final String SQL_FK_SCHEMA_AND_TABLE = SQL_FK_BASE
			+ " and p.owner = ? and p.table_name = ? " + SQL_FK_ORDER;

	private PreparedStatement prepFkNone;

	private PreparedStatement prepFkSchema;

	private PreparedStatement prepFkTable;

	private PreparedStatement prepFkSchemaAndTable;
	
	public Iterator getTables(final String catalog, final String schema,
			String table) {
		try {
			log.debug("getTables(" + catalog + "." + schema + "." + table + ")");
			
			ResultSet tableRs;
			tableRs = getTableResultSet( schema, table );

			return new ResultSetIterator(null, tableRs,
					getSQLExceptionConverter()) {

				Map element = new HashMap();

				
				protected Object convertRow(ResultSet tableResultSet)
						throws SQLException {
					element.clear();
					element.put("TABLE_NAME", tableResultSet.getString(1));
					element.put("TABLE_SCHEM", tableResultSet.getString(2));
					element.put("TABLE_CAT", null);
					element.put("TABLE_TYPE", tableResultSet.getString(4));
					element.put("REMARKS", tableResultSet.getString(3));
					log.info( element.toString() );
					return element;
				}

				protected Throwable handleSQLException(SQLException e) {
					// schemaRs and catalogRs are only used for error reporting
					// if
					// we get an exception
					String databaseStructure = getDatabaseStructure(catalog,
							schema);
					throw getSQLExceptionConverter().convert(
							e,
							"Could not get list of tables from database. Probably a JDBC driver problem. "
									+ databaseStructure, null);
				}
			};
		} catch (SQLException e) {
			// schemaRs and catalogRs are only used for error reporting if we
			// get an exception
			String databaseStructure = getDatabaseStructure(catalog, schema);
			throw getSQLExceptionConverter().convert(
					e,
					"Could not get list of tables from database. Probably a JDBC driver problem. "
							+ databaseStructure, null);
		}
	}
	
	public Iterator getIndexInfo(final String catalog, final String schema,
			final String table) {
		try {
			log.debug("getIndexInfo(" + catalog + "." + schema + "." + table + ")");

			ResultSet indexRs;
			indexRs = getIndexInfoResultSet( schema, table );

			return new ResultSetIterator(null, indexRs,
					getSQLExceptionConverter()) {

				Map element = new HashMap();

				
				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					element.put("COLUMN_NAME", rs.getString(1));
					element.put("TYPE", new Short((short) 1)); // CLUSTERED
																// INDEX
					element.put("NON_UNIQUE", Boolean.valueOf(rs.getString(2)));
					element.put("TABLE_SCHEM", rs.getString(3));
					element.put("INDEX_NAME", rs.getString(4));
					element.put("TABLE_CAT", null);
					element.put("TABLE_NAME", rs.getString(5));

					return element;
				}

				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(
							e,
							"Exception while getting index info for "
									+ Table.qualify(catalog, schema, table),
							null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(
					e,
					"Exception while getting index info for "
							+ Table.qualify(catalog, schema, table), null);
		}
	}

	public Iterator getColumns(final String catalog, final String schema,
			final String table, String column) {
		
		try {
			log.debug("getColumns(" + catalog + "." + schema + "." + table + "." + column + ")");

			ResultSet columnRs;
			columnRs = getColumnsResultSet( schema, table, column );

			return new ResultSetIterator(null, columnRs,
					getSQLExceptionConverter()) {

				Map element = new HashMap();

				
				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					element.put("COLUMN_NAME", rs.getString(1));
					element.put("TABLE_SCHEM", rs.getString(2));
					element.put("NULLABLE", new Integer(rs.getInt(3)));
					element.put("COLUMN_SIZE", new Integer(rs.getInt(4)));
					element.put("DATA_TYPE", new Integer(rs.getInt(5)));
					element.put("TABLE_NAME", rs.getString(6));
					element.put("TYPE_NAME", rs.getString(7));
					element.put("DECIMAL_DIGITS", new Integer(rs.getInt(8)));
					element.put("TABLE_CAT", null);
					element.put("REMARKS", rs.getString(9));
					return element;
				}

				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(
							e,
							"Error while reading column meta data for "
									+ Table.qualify(catalog, schema, table),
							null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(
					e,
					"Error while reading column meta data for "
							+ Table.qualify(catalog, schema, table), null);
		}
	}

	public Iterator getPrimaryKeys(final String catalog, final String schema,
			final String table) {
		
		try {
			log.debug("getPrimaryKeys(" + catalog + "." + schema + "." + table
					+ ")");

			ResultSet pkeyRs;
			pkeyRs = getPrimaryKeysResultSet( schema, table );

			return new ResultSetIterator(null, pkeyRs,
					getSQLExceptionConverter()) {

				Map element = new HashMap();

				
				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					element.put("TABLE_NAME", rs.getString(1));
					element.put("COLUMN_NAME", rs.getString(2));
					element.put("KEY_SEQ", new Short(rs.getShort(3)));
					element.put("PK_NAME", rs.getString(4));
					element.put("TABLE_SCHEM", rs.getString(5));
					element.put("TABLE_CAT", null);
					return element;
				}

				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(
							e,
							"Error while reading primary key meta data for "
									+ Table.qualify(catalog, schema, table),
							null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(
					e,
					"Error while reading primary key meta data for "
							+ Table.qualify(catalog, schema, table), null);
		}
	}

	public Iterator getExportedKeys(final String catalog, final String schema,
			final String table) {
		
		try {
			log.debug("getExportedKeys(" + catalog + "." + schema + "." + table
					+ ")");

			ResultSet pExportRs;
			pExportRs = getExportedKeysResultSet( schema, table );

			return new ResultSetIterator(null, pExportRs,
					getSQLExceptionConverter()) {

				Map element = new HashMap();

				
				protected Object convertRow(ResultSet rs) throws SQLException {
					element.clear();
					element.put("PKTABLE_NAME", rs.getString(1));
					element.put("PKTABLE_SCHEM", rs.getString(2));
					element.put("PKTABLE_CAT", null);
					element.put("FKTABLE_CAT", null);
					element.put("FKTABLE_SCHEM", rs.getString(3));
					element.put("FKTABLE_NAME", rs.getString(4));
					element.put("FKCOLUMN_NAME", rs.getString(5));
					element.put("PKCOLUMN_NAME", rs.getString(6));
					element.put("FK_NAME", rs.getString(7));
					element.put("KEY_SEQ", new Short(rs.getShort(8)));
					return element;
				}

				protected Throwable handleSQLException(SQLException e) {
					throw getSQLExceptionConverter().convert(
							e,
							"Error while reading exported keys meta data for "
									+ Table.qualify(catalog, schema, table),
							null);
				}
			};
		} catch (SQLException e) {
			throw getSQLExceptionConverter().convert(
					e,
					"Error while reading exported keys meta data for "
							+ Table.qualify(catalog, schema, table), null);
		}
	}	
	
	public void close() {
		try {
			prepTableNone = close( prepTableNone );
			prepTableSchema = close( prepTableSchema );
			prepTableTable = close( prepTableTable );
			prepTableSchemaAndTable = close( prepTableSchemaAndTable );
			prepIndexNone = close( prepIndexNone );
			prepIndexSchema = close( prepIndexSchema );
			prepIndexTable = close( prepIndexTable );
			prepIndexSchemaAndTable = close( prepIndexSchemaAndTable );
			prepColumnNone = close( prepColumnNone );
			prepColumnSchema = close( prepColumnSchema );
			prepColumnTable = close( prepColumnTable );
			prepColumnColumn = close( prepColumnColumn );
			prepColumnSchemaAndTable = close( prepColumnSchemaAndTable );
			prepColumnSchemaAndColumn = close( prepColumnSchemaAndColumn );
			prepColumnTableAndColumn = close( prepColumnTableAndColumn );
			prepColumnSchemaAndTableAndColumn = close( prepColumnSchemaAndTableAndColumn );
			prepPkNone = close( prepPkNone );
			prepPkSchema = close( prepPkSchema );
			prepPkTable = close( prepPkTable );
			prepPkSchemaAndTable = close( prepPkSchemaAndTable );
			prepFkNone = close( prepFkNone );
			prepFkSchema = close( prepFkSchema );
			prepFkTable = close( prepFkTable );
			prepFkSchemaAndTable = close( prepFkSchemaAndTable );
		}
		finally {
			super.close();
		}
	}

	private PreparedStatement close(PreparedStatement ps) {
		if(ps==null) {
			return null;
		} else {
			try {
				ps.close();
			}
			catch (SQLException e) {
				throw getSQLExceptionConverter().convert(e,
						"Problem while closing prepared statement", null);				
			}
			return null;
		}
		
	}

	private ResultSet getPrimaryKeysResultSet(final String schema, final String table) throws SQLException {
		if(prepPkNone==null) {
			// Prepare primary key queries
			log.debug("Preparing primary key queries...");
			Connection con = getConnection();
			prepPkNone = con .prepareStatement(SQL_PK_NONE);
			prepPkSchema = con.prepareStatement(SQL_PK_SCHEMA);
			prepPkTable = con.prepareStatement(SQL_PK_TABLE);
			prepPkSchemaAndTable = con
			.prepareStatement(SQL_PK_SCHEMA_AND_TABLE);
			log.debug("  primary key queries prepared!");
		}
		
		ResultSet pkeyRs;
		if (schema == null && table == null) {
			pkeyRs = prepPkNone.executeQuery();
		} else if (schema != null) {
			if (table == null) {
				prepPkSchema.setString(1, schema);
				pkeyRs = prepPkSchema.executeQuery();
			} else {
				prepPkSchemaAndTable.setString(1, schema);
				prepPkSchemaAndTable.setString(2, table);
				pkeyRs = prepPkSchemaAndTable.executeQuery();
			}
		} else {
			prepPkTable.setString(1, table);
			pkeyRs = prepPkTable.executeQuery();
		}
		return pkeyRs;
	}

	private ResultSet getIndexInfoResultSet(final String schema, final String table) throws SQLException {
		if(prepIndexNone==null) {
			//	Prepare index queries
			log.debug("Preparing index queries...");
			Connection con = getConnection();
			prepIndexNone = con.prepareStatement(SQL_INDEX_NONE);
			prepIndexSchema = con.prepareStatement(SQL_INDEX_SCHEMA);
			prepIndexTable = con.prepareStatement(SQL_INDEX_TABLE);
			prepIndexSchemaAndTable = con.prepareStatement(SQL_INDEX_SCHEMA_AND_TABLE);
			log.debug("  ...index queries prepared!");			
		}
		ResultSet indexRs;
		if (schema == null && table == null) {
			indexRs = prepIndexNone.executeQuery();
		} else if (schema != null) {
			if (table == null) {
				prepIndexSchema.setString(1, schema);
				indexRs = prepIndexSchema.executeQuery();
			} else {
				prepIndexSchemaAndTable.setString(1, schema);
				prepIndexSchemaAndTable.setString(2, table);
				indexRs = prepIndexSchemaAndTable.executeQuery();
			}
		} else {
			prepIndexTable.setString(1, table);
			indexRs = prepIndexTable.executeQuery();
		}
		return indexRs;
	}

	private ResultSet getExportedKeysResultSet(final String schema, final String table) throws SQLException {
		if(prepFkNone==null) {
			//	Prepare foreign key queries
			log.debug("Preparing foreign key queries...");
			Connection con = getConnection();
			prepFkNone = con .prepareStatement(SQL_FK_NONE);
			prepFkSchema = con.prepareStatement(SQL_FK_SCHEMA);
			prepFkTable = con.prepareStatement(SQL_FK_TABLE);
			prepFkSchemaAndTable = con
					.prepareStatement(SQL_FK_SCHEMA_AND_TABLE);
			log.debug("  foreign key queries prepared!");
		}
		
		ResultSet pExportRs;
		if (schema == null && table == null) {
			pExportRs = prepFkNone.executeQuery();
		} else if (schema != null) {
			if (table == null) {
				prepFkSchema.setString(1, schema);
				pExportRs = prepFkSchema.executeQuery();
			} else {
				prepFkSchemaAndTable.setString(1, schema);
				prepFkSchemaAndTable.setString(2, table);
				pExportRs = prepFkSchemaAndTable.executeQuery();
			}
		} else {
			prepFkTable.setString(1, table);
			pExportRs = prepFkTable.executeQuery();
		}
		return pExportRs;
	}
	private ResultSet getColumnsResultSet(final String schema, final String table, String column) throws SQLException {
		
		if(prepColumnNone==null) {			
			// Prepare column queries
			log.debug("Preparing column queries...");
			Connection con = getConnection();
			prepColumnNone = con.prepareStatement(SQL_COLUMN_NONE);
			prepColumnSchema = con.prepareStatement(SQL_COLUMN_SCHEMA);
			prepColumnTable = con.prepareStatement(SQL_COLUMN_TABLE);
			prepColumnColumn = con.prepareStatement(SQL_COLUMN_COLUMN);
			prepColumnSchemaAndTable = con.prepareStatement(SQL_COLUMN_SCHEMA_AND_TABLE);
			prepColumnSchemaAndColumn = con.prepareStatement(SQL_COLUMN_SCHEMA_AND_COLUMN);
			prepColumnTableAndColumn = con.prepareStatement(SQL_COLUMN_TABLE_AND_COLUMN);
			prepColumnSchemaAndTableAndColumn = con.prepareStatement(SQL_COLUMN_SCHEMA_AND_TABLE_AND_COLUMN);
			log.debug("  ...column queries prepared!");
		}
		
		ResultSet columnRs;
		// No parameters specified
		if (schema == null && table == null && column == null) {
			columnRs = prepColumnNone.executeQuery();
		} else if (schema != null) {
			if (table == null) {
				if (column == null) {
					// Schema specified
					prepColumnSchema.setString(1, schema);
					columnRs = prepColumnSchema.executeQuery();
				} else {
					// Schema and column specified
					prepColumnSchemaAndColumn.setString(1, schema);
					prepColumnSchemaAndColumn.setString(2, column);
					columnRs = prepColumnSchemaAndColumn.executeQuery();
				}
			} else {
				if (column == null) {
					// Schema and table specified
					prepColumnSchemaAndTable.setString(1, schema);
					prepColumnSchemaAndTable.setString(2, table);
					columnRs = prepColumnSchemaAndTable.executeQuery();
				} else {
					// Schema, table and column specified
					prepColumnSchemaAndTableAndColumn.setString(1, schema);
					prepColumnSchemaAndTableAndColumn.setString(2, table);
					prepColumnSchemaAndTableAndColumn.setString(3, column);
					columnRs = prepColumnSchemaAndTableAndColumn.executeQuery();
				}
			}
		} else {
			if (table == null) {
				// Column specified
				prepColumnColumn.setString(1, column);
				columnRs = prepColumnColumn.executeQuery();
			} else {
				if (column == null) {
					// Table specified
					prepColumnTable.setString(1, table);
					columnRs = prepColumnTable.executeQuery();
				} else {
					// Table and column specified
					prepColumnTableAndColumn.setString(1, table);
					prepColumnTableAndColumn.setString(2, column);
					columnRs = prepColumnTableAndColumn.executeQuery();
	
				}
			}
		}
		return columnRs;
	}

	private ResultSet getTableResultSet(final String schema, String table) throws SQLException {
		ResultSet tableRs;
		if(prepTableNone==null) {
			// Prepare table queries
			log.debug("Preparing table queries...");
			Connection connection2 = getConnection();
			prepTableNone = connection2.prepareStatement(SQL_TABLE_NONE);
			prepTableSchema = connection2.prepareStatement(SQL_TABLE_SCHEMA);
			prepTableTable = connection2.prepareStatement(SQL_TABLE_TABLE);
			prepTableSchemaAndTable = connection2.prepareStatement(SQL_TABLE_SCHEMA_AND_TABLE);
			log.debug("  ...table queries prepared!");
		}
		if (schema == null && table == null) {
			tableRs = prepTableNone.executeQuery();
		} else if (schema != null) {
			if (table == null) {
				prepTableSchema.setString(1, schema);
				prepTableSchema.setString(2, schema);
				tableRs = prepTableSchema.executeQuery();
			} else {
				prepTableSchemaAndTable.setString(1, schema);
				prepTableSchemaAndTable.setString(2, table);
				prepTableSchemaAndTable.setString(3, schema);
				prepTableSchemaAndTable.setString(4, table);
				tableRs = prepTableSchemaAndTable.executeQuery();
			}
		} else {
			prepTableTable.setString(1, table);
			prepTableTable.setString(2, table);
			tableRs = prepTableTable.executeQuery();
		}
		return tableRs;
	}

}
