package org.hibernate.tool.orm.jbt.util;

import java.lang.reflect.Field;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;

public class SpecialRootClass extends RootClass {
	
	private Property property;

	public SpecialRootClass(Property property) {
		super(getMetadataBuildingContext(property));
		this.property = property;
	}
	
	public Property getProperty() {
		return property;
	}
	
	private static MetadataBuildingContext getMetadataBuildingContext(Property property) {
		MetadataBuildingContext result = DummyMetadataBuildingContext.INSTANCE;
		try {
			if (property != null) {
				PersistentClass pc = property.getPersistentClass();
				Field field = PersistentClass.class.getDeclaredField("metadataBuildingContext");
				field.setAccessible(true);
				result = (MetadataBuildingContext)field.get(pc);
			}
		} catch (NoSuchFieldException | 
				SecurityException | 
				IllegalArgumentException | 
				IllegalAccessException e) {
			throw new RuntimeException("Problem while trying to retrieve MetadataBuildingContext from field", e);
		}
		return result;
	}

}
