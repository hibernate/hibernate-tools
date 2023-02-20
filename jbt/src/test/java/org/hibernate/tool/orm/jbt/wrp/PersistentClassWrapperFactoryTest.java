package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapperFactory.PersistentClassWrapperInvocationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersistentClassWrapperFactoryTest {
	
	private PersistentClass rootClassTarget = null;
	private PersistentClassWrapper rootClassWrapper = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		rootClassWrapper = (PersistentClassWrapper)PersistentClassWrapperFactory
				.createRootClassWrapper();
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(rootClassWrapper);
		Field wrapperField = PersistentClassWrapperInvocationHandler.class.getDeclaredField("wrapper");
		wrapperField.setAccessible(true);
		rootClassTarget = (PersistentClass)wrapperField.get(invocationHandler);
		
;	}
	
	@Test
	public void testConstruction() {
		assertNotNull(rootClassWrapper);
		assertNotNull(rootClassTarget);
	}
	
	@Test
	public void testGetWrappedObject() {
		assertSame(rootClassTarget, rootClassWrapper.getWrappedObject());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("foo", rootClassWrapper.getEntityName());
		rootClassTarget.setEntityName("foo");
		assertEquals("foo", rootClassWrapper.getEntityName());
	}
	
}
