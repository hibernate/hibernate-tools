package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.mapping.Property;
import org.hibernate.tool.orm.jbt.api.PropertyWrapper;

public class PropertyWrapperFactory {

	public static PropertyWrapper createPropertyWrapper(Property wrappedProperty) {
		return new PropertyWrapper() {
			@Override public Property getWrappedObject() { return wrappedProperty; }
		};
	}
	
}
