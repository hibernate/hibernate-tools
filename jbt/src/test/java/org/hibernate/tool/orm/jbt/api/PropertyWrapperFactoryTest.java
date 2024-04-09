package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.Property;
import org.hibernate.tool.orm.jbt.internal.factory.PropertyWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyWrapperFactoryTest {

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

}
