package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.internal.factory.ForeignKeyWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ForeignKeyWrapperTest {

	private ForeignKeyWrapper foreignKeyWrapper = null; 
	private ForeignKey wrappedForeignKey = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedForeignKey = new ForeignKey();
		foreignKeyWrapper = ForeignKeyWrapperFactory.createForeignKeyWrapper(wrappedForeignKey);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedForeignKey);
		assertNotNull(foreignKeyWrapper);
	}
	
	@Test
	public void testGetReferencedTable() {
		Table table = new Table("");
		wrappedForeignKey.setReferencedTable(table);
		Table t = foreignKeyWrapper.getReferencedTable();
		assertSame(t, table);
	}
	
	@Test
	public void testColumnIterator() {
		Iterator<Column> iterator = foreignKeyWrapper.columnIterator();
		assertFalse(iterator.hasNext());
		Column column = new Column();
		wrappedForeignKey.addColumn(column);
		iterator = foreignKeyWrapper.columnIterator();
		Column c = iterator.next();
		assertSame(c, column);
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void testIsReferenceToPrimaryKey() {
		assertTrue(foreignKeyWrapper.isReferenceToPrimaryKey());
		ArrayList<Column> list = new ArrayList<Column>();
		Column column = new Column();
		list.add(column);
		wrappedForeignKey.addReferencedColumns(list);
		assertFalse(foreignKeyWrapper.isReferenceToPrimaryKey());
	}
	
	@Test
	public void testGetReferencedColumns() {
		List<Column> list = foreignKeyWrapper.getReferencedColumns();
		assertTrue(list.isEmpty());		
		Column column = new Column();
		ArrayList<Column> columns = new ArrayList<Column>();
		columns.add(column);
		wrappedForeignKey.addReferencedColumns(columns);
		list = foreignKeyWrapper.getReferencedColumns();
		assertEquals(1, list.size());
		assertSame(column, list.get(0));
	}
	
	@Test
	public void testContainsColumn() throws Exception {
		Column column = new Column("foo");
		assertFalse(foreignKeyWrapper.containsColumn(column));
		wrappedForeignKey.addColumn(column);
		assertTrue(foreignKeyWrapper.containsColumn(column));
	}
	
}
