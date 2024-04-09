package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.internal.factory.PropertyWrapperFactory;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyWrapperTest {

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
	
	@Test
	public void testSetPersistentClass() {
		PersistentClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		assertNull(wrappedProperty.getPersistentClass());
		propertyWrapper.setPersistentClass(persistentClass);
		assertSame(persistentClass, wrappedProperty.getPersistentClass());
	}
	
	@Test
	public void testGetPersistentClass() {
		PersistentClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		assertNull(propertyWrapper.getPersistentClass());
		wrappedProperty.setPersistentClass(persistentClass);
		assertSame(persistentClass, propertyWrapper.getPersistentClass());
	}
	
	@Test
	public void testIsComposite() {
		wrappedProperty.setValue(new BasicValue(DummyMetadataBuildingContext.INSTANCE));
		assertFalse(propertyWrapper.isComposite());
		Component component = new Component(
				DummyMetadataBuildingContext.INSTANCE, 
				new Table(""), 
				new RootClass(DummyMetadataBuildingContext.INSTANCE));
		wrappedProperty.setValue(component);
		assertTrue(propertyWrapper.isComposite());
	}
	
}
