package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyWrapperFactoryTest {
	
	private Property wrappedProperty = null;
	private PropertyWrapperFactory.PropertyWrapper propertyWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedProperty = new Property();
		propertyWrapper = PropertyWrapperFactory.createPropertyWrapper(wrappedProperty);
	}

	@Test
	public void testCreatePropertyWrapper() {
		assertNotNull(wrappedProperty);
		assertNotNull(propertyWrapper);
		assertSame(wrappedProperty, propertyWrapper.getWrappedObject());
	}
	
	@Test
	public void testGetValue() {
		assertNull(propertyWrapper.getValue());
		Value valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		wrappedProperty.setValue(valueTarget);
		Value valueWrapper = propertyWrapper.getValue();
		assertNotNull(valueWrapper);
		assertSame(valueTarget, ((Wrapper)valueWrapper).getWrappedObject());
	}
	
}
