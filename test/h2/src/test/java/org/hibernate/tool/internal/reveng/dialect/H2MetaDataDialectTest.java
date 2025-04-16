/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2004-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.internal.reveng.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class H2MetaDataDialectTest {

	private H2MetaDataDialect mdd = null;
	
	private ConnectionProvider cp = null;

	
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting(AvailableSettings.DIALECT, H2Dialect.class.getName());
		ServiceRegistry sr = ssrb.build();
		cp = sr.getService(ConnectionProvider.class);
		mdd = new H2MetaDataDialect();
		mdd.configure(cp);
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testGetMetaData() throws Exception {
		DatabaseMetaData dbmd = mdd.getMetaData();
		ResultSet ttrs = dbmd.getTableTypes();
		ResultSetMetaData ttrsmd = ttrs.getMetaData();
		for (int i = 1; i <= ttrsmd.getColumnCount(); i++) {
			System.out.println(ttrsmd.getColumnName(i));
		}
		System.out.println("==========");
		while(ttrs.next()) {
			System.out.println(ttrs.getString("TABLE_TYPE"));
		}
	}
	
	@Test
	public void testBlablabla() throws Exception {
		Connection c = cp.getConnection();
		Statement s = c.createStatement();
		System.out.println("INFORMATION_SCHEMA.INDEXES");
		System.out.println("==========================");
		ResultSet rs = s.executeQuery("SELECT * FROM INFORMATION_SCHEMA.INDEXES");
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			System.out.println(rsmd.getColumnName(i));
		}
		System.out.println("==========================");
		System.out.println("INFORMATION_SCHEMA.COLUMNS");
		System.out.println("==========================");
		rs = s.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS");
		rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			System.out.println(rsmd.getColumnName(i));
		}
		System.out.println("===============================");
		System.out.println("SuggestedPrimaryKeyStrategyName");
		System.out.println("===============================");
		rs = s.executeQuery(
				"SELECT " +
				"  idxs.TABLE_CATALOG TABLE_CAT, " +
				"  idxs.TABLE_SCHEMA TABLE_SCHEM, " +
				"  idxs.TABLE_NAME, " +
				"  cols.COLUMN_NAME, " +
				"  cols.COLUMN_DEFAULT " +
				"FROM " + 
				"  INFORMATION_SCHEMA.INDEXES idxs, " + 
				"  INFORMATION_SCHEMA.INDEX_COLUMNS idx_cols, " +
				"  INFORMATION_SCHEMA.COLUMNS cols " +
				"WHERE                                     " +
				"   idxs.TABLE_CATALOG = cols.TABLE_CATALOG AND " + 
				"   idxs.TABLE_SCHEMA = cols.TABLE_SCHEMA   AND " +
				"   idxs.TABLE_NAME = cols.TABLE_NAME AND " + 
				"   idxs.INDEX_TYPE_NAME = 'PRIMARY KEY' AND " +
				"   cols.COLUMN_NAME = idx_cols.COLUMN_NAME AND " +
				"   cols.IS_IDENTITY = 'YES'");
		rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			System.out.println(rsmd.getColumnLabel(i));
		}
		while(rs.next()) {
			System.out.println("TABLE_CAT: " + rs.getString("TABLE_CAT"));
			System.out.println("TABLE_SCHEM: " + rs.getString("TABLE_SCHEM"));
			System.out.println("TABLE_NAME: " + rs.getString("TABLE_NAME"));
			System.out.println("COLUMN_NAME: " + rs.getString("COLUMN_NAME"));
			System.out.println("COLUMN_DEFAULT: " + rs.getString("COLUMN_DEFAULT"));
		}
		s.close();
		c.close();
	}
	
	@Test
	public void testIndexes() throws Exception {
		Connection c = cp.getConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM INFORMATION_SCHEMA.INDEXES");
		while(rs.next()) {
			System.out.println("INDEX_CATALOG: " + rs.getString("INDEX_CATALOG"));
			System.out.println("INDEX_SCHEMA: " + rs.getString("INDEX_SCHEMA"));
			System.out.println("INDEX_NAME: " + rs.getString("INDEX_NAME"));
			System.out.println("TABLE_CATALOG: " + rs.getString("TABLE_CATALOG"));
			System.out.println("TABLE_SCHEMA: " + rs.getString("TABLE_SCHEMA"));
			System.out.println("TABLE_NAME: " + rs.getString("TABLE_NAME"));
			System.out.println("INDEX_TYPE_NAME: " + rs.getString("INDEX_TYPE_NAME"));
			System.out.println("IS_GENERATED: " + rs.getString("IS_GENERATED"));
			System.out.println("REMARKS: " + rs.getString("REMARKS"));
			System.out.println("INDEX_CLASS: " + rs.getString("INDEX_CLASS"));
		}
	}

	@Test
	public void testTables() throws Exception {
		Connection c = cp.getConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES");
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			System.out.println(rsmd.getColumnName(i));
		}
		while(rs.next()) {
			System.out.println("INDEX_CATALOG: " + rs.getString("INDEX_CATALOG"));
			System.out.println("INDEX_SCHEMA: " + rs.getString("INDEX_SCHEMA"));
			System.out.println("INDEX_NAME: " + rs.getString("INDEX_NAME"));
			System.out.println("TABLE_CATALOG: " + rs.getString("TABLE_CATALOG"));
			System.out.println("TABLE_SCHEMA: " + rs.getString("TABLE_SCHEMA"));
			System.out.println("TABLE_NAME: " + rs.getString("TABLE_NAME"));
			System.out.println("INDEX_TYPE_NAME: " + rs.getString("INDEX_TYPE_NAME"));
			System.out.println("IS_GENERATED: " + rs.getString("IS_GENERATED"));
			System.out.println("REMARKS: " + rs.getString("REMARKS"));
			System.out.println("INDEX_CLASS: " + rs.getString("INDEX_CLASS"));
		}
	}
	
	@Test
	public void testColumns() throws Exception {
		Connection c = cp.getConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery(
"				SELECT   idx.TABLE_CATALOG TABLE_CAT,   idx.TABLE_SCHEMA TABLE_SCHEM,   idx.TABLE_NAME,   cols.COLUMN_NAME,   cols.COLUMN_DEFAULT FROM   INFORMATION_SCHEMA.INDEXES idx,   INFORMATION_SCHEMA.INDEX_COLUMNS idx_cols,   INFORMATION_SCHEMA.COLUMNS cols WHERE                                        idx.TABLE_CATALOG = cols.TABLE_CATALOG AND    idx.TABLE_SCHEMA = cols.TABLE_SCHEMA   AND    idx.TABLE_NAME = cols.TABLE_NAME AND    idx.INDEX_TYPE_NAME = 'PRIMARY KEY' AND    cols.COLUMN_NAME = idx_cols.COLUMN_NAME AND    cols.IS_IDENTITY = 'YES'AND idx.TABLE_CATALOG like 'TEST' AND idx.TABLE_SCHEMA like 'PUBLIC' AND idx.TABLE_NAME like 'AUTOINC' "
);
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			System.out.println(rsmd.getColumnName(i));
		}
		while(rs.next()) {
			System.out.println("TABLE_CATALOG: " + rs.getString("TABLE_CATALOG"));
			System.out.println("TABLE_SCHEMA: " + rs.getString("TABLE_SCHEMA"));
			System.out.println("TABLE_NAME: " + rs.getString("TABLE_NAME"));
			System.out.println("COLUMN_NAME: " + rs.getString("COLUMN_NAME"));
			System.out.println("ORDINAL_POSITION: " + rs.getString("ORDINAL_POSITION"));
			System.out.println("IS_NULLABLE: " + rs.getString("IS_NULLABLE"));
			System.out.println("DATA_TYPE: " + rs.getString("DATA_TYPE"));
			System.out.println("CHARACTER_MAXIMUM_LENGTH: " + rs.getString("CHARACTER_MAXIMUM_LENGTH"));
			System.out.println("CHARACTER_OCTET_LENGTH: " + rs.getString("CHARACTER_OCTET_LENGTH"));
			System.out.println("NUMERIC_PRECISION: " + rs.getString("NUMERIC_PRECISION"));
			System.out.println("NUMERIC_PRECISION_RADIX: " + rs.getString("NUMERIC_PRECISION_RADIX"));
			System.out.println("NUMERIC_SCALE: " + rs.getString("NUMERIC_SCALE"));
			System.out.println("DATETIME_PRECISION: " + rs.getString("DATETIME_PRECISION"));
			System.out.println("INTERVAL_TYPE: " + rs.getString("INTERVAL_TYPE"));
			System.out.println("INTERVAL_PRECISION: " + rs.getString("INTERVAL_PRECISION"));
			System.out.println("CHARACTER_SET_CATALOG: " + rs.getString("CHARACTER_SET_CATALOG"));
			System.out.println("CHARACTER_SET_SCHEMA: " + rs.getString("CHARACTER_SET_SCHEMA"));
			System.out.println("CHARACTER_SET_NAME: " + rs.getString("CHARACTER_SET_NAME"));
			System.out.println("COLLATION_CATALOG: " + rs.getString("COLLATION_CATALOG"));
			System.out.println("COLLATION_SCHEMA: " + rs.getString("COLLATION_SCHEMA"));
			System.out.println("COLLATION_NAME: " + rs.getString("COLLATION_NAME"));
			System.out.println("DOMAIN_CATALOG: " + rs.getString("DOMAIN_CATALOG"));
			System.out.println("DOMAIN_SCHEMA: " + rs.getString("DOMAIN_SCHEMA"));
			System.out.println("DOMAIN_NAME: " + rs.getString("DOMAIN_NAME"));
			System.out.println("MAXIMUM_CARDINALITY: " + rs.getString("MAXIMUM_CARDINALITY"));
			System.out.println("DTD_IDENTIFIER: " + rs.getString("DTD_IDENTIFIER"));
			System.out.println("IS_IDENTITY: " + rs.getString("IS_IDENTITY"));
			System.out.println("==========================================");
		}
	}

}
