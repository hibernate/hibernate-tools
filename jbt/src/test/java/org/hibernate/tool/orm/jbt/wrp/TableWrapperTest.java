package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.KeyValue;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableWrapperTest {
	
	private TableWrapper tableWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		tableWrapper = new TableWrapper("foo");
		KeyValue v = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		tableWrapper.setIdentifierValue(v);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(tableWrapper);
		assertEquals("foo", tableWrapper.getName());
	}
	
	@Test
	public void testGetIdentifierValue() {
		KeyValue v = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		tableWrapper.setIdentifierValue(v);
		assertSame(v,  ((Wrapper)tableWrapper.getIdentifierValue()).getWrappedObject());
	}

}
