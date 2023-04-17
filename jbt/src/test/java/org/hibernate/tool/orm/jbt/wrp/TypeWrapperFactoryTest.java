package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;
import org.hibernate.type.Type;
import org.junit.jupiter.api.Test;

public class TypeWrapperFactoryTest {

	@Test
	public void testCreateTyperapper() {
		TypeWrapper typeWrapper = TypeWrapperFactory.createTypeWrapper(TypeFactoryWrapper.INSTANCE.getStringType());
		assertNotNull(typeWrapper);
		assertEquals("string", ((Type)((Wrapper)typeWrapper).getWrappedObject()).getName());
	}
	
}
