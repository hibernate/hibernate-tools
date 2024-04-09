package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.internal.factory.PropertyWrapperFactory;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyWrapperFactoryTest {

	private Property wrappedProperty = null;
	private PropertyWrapper propertyWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedProperty = new Property();
		propertyWrapper = (PropertyWrapper)PropertyWrapperFactory.createPropertyWrapper(wrappedProperty);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedProperty);
		assertNotNull(propertyWrapper);
	}

	@Test
	public void testGetValue() {
		assertNull(propertyWrapper.getValue());
		Value value = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		wrappedProperty.setValue(value);
		Value v = propertyWrapper.getValue();
		assertSame(value, v);
	}
	
	@Test
	public void testSetName() {
		assertNotEquals("foo", wrappedProperty.getName());
		propertyWrapper.setName("foo");
		assertEquals("foo", wrappedProperty.getName());
	}
	
}
