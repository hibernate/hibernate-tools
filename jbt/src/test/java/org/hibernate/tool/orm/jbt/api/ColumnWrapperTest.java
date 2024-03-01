package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.mapping.Column;
import org.hibernate.tool.orm.jbt.internal.factory.ColumnWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColumnWrapperTest {

	private ColumnWrapper columnWrapper = null; 
	private Column wrappedColumn = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		columnWrapper = ColumnWrapperFactory.createColumnWrapper(null);
		wrappedColumn = (Column)columnWrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(columnWrapper);
	}
	
	@Test
	public void testGetName() {
		assertNull(columnWrapper.getName());
		wrappedColumn.setName("foobar");
		assertEquals("foobar", columnWrapper.getName());
	}
	
}
