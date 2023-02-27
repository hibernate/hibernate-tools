package org.hibernate.tool.orm.jbt.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.junit.jupiter.api.Test;

public class SpecialRootClassTest {
	
	private SpecialRootClass specialRootClass = null;
	
	@Test
	public void testPropertyIsNull() {
		specialRootClass = new SpecialRootClass(null);
		assertNotNull(specialRootClass);
		assertNull(specialRootClass.getProperty());
		assertNull(specialRootClass.getParentProperty());
	}
	
	@Test
	public void testPropertyIsNotNull() throws Exception {
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		Property property = new Property();
		property.setPersistentClass(pc);
		specialRootClass = new SpecialRootClass(property);
		assertNotNull(specialRootClass);
		Field field = PersistentClass.class.getDeclaredField("metadataBuildingContext");
		field.setAccessible(true);
		assertSame(DummyMetadataBuildingContext.INSTANCE, field.get(specialRootClass));
		assertSame(property, specialRootClass.getProperty());
	}
	
	@Test
	public void testPropertyIsComponent() {
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		Component component = new Component(DummyMetadataBuildingContext.INSTANCE, pc);
		Property property = new Property();
		property.setValue(component);
		property.setPersistentClass(pc);
		specialRootClass = new SpecialRootClass(property);
		assertSame(property, specialRootClass.getProperty());
		assertNull(specialRootClass.getParentProperty());
		assertTrue(specialRootClass.getProperties().isEmpty());
		component.setParentProperty("foo");
		specialRootClass = new SpecialRootClass(property);
		assertSame(property, specialRootClass.getProperty());
		Property parentProperty = specialRootClass.getParentProperty();
		assertNotNull(parentProperty);
		assertEquals("foo", parentProperty.getName());
		assertTrue(specialRootClass.getProperties().isEmpty());
		Property p = new Property();
		p.setName("bar");
		component.addProperty(p);
		specialRootClass = new SpecialRootClass(property);
		assertFalse(specialRootClass.getProperties().isEmpty());
		assertEquals("bar", specialRootClass.getProperties().get(0).getName());
	}
	
	@Test
	public void testPropertyIsCollection() {
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		Set set = new Set(DummyMetadataBuildingContext.INSTANCE, pc);
		Property property = new Property();
		property.setValue(set);
		property.setPersistentClass(pc);
		specialRootClass = new SpecialRootClass(property);
		assertSame(property, specialRootClass.getProperty());
		assertNull(specialRootClass.getParentProperty());
		assertTrue(specialRootClass.getProperties().isEmpty());
		set.setElement(new BasicValue(DummyMetadataBuildingContext.INSTANCE));
		specialRootClass = new SpecialRootClass(property);
		assertSame(property, specialRootClass.getProperty());
		assertNull(specialRootClass.getParentProperty());
		assertTrue(specialRootClass.getProperties().isEmpty());
		Component component = new Component(DummyMetadataBuildingContext.INSTANCE, pc);
		set.setElement(component);
		specialRootClass = new SpecialRootClass(property);
		assertSame(property, specialRootClass.getProperty());
		assertNull(specialRootClass.getParentProperty());
		assertTrue(specialRootClass.getProperties().isEmpty());
		component.setParentProperty("foo");
		specialRootClass = new SpecialRootClass(property);
		assertSame(property, specialRootClass.getProperty());
		Property parentProperty = specialRootClass.getParentProperty();
		assertNotNull(parentProperty);
		assertEquals("foo", parentProperty.getName());
		assertTrue(specialRootClass.getProperties().isEmpty());
		Property p = new Property();
		p.setName("bar");
		component.addProperty(p);
		specialRootClass = new SpecialRootClass(property);
		assertFalse(specialRootClass.getProperties().isEmpty());
		assertEquals("bar", specialRootClass.getProperties().get(0).getName());
	}
	
}
