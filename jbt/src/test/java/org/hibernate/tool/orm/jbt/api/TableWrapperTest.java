package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
	
}
