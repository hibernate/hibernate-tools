package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.internal.factory.ColumnWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.PrimaryKeyWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrimaryKeyWrapperTest {
	
	private PrimaryKeyWrapper primaryKeyWrapper;
	private PrimaryKey wrappedPrimaryKey;
	
	@BeforeEach
	public void beforeEach() {
		wrappedPrimaryKey = new PrimaryKey(new Table(""));
		primaryKeyWrapper = PrimaryKeyWrapperFactory.createPrimaryKeyWrapper(wrappedPrimaryKey);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedPrimaryKey);
		assertNotNull(primaryKeyWrapper);
	}
	
	@Test
	public void testAddColumn() throws Exception {
		Column column = new Column("foo");
		ColumnWrapper columnWrapper = ColumnWrapperFactory.createColumnWrapper(column);
		assertTrue(wrappedPrimaryKey.getColumns().isEmpty());
		primaryKeyWrapper.addColumn(columnWrapper);
		assertEquals(1, wrappedPrimaryKey.getColumns().size());
		assertSame(column, wrappedPrimaryKey.getColumns().get(0));
	}
	
	@Test
	public void testGetColumnSpan() {
		assertEquals(0, primaryKeyWrapper.getColumnSpan());
		wrappedPrimaryKey.addColumn(new Column());
		assertEquals(1, primaryKeyWrapper.getColumnSpan());
	}
	
	@Test
	public void testGetColumns() throws Exception {
		Column column = new Column("foo");
		assertTrue(primaryKeyWrapper.getColumns().isEmpty());
		wrappedPrimaryKey.addColumn(column);
		List<ColumnWrapper> columns = primaryKeyWrapper.getColumns();
		assertNotNull(columns);
		assertEquals(1, columns.size());
		assertSame(column, columns.get(0).getWrappedObject());
	}
	
	@Test
	public void testGetColumn() throws Exception {
		try {
			primaryKeyWrapper.getColumn(0);
			fail();
		} catch (IndexOutOfBoundsException e) {
			assertTrue(e.getMessage().contains("Index 0 out of bounds for length 0"));
		}
		Column column = new Column();
		wrappedPrimaryKey.addColumn(column);
		ColumnWrapper c = primaryKeyWrapper.getColumn(0);
		assertNotNull(c);
		assertSame(column, c.getWrappedObject());
	}
	
	@Test
	public void testGetTable() throws Exception {
		Table table = new Table("foo");
		assertNotSame(table, primaryKeyWrapper.getTable().getWrappedObject());
		wrappedPrimaryKey.setTable(table);
		assertSame(table, primaryKeyWrapper.getTable().getWrappedObject());
	}
	
	@Test
	public void testContainsColumn() {
		Column column = new Column("foo");
		ColumnWrapper columnWrapper = ColumnWrapperFactory.createColumnWrapper(column);
		assertFalse(primaryKeyWrapper.containsColumn(columnWrapper));
		wrappedPrimaryKey.addColumn(column);
		assertTrue(primaryKeyWrapper.containsColumn(columnWrapper));
	}
	
	@Test
	public void testColumnIterator() throws Exception {
		assertFalse(primaryKeyWrapper.columnIterator().hasNext());
		Column column = new Column();
		wrappedPrimaryKey.addColumn(column);
		Iterator<ColumnWrapper> columnIterator = primaryKeyWrapper.columnIterator();
		assertTrue(columnIterator.hasNext());
		assertSame(column, columnIterator.next().getWrappedObject());
	}
	
	@Test
	public void testGetName() {
		assertNotEquals("foo", primaryKeyWrapper.getName());
		wrappedPrimaryKey.setName("foo");
		assertEquals("foo", primaryKeyWrapper.getName());
	}
	
}
