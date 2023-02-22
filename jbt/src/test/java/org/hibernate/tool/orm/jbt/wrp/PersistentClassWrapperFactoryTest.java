package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapperFactory.PersistentClassWrapperInvocationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersistentClassWrapperFactoryTest {
	
	private PersistentClass rootClassTarget = null;
	private PersistentClassWrapper rootClassWrapper = null;
	private PersistentClass singleTableSubclassTarget = null;
	private PersistentClassWrapper singleTableSubclassWrapper = null;
	private PersistentClass joinedSubclassTarget = null;
	private PersistentClassWrapper joinedSubclassWrapper = null;
	
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
		joinedSubclassWrapper = PersistentClassWrapperFactory.createJoinedSubclassWrapper(rootClassWrapper);
		invocationHandler = Proxy.getInvocationHandler(joinedSubclassWrapper);
		joinedSubclassTarget = (PersistentClass)wrapperField.get(invocationHandler);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(rootClassWrapper);
		assertNotNull(rootClassTarget);
		assertTrue(rootClassTarget instanceof RootClass);
		assertNotNull(singleTableSubclassWrapper);
		assertNotNull(singleTableSubclassTarget);
		assertTrue(singleTableSubclassTarget instanceof SingleTableSubclass);
		assertNotNull(joinedSubclassWrapper);
		assertNotNull(joinedSubclassTarget);
		assertTrue(joinedSubclassTarget instanceof JoinedSubclass);
	}
	
	@Test
	public void testGetWrappedObject() {
		assertSame(rootClassTarget, rootClassWrapper.getWrappedObject());
		assertSame(singleTableSubclassTarget, singleTableSubclassWrapper.getWrappedObject());
		assertSame(joinedSubclassTarget, joinedSubclassWrapper.getWrappedObject());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("foo", rootClassWrapper.getEntityName());
		assertNotEquals("bar", singleTableSubclassWrapper.getEntityName());
		assertNotEquals("raz", joinedSubclassWrapper.getEntityName());
		rootClassTarget.setEntityName("foo");
		singleTableSubclassTarget.setEntityName("bar");
		joinedSubclassTarget.setEntityName("raz");
		assertEquals("foo", rootClassWrapper.getEntityName());
		assertEquals("bar", singleTableSubclassWrapper.getEntityName());
		assertEquals("raz", joinedSubclassWrapper.getEntityName());
	}
	
	@Test
	public void testGetClassName() {
		assertNotEquals("foo", rootClassWrapper.getClassName());
		assertNotEquals("bar", singleTableSubclassWrapper.getClassName());
		assertNotEquals("raz", joinedSubclassWrapper.getClassName());
		rootClassTarget.setClassName("foo");
		singleTableSubclassTarget.setClassName("bar");
		joinedSubclassTarget.setClassName("raz");
		assertEquals("foo", rootClassWrapper.getClassName());
		assertEquals("bar", singleTableSubclassWrapper.getClassName());
		assertEquals("raz", joinedSubclassWrapper.getClassName());
	}
	
	@Test
	public void testIsAssignableToRootClass() {
		assertTrue(rootClassWrapper.isAssignableToRootClass());
		assertFalse(singleTableSubclassWrapper.isAssignableToRootClass());
		assertFalse(joinedSubclassWrapper.isAssignableToRootClass());
	}
	
	@Test
	public void testIsRootClass() {
		assertTrue(rootClassWrapper.isRootClass());
		assertFalse(singleTableSubclassWrapper.isRootClass());
		assertFalse(joinedSubclassWrapper.isRootClass());
	}
	
	@Test
	public void testGetIdentifierProperty() {
		assertNull(rootClassWrapper.getIdentifierProperty());
		Property property = new Property();
		((RootClass)rootClassTarget).setIdentifierProperty(property);
		assertSame(property, rootClassWrapper.getIdentifierProperty());
	}
	
	@Test
	public void testHasIdentifierProperty() {
		assertFalse(rootClassWrapper.hasIdentifierProperty());
		((RootClass)rootClassTarget).setIdentifierProperty(new Property());
		assertTrue(rootClassWrapper.hasIdentifierProperty());
	}
	
	@Test
	public void testIsInstanceOfRootClass() {
		assertTrue(rootClassWrapper.isInstanceOfRootClass());
		assertFalse(singleTableSubclassWrapper.isInstanceOfRootClass());
		assertFalse(joinedSubclassWrapper.isInstanceOfRootClass());
	}
	
	@Test
	public void testIsInstanceOfSubclass() {
		assertFalse(rootClassWrapper.isInstanceOfSubclass());
		assertTrue(singleTableSubclassWrapper.isInstanceOfSubclass());
		assertTrue(joinedSubclassWrapper.isInstanceOfSubclass());
	}
	
	@Test
	public void testGetRootClass() {
		assertSame(rootClassWrapper.getRootClass(), rootClassTarget);
		assertSame(singleTableSubclassWrapper.getRootClass(), rootClassTarget);
		assertSame(joinedSubclassWrapper.getRootClass(), rootClassTarget);
	}
	
	@Test
	public void testGetPropertyClosureIterator() {
		Iterator<Property> propertyClosureIterator = rootClassWrapper.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		Property property = new Property();
		rootClassTarget.addProperty(property);
		propertyClosureIterator = rootClassWrapper.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());
		assertSame(property, propertyClosureIterator.next());
	}
	
	@Test
	public void testGetSuperclass() {
		assertNull(rootClassWrapper.getSuperclass());
		assertSame(rootClassTarget, singleTableSubclassTarget.getSuperclass());
		assertSame(rootClassTarget, joinedSubclassTarget.getSuperclass());
	}
	
	@Test
	public void testGetPropertyIterator() {
		Iterator<Property> propertyIterator = rootClassWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		Property property = new Property();
		rootClassTarget.addProperty(property);
		propertyIterator = rootClassWrapper.getPropertyIterator();
		assertSame(property, propertyIterator.next());
	}
	
	@Test
	public void testGetProperty() {
		try {
			rootClassWrapper.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					t.getMessage());
		}
		Property property = new Property();
		property.setName("foo");
		rootClassTarget.addProperty(property);
		assertSame(property, rootClassWrapper.getProperty("foo"));
		try {
			rootClassWrapper.getProperty();
			fail();
		} catch (Throwable t) {
			assertEquals(
					"getProperty() is only allowed on SpecialRootClass", 
					t.getMessage());
		}
	}
	
	@Test
	public void testGetTable() {
		assertNull(rootClassWrapper.getTable());
		Table table = new Table("test");
		((RootClass)rootClassTarget).setTable(table);
		assertSame(table, rootClassWrapper.getTable());
	}
	
	@Test 
	public void testIsAbstract() {
		assertNull(rootClassWrapper.isAbstract());
		rootClassTarget.setAbstract(true);
		assertTrue(rootClassWrapper.isAbstract());
		rootClassTarget.setAbstract(false);
		assertFalse(rootClassWrapper.isAbstract());
	}
	
	@Test
	public void testGetDiscriminator() {
		assertNull(rootClassWrapper.getDiscriminator());
		Value value = createValue();
		((RootClass)rootClassTarget).setDiscriminator(value);
		assertSame(value, rootClassWrapper.getDiscriminator());
	}
	
	@Test
	public void testGetIdentifier() {
		assertNull(rootClassWrapper.getIdentifier());
		KeyValue value = createValue();
		((RootClass)rootClassTarget).setIdentifier(value);
		assertSame(value, rootClassWrapper.getIdentifier());
	}
	
	@Test
	public void testGetJoinIterator() {
		assertFalse(rootClassWrapper.getJoinIterator().hasNext());
		Join join = new Join();
		rootClassTarget.addJoin(join);
		Iterator<Join> joinIterator = rootClassWrapper.getJoinIterator();
		assertSame(join, joinIterator.next());	
	}
	
	@Test
	public void testGetVersion() {
		assertNull(rootClassWrapper.getVersion());
		Property versionTarget = new Property();
		((RootClass)rootClassTarget).setVersion(versionTarget);
		assertSame(versionTarget, rootClassWrapper.getVersion());
	}
	
	@Test
	public void testSetClassName() {
		assertNull(rootClassTarget.getClassName());
		rootClassWrapper.setClassName("foo");
		assertEquals("foo", rootClassTarget.getClassName());
	}
	
	@Test
	public void testSetEntityName() {
		assertNull(rootClassTarget.getEntityName());
		rootClassWrapper.setEntityName("foo");
		assertEquals("foo", rootClassTarget.getEntityName());
	}
	
	private KeyValue createValue() {
		return (KeyValue)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { KeyValue.class }, 
				new InvocationHandler() {	
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
		});
	}
}
