package org.hibernate.tool.orm.jbt.internal.util;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;

public class SpecialRootClass extends RootClass {
	
	private static final long serialVersionUID = 1L;

	private Property property;
	private Property parentProperty;

	public SpecialRootClass(Property property) {
		super(getMetadataBuildingContext(property));
		this.property = property;
		initialize();
	}
	
	public Property getProperty() {
		return property;
	}
	
	public Property getParentProperty() {
		return parentProperty;
	}
	
	private void initialize() {
		Component component = getComponent();
		if (component != null) {
			setClassName(component.getComponentClassName());
			setEntityName(component.getComponentClassName());
			PersistentClass ownerClass = component.getOwner();
			if (component.getParentProperty() != null) {
				parentProperty = new Property();
				parentProperty.setName(component.getParentProperty());
				parentProperty.setPersistentClass(ownerClass);
			}
			Iterator<Property> iterator = component.getProperties().iterator();
			while (iterator.hasNext()) {
				Property property = iterator.next();
				if (property != null) {
					addProperty(property);
				}
			}
		}
	}
	
	private Component getComponent() {
		Component result = null;
		if (property != null) {
			Value v = property.getValue();
			if (v != null) {
				if (v instanceof Wrapper) {
					v = (Value)((Wrapper)v).getWrappedObject();
				}
				if (Collection.class.isAssignableFrom(v.getClass())) {
					v = ((Collection)v).getElement();
				}
				if (v != null && Component.class.isAssignableFrom(v.getClass())) {
					result = (Component)v;
				}
			}
		}
		return result;
	}
	
	private static MetadataBuildingContext getMetadataBuildingContext(Property property) {
		MetadataBuildingContext result = DummyMetadataBuildingContext.INSTANCE;
		try {
			if (property != null) {
				PersistentClass pc = property.getPersistentClass();
				if (pc != null) {
					Field field = PersistentClass.class.getDeclaredField("metadataBuildingContext");
					field.setAccessible(true);
					result = (MetadataBuildingContext)field.get(pc);
				}
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
