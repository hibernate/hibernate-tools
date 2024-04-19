package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.internal.factory.TableWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableWrapperTest {

	private Table wrappedTable = null;
	private TableWrapper tableWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedTable = new Table("Hibernate Tools", "foo");
		tableWrapper = TableWrapperFactory.createTableWrapper(wrappedTable);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedTable);
		assertNotNull(tableWrapper);
	}
	
	@Test
	public void testGetName() {
		assertEquals("foo", tableWrapper.getName());
		wrappedTable.setName("bar");
		assertEquals("bar", tableWrapper.getName());
	}
	
	@Test
	public void testAddColumn() {
		Column column = new Column("foo");
		assertNull(wrappedTable.getColumn(column));
		tableWrapper.addColumn(column);
		assertSame(column, wrappedTable.getColumn(column));
	}
	
	@Test
	public void testGetCatalog() {
		assertNull(tableWrapper.getCatalog());
		wrappedTable.setCatalog("foo");
		assertEquals("foo", tableWrapper.getCatalog());
	}
	
	@Test
	public void testGetSchema() {
		assertNull(tableWrapper.getSchema());
		wrappedTable.setSchema("foo");
		assertEquals("foo", tableWrapper.getSchema());
	}
	
	@Test
	public void testGetPrimaryKey() {
		PrimaryKey primaryKey = new PrimaryKey(wrappedTable);
		assertNull(tableWrapper.getPrimaryKey());
		wrappedTable.setPrimaryKey(primaryKey);
		assertSame(primaryKey, tableWrapper.getPrimaryKey());
	}
	
	@Test
	public void testGetColumnIterator() {
		Column column = new Column("Hibernate Tools");
		Iterator<Column> columnIterator = tableWrapper.getColumnIterator();
		assertFalse(columnIterator.hasNext());
		wrappedTable.addColumn(column);
		columnIterator = tableWrapper.getColumnIterator();
		assertTrue(columnIterator.hasNext());
		assertSame(column, columnIterator.next());
	}
	
	@Test
	public void testGetComment() {
		assertNull(tableWrapper.getComment());
		wrappedTable.setComment("foo");
		assertEquals("foo", tableWrapper.getComment());
	}
	
	@Test
	public void testGetRowId() {
		assertNull(tableWrapper.getRowId());
		wrappedTable.setRowId("foo");
		assertEquals("foo", tableWrapper.getRowId());
	}
	
	@Test
	public void testGetSubselect() {
		assertNull(tableWrapper.getSubselect());		
		wrappedTable.setSubselect("foo");
		assertEquals("foo", tableWrapper.getSubselect());
	}
	
	@Test
	public void testHasDenormalizedTables() throws Exception {
		assertFalse(tableWrapper.hasDenormalizedTables());
		Method method = Table.class.getDeclaredMethod(
				"setHasDenormalizedTables", 
				new Class[] { });
		method.setAccessible(true);
		method.invoke(wrappedTable, new Object[] { });
		assertTrue(tableWrapper.hasDenormalizedTables());
	}
	
	@Test
	public void testIsAbstract() {
		wrappedTable.setAbstract(true);
		assertTrue(tableWrapper.isAbstract());		
		wrappedTable.setAbstract(false);
		assertFalse(tableWrapper.isAbstract());		
	}
	
	@Test
	public void testIsAbstractUnionTable() throws Exception {
		wrappedTable.setAbstract(false);
		assertFalse(tableWrapper.isAbstractUnionTable());	
		wrappedTable.setAbstract(true);
		assertFalse(tableWrapper.isAbstractUnionTable());	
		Method method = Table.class.getDeclaredMethod(
				"setHasDenormalizedTables", 
				new Class[] { });
		method.setAccessible(true);
		method.invoke(wrappedTable, new Object[] { });
		assertTrue(tableWrapper.isAbstractUnionTable());
	}
	
}
