package org.hibernate.tool.orm.jbt.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpecialRootClassTest {
	
	private SpecialRootClass specialRootClass = null;
	
	private Property property = new Property();
	
	@BeforeEach 
	public void beforeEach() {
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		property.setPersistentClass(pc);
		specialRootClass = new SpecialRootClass(property);
	}
	
	@Test
	public void testConstruction() throws Exception {
		assertNotNull(specialRootClass);
		Field field = PersistentClass.class.getDeclaredField("metadataBuildingContext");
		field.setAccessible(true);
		assertSame(DummyMetadataBuildingContext.INSTANCE, field.get(specialRootClass));
	}
	
	@Test
	public void testGetProperty() {
		assertSame(property, specialRootClass.getProperty());
	}

}
