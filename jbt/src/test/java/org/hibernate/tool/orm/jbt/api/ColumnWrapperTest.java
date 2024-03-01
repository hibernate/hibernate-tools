package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.orm.jbt.internal.factory.ColumnWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColumnWrapperTest {

	private ColumnWrapper columnWrapper = null; 
	
	@BeforeEach
	public void beforeEach() throws Exception {
		columnWrapper = ColumnWrapperFactory.createColumnWrapper("foo");
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(columnWrapper);
	}
	
}
