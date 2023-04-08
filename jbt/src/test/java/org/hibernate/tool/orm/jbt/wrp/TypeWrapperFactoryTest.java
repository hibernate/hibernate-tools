package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.orm.jbt.type.TypeFactory;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;
import org.junit.jupiter.api.Test;

public class TypeWrapperFactoryTest {

	@Test
	public void testCreateTyperapper() {
		TypeWrapper typeWrapper = TypeWrapperFactory.createTypeWrapper(TypeFactory.STRING_TYPE);
		assertNotNull(typeWrapper);
		assertSame(TypeFactory.STRING_TYPE, ((Wrapper)typeWrapper).getWrappedObject());
	}
	
}
