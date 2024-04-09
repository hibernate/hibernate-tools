package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
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
		assertTrue(wrappedPrimaryKey.getColumns().isEmpty());
		primaryKeyWrapper.addColumn(column);
		assertEquals(1, wrappedPrimaryKey.getColumns().size());
		assertSame(column, wrappedPrimaryKey.getColumns().get(0));
	}
	
	@Test
	public void testGetColumnSpan() {
		assertEquals(0, primaryKeyWrapper.getColumnSpan());
		wrappedPrimaryKey.addColumn(new Column());
		assertEquals(1, primaryKeyWrapper.getColumnSpan());
	}
	
}
