package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.hibernate.tool.orm.jbt.wrp.EntityPersisterWrapperFactory.EntityPersisterExtension;
import org.junit.jupiter.api.Test;

public class EntityPersisterWrapperFactoryTest {
	
	@Test
	public void testCreate() throws Exception {
		Object dummyEntityPersister = EntityPersisterWrapperFactory.create(null);
		Method isInstanceOfAbstractEntityPersisterMethod =
				EntityPersisterExtension.class
				.getDeclaredMethod("isInstanceOfAbstractEntityPersister");
		assertTrue((Boolean)isInstanceOfAbstractEntityPersisterMethod.invoke(dummyEntityPersister, (Object[])null));
	}

}
