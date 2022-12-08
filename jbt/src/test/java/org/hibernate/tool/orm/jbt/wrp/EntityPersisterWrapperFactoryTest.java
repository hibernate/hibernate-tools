package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.wrp.EntityPersisterWrapperFactory.EntityPersisterExtension;
import org.junit.jupiter.api.Test;

public class EntityPersisterWrapperFactoryTest {
	
	@Test
	public void testIsInstanceOfAbstractEntityPersister() throws Exception {
		Object dummyEntityPersister = EntityPersisterWrapperFactory.create(createTestEntityPersister());
		Method isInstanceOfAbstractEntityPersisterMethod =
				EntityPersisterExtension.class
				.getDeclaredMethod("isInstanceOfAbstractEntityPersister");
		assertTrue((Boolean)isInstanceOfAbstractEntityPersisterMethod.invoke(dummyEntityPersister, (Object[])null));
	}
	
	@Test
	public void testGetTuplizerPropertyValue() throws Exception {
		Object dummyEntityPersister = EntityPersisterWrapperFactory.create(createTestEntityPersister());
		Method getTuplizerPropertyValueMethod =
				EntityPersisterExtension.class
				.getDeclaredMethod("getTuplizerPropertyValue", new Class[] { Object.class, int.class });
		assertSame(PROPERTY_VALUE, getTuplizerPropertyValueMethod.invoke(dummyEntityPersister, new Object(), 0));
	}
	
	private static EntityPersister createTestEntityPersister() {
		return (EntityPersister)Proxy.newProxyInstance(
				EntityPersisterWrapperFactoryTest.class.getClassLoader(), 
				new Class[] { EntityPersister.class }, 
				new TestInvocationHandler());
	}
	
	private static class TestInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("getValue".equals(method.getName())) {
				return PROPERTY_VALUE;
			}
			return null;
		}
		
	}
	
	private static final Object PROPERTY_VALUE = new Object();

}
