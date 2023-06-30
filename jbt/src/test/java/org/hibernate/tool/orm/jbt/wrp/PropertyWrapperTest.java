package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyWrapperTest {
	
	private PropertyWrapper propertyWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		propertyWrapper = new PropertyWrapper();
	}
	
	@Test
	public void testConstruction() {
		assertSame(propertyWrapper, propertyWrapper.getWrappedObject());
	}

}
