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
	private PersistentClass singleTableSubclassTarget = null;
	private PersistentClassWrapper singleTableSubclassWrapper = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		InvocationHandler invocationHandler = null;
		Field wrapperField = PersistentClassWrapperInvocationHandler.class.getDeclaredField("wrapper");
		wrapperField.setAccessible(true);
		rootClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		invocationHandler = Proxy.getInvocationHandler(rootClassWrapper);
		rootClassTarget = (PersistentClass)wrapperField.get(invocationHandler);
		singleTableSubclassWrapper = PersistentClassWrapperFactory.createSingleTableSubclassWrapper(rootClassWrapper);
		invocationHandler = Proxy.getInvocationHandler(singleTableSubclassWrapper);
		singleTableSubclassTarget = (PersistentClass)wrapperField.get(invocationHandler);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(rootClassWrapper);
		assertNotNull(rootClassTarget);
		assertNotNull(singleTableSubclassWrapper);
		assertNotNull(singleTableSubclassTarget);
	}
	
	@Test
	public void testGetWrappedObject() {
		assertSame(rootClassTarget, rootClassWrapper.getWrappedObject());
		assertSame(singleTableSubclassTarget, singleTableSubclassWrapper.getWrappedObject());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("foo", rootClassWrapper.getEntityName());
		assertNotEquals("bar", singleTableSubclassWrapper.getEntityName());
		rootClassTarget.setEntityName("foo");
		singleTableSubclassTarget.setEntityName("bar");
		assertEquals("foo", rootClassWrapper.getEntityName());
		assertEquals("bar", singleTableSubclassWrapper.getEntityName());
	}
	
	@Test
	public void testGetClassName() {
		assertNotEquals("foo", rootClassWrapper.getClassName());
		assertNotEquals("bar", singleTableSubclassWrapper.getClassName());
		rootClassTarget.setClassName("foo");
		singleTableSubclassTarget.setClassName("bar");
		assertEquals("foo", rootClassWrapper.getClassName());
		assertEquals("bar", singleTableSubclassWrapper.getClassName());
	}
	
}
