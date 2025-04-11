/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
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
package org.hibernate.tool.orm.jbt.api.wrp;

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
import org.hibernate.tool.orm.jbt.internal.factory.ColumnWrapperFactory;
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
		TableWrapper tableWrapper = foreignKeyWrapper.getReferencedTable();
		assertSame(tableWrapper.getWrappedObject(), table);
	}
	
	@Test
	public void testColumnIterator() {
		Iterator<ColumnWrapper> iterator = foreignKeyWrapper.columnIterator();
		assertFalse(iterator.hasNext());
		Column column = new Column();
		wrappedForeignKey.addColumn(column);
		iterator = foreignKeyWrapper.columnIterator();
		ColumnWrapper columnWrapper = iterator.next();
		assertSame(columnWrapper.getWrappedObject(), column);
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
		List<ColumnWrapper> list = foreignKeyWrapper.getReferencedColumns();
		assertTrue(list.isEmpty());		
		Column column = new Column();
		ArrayList<Column> columns = new ArrayList<Column>();
		columns.add(column);
		wrappedForeignKey.addReferencedColumns(columns);
		list = foreignKeyWrapper.getReferencedColumns();
		assertEquals(1, list.size());
		assertSame(column, list.get(0).getWrappedObject());
	}
	
	@Test
	public void testContainsColumn() throws Exception {
		Column column = new Column("foo");
		ColumnWrapper columnWrapper = ColumnWrapperFactory.createColumnWrapper(column);
		assertFalse(foreignKeyWrapper.containsColumn(columnWrapper));
		wrappedForeignKey.addColumn(column);
		assertTrue(foreignKeyWrapper.containsColumn(columnWrapper));
	}
	
}
