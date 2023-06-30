package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyWrapperTest {
	
	private PropertyWrapper propertyWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		propertyWrapper = new PropertyWrapper();
	}
	
	@Test
	public void testConstruction() {
		assertSame(propertyWrapper, propertyWrapper.getWrappedObject());
	}
	
	@Test
	public void testGetValue() {
		Value v = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		assertNull(propertyWrapper.getValue());
		propertyWrapper.setValue(v);
		assertSame(v, ((Wrapper)propertyWrapper.getValue()).getWrappedObject());
	}

}
