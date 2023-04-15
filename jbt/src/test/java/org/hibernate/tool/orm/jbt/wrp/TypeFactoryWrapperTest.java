package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.orm.jbt.type.TypeFactory;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;
import org.junit.jupiter.api.Test;

public class TypeFactoryWrapperTest {

	@Test
	public void testConstruction() {
		assertNotNull(TypeFactoryWrapper.INSTANCE);
	}
	
	@Test
	public void testGetBooleanType() {
		TypeWrapper booleanTypeWrapper = TypeFactoryWrapper.INSTANCE.getBooleanType();
		assertSame(booleanTypeWrapper.getWrappedObject(), TypeFactory.BOOLEAN_TYPE);
	}

}
