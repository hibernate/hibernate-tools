package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.Backref;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.internal.factory.PropertyWrapperFactory;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.type.Type;
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
		Type t = propertyWrapper.getType();
		assertEquals("integer", t.getName());
		assertSame(v.getType(), t);
	}
	
	@Test
	public void testSetValue() {
		assertNull(wrappedProperty.getValue());	
		BasicValue value = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		propertyWrapper.setValue(value);
		assertSame(value, wrappedProperty.getValue());
	}
	
	@Test
	public void testSetPropertyAccessorName() {
		assertNotEquals("foo", wrappedProperty.getPropertyAccessorName());
		propertyWrapper.setPropertyAccessorName("foo");
		assertEquals("foo", wrappedProperty.getPropertyAccessorName());
	}
	
	@Test
	public void testSetCascade() {
		assertNotEquals("foo", wrappedProperty.getCascade());
		propertyWrapper.setCascade("foo");
		assertEquals("foo", wrappedProperty.getCascade());
	}
	
	@Test
	public void testIsBackRef() throws Exception {
		assertFalse(propertyWrapper.isBackRef());
		propertyWrapper = (PropertyWrapper)PropertyWrapperFactory.createPropertyWrapper(new Backref());
		assertTrue(propertyWrapper.isBackRef());
	}
	
	@Test
	public void testIsSelectable() {
		wrappedProperty.setSelectable(true);
		assertTrue(propertyWrapper.isSelectable());
		wrappedProperty.setSelectable(false);
		assertFalse(propertyWrapper.isSelectable());
	}
	
	@Test
	public void testIsInsertable() {
		BasicValue v = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		v.setTable(new Table(""));
		Column c = new Column();
		v.addColumn(c);
		wrappedProperty.setValue(v);
		wrappedProperty.setInsertable(true);
		assertTrue(propertyWrapper.isInsertable());
		wrappedProperty.setInsertable(false);
		assertFalse(propertyWrapper.isInsertable());
	}
	
	@Test
	public void testIsUpdateable() {
		BasicValue v = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		v.setTable(new Table(""));
		Column c = new Column();
		v.addColumn(c);
		wrappedProperty.setValue(v);
		wrappedProperty.setUpdateable(false);
		assertFalse(propertyWrapper.isUpdateable());
		wrappedProperty.setUpdateable(true);
		assertTrue(propertyWrapper.isUpdateable());
	}
	
	@Test
	public void testGetCascade() {
		assertNotEquals("foo", propertyWrapper.getCascade());
		wrappedProperty.setCascade("foo");
		assertEquals("foo", propertyWrapper.getCascade());
	}
	
	@Test
	public void testIsLazy() {
		wrappedProperty.setLazy(true);
		assertTrue(propertyWrapper.isLazy());
		wrappedProperty.setLazy(false);
		assertFalse(propertyWrapper.isLazy());
	}
	
}
