/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.hibernate.mapping.Backref;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.internal.factory.PersistentClassWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.PropertyWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyWrapperTest {

	private Property wrappedProperty = null;
	private PropertyWrapper propertyWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		propertyWrapper = PropertyWrapperFactory.createPropertyWrapper();
		wrappedProperty = (Property)propertyWrapper.getWrappedObject();
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
		ValueWrapper v = propertyWrapper.getValue();
		assertSame(value, v.getWrappedObject());
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
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createPersistentClassWrapper(persistentClass);
		assertNull(wrappedProperty.getPersistentClass());
		propertyWrapper.setPersistentClass(persistentClassWrapper);
		assertSame(persistentClass, wrappedProperty.getPersistentClass());
	}
	
	@Test
	public void testGetPersistentClass() {
		PersistentClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		assertNull(propertyWrapper.getPersistentClass());
		wrappedProperty.setPersistentClass(persistentClass);
		assertSame(persistentClass, propertyWrapper.getPersistentClass().getWrappedObject());
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
		TypeWrapper t = propertyWrapper.getType();
		assertEquals("integer", t.getName());
		assertSame(v.getType(), t.getWrappedObject());
	}
	
	@Test
	public void testSetValue() {
		assertNull(wrappedProperty.getValue());	
		BasicValue value = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		ValueWrapper valueWrapper = ValueWrapperFactory.createValueWrapper(value);
		propertyWrapper.setValue(valueWrapper);
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
		Field field = propertyWrapper.getClass().getDeclaredField("property");
		field.setAccessible(true);
		field.set(propertyWrapper, new Backref());
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
	
	@Test
	public void testIsOptional() {
		wrappedProperty.setValue(new BasicValue(DummyMetadataBuildingContext.INSTANCE));
		wrappedProperty.setOptional(true);
		assertTrue(propertyWrapper.isOptional());
		wrappedProperty.setOptional(false);
		assertFalse(propertyWrapper.isOptional());
	}
	
	@Test
	public void testIsNaturalIdentifier() {
		wrappedProperty.setNaturalIdentifier(true);
		assertTrue(propertyWrapper.isNaturalIdentifier());
		wrappedProperty.setNaturalIdentifier(false);
		assertFalse(propertyWrapper.isNaturalIdentifier());
	}
	
	@Test
	public void testIsOptimisticLocked() {
		wrappedProperty.setOptimisticLocked(true);
		assertTrue(propertyWrapper.isOptimisticLocked());
		wrappedProperty.setOptimisticLocked(false);
		assertFalse(propertyWrapper.isOptimisticLocked());
	}
	
}
