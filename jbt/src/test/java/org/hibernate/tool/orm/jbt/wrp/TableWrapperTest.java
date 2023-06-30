package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableWrapperTest {
	
	private TableWrapper tableWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		tableWrapper = new TableWrapper("foo");
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(tableWrapper);
		assertEquals("foo", tableWrapper.getName());
	}

}
