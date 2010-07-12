/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

/**
 * @author max
 * 
 */
public class BasicMultiSchemaTest extends JDBCMetaDataBinderTestCase {

	/**
	 * @return
	 */
	protected String[] getDropSQL() {
		return new String[] { 
		        "drop table basic", 
		        "drop table somecolumnsnopk",
				"drop table multikeyed",
				"drop table otherschema.basic",
				"drop schema otherschema"};
	}

	/**
	 * @return
	 */
	protected String[] getCreateSQL() {

		return new String[] {
				"create table basic ( a int not null, name varchar(20), primary key (a)  )",
				"create table somecolumnsnopk ( pk varchar(25) not null, b char, c int not null, aBoolean boolean )",
				"create table multikeyed ( orderid varchar(10), customerid varchar(10), name varchar(10), primary key(orderid, customerid) )",
				"create schema otherschema authorization dba",
				"create table otherschema.basic ( a int not null, name varchar(20), primary key (a)  )",
				};
	}

	public void testBasic() throws SQLException {

		assertHasNext( "There should be three tables!", 3, cfg
				.getTableMappings() );

		Table table = getTable( identifier( "basic" ) );

		assertEqualIdentifiers( "basic", table.getName() );
		assertEquals( 2, table.getColumnSpan() );

		Column basicColumn = table.getColumn( 0 );
		assertEqualIdentifiers( "a", basicColumn.getName() );
		// TODO: we cannot call getSqlType(dialect,cfg) without a
		// MappingassertEquals("INTEGER", basicColumn.getSqlType() ); // at
		// least on hsqldb
		// assertEquals(22, basicColumn.getLength() ); // at least on oracle

		PrimaryKey key = table.getPrimaryKey();
		assertNotNull( "There should be a primary key!", key );
		assertEquals( key.getColumnSpan(), 1 );

		Column column = key.getColumn( 0 );
		assertTrue( column.isUnique() );

		assertSame( basicColumn, column );

	}

	public void testScalePrecisionLength() {

		Table table = getTable( identifier( "basic" ) );

		Column nameCol = table.getColumn( new Column( identifier( "name" ) ) );
		assertEquals( nameCol.getLength(), 20 );
		assertEquals( nameCol.getPrecision(), Column.DEFAULT_PRECISION );
		assertEquals( nameCol.getScale(), Column.DEFAULT_SCALE );
	}

	
/*	public void testAutoDetectSingleSchema() {
		
		//read single schema without default schema: result = no schema info in tables.
		JDBCMetaDataConfiguration mycfg = new JDBCMetaDataConfiguration();
		mycfg.setReverseEngineeringStrategy(new DelegatingReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy()) {
			public boolean excludeTable(TableIdentifier ti) {
				return !"PUBLIC".equals(ti.getSchema());				
			}
		});
		mycfg.getProperties().remove(org.hibernate.cfg.Environment.DEFAULT_SCHEMA);
		mycfg.readFromJDBC();			
		
		Table table = getTable(mycfg, identifier("otherschema"));
		assertNull("rev.eng.strategy should have excluded this table",table);
		
		table = getTable(mycfg, identifier("basic"));
		assertNotNull(table);
		assertNull(table.getSchema());
		
		
		//read single schema with default schema: result = no schema info in tables.
		
		//read other single schema than default schema: result = schema info in tables.
		
	}*/
	
	/*
	 * public void testGetTables() {
	 * 
	 * Table table = new Table(); table.setName("dummy"); cfg.addTable(table);
	 * 
	 * Table foundTable = cfg.getTable(null,null,"dummy");
	 * 
	 * assertSame(table,foundTable);
	 * 
	 * foundTable = cfg.getTable(null,"dschema", "dummy");
	 * 
	 * assertNotSame(table, foundTable); }
	 */

	public void testCompositeKeys() {

		Table table = getTable( identifier( "multikeyed" ) );

		PrimaryKey primaryKey = table.getPrimaryKey();

		assertEquals( 2, primaryKey.getColumnSpan() );
	}

	public static Test suite() {
		return new TestSuite( BasicMultiSchemaTest.class );
	}

}
