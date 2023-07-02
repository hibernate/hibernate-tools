package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
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
	
	@Test
	public void testSetName() {
		assertNotEquals("foo", wrappedProperty.getName());
		propertyWrapper.setName("foo");
		assertEquals("foo", wrappedProperty.getName());
	}
	
	@Test
	public void testSetPersistentClass() {
		assertNull(wrappedProperty.getPersistentClass());
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		propertyWrapper.setPersistentClass(pc);
		assertSame(pc, wrappedProperty.getPersistentClass());
	}
	
}