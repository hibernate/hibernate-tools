package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;
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
		PersistentClassWrapper persistentClass = PersistentClassWrapperFactory.createRootClassWrapper();
		assertNull(wrappedProperty.getPersistentClass());
		propertyWrapper.setPersistentClass(persistentClass.getWrappedObject());
		assertSame(persistentClass.getWrappedObject(), wrappedProperty.getPersistentClass());
	}
	
	@Test
	public void testGetPersistentClass() {
		PersistentClassWrapper persistentClass = PersistentClassWrapperFactory.createRootClassWrapper();
		assertNull(propertyWrapper.getPersistentClass());
		wrappedProperty.setPersistentClass(persistentClass.getWrappedObject());
		PersistentClassWrapper persistentClassWrapper = propertyWrapper.getPersistentClass();
		assertSame(persistentClass.getWrappedObject(), persistentClassWrapper.getWrappedObject());
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
	
	@Test
	public void testGetPropetyAccessorName() {
		assertNotEquals("foo", propertyWrapper.getPropertyAccessorName());
		wrappedProperty.setPropertyAccessorName("foo");
		assertEquals("foo", propertyWrapper.getPropertyAccessorName());
	}
	
	@Test
	public void testGetName() {
		assertNotEquals("foo", propertyWrapper.getName());
		wrappedProperty.setName("foo");
		assertEquals("foo", propertyWrapper.getName());
	}
	
	@Test
	public void testGetType() {
		BasicValue v = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		v.setTypeName("int");
		assertNull(propertyWrapper.getType());
		wrappedProperty.setValue(v);
		TypeWrapper typeWrapper = (TypeWrapper)propertyWrapper.getType();
		assertEquals("integer", typeWrapper.getName());
		assertSame(v.getType(), typeWrapper.getWrappedObject());
	}
	
}
