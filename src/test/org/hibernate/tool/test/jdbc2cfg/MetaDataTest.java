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
public class MetaDataTest extends JDBCMetaDataBinderTestCase {

	/**
	 * @return
	 */
	protected String[] getDropSQL() {
		return new String[] { 
		        "drop table basic", 
		        "drop table somecolumnsnopk",
				"drop table multikeyed" };
	}

	/**
	 * @return
	 */
	protected String[] getCreateSQL() {

		return new String[] {
				"create table basic ( a int not null, name varchar(20), primary key (a)  )",
				"create table somecolumnsnopk ( pk varchar(25) not null, b char, c int not null, aBoolean boolean )",
				"create table multikeyed ( orderid varchar(10), customerid varchar(10), name varchar(10), primary key(orderid, customerid) )" };
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
		return new TestSuite( MetaDataTest.class );
	}

}
