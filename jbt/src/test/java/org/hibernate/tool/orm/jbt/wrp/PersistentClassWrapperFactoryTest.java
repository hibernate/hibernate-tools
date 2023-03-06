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

import org.hibernate.MappingException;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;
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
	private PersistentClass specialRootClassTarget = null;
	private PersistentClassWrapper specialRootClassWrapper = null;
	
	private Property property = null;
	
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
		property = new Property();
		property.setPersistentClass(rootClassTarget);
		specialRootClassWrapper = PersistentClassWrapperFactory.createSpecialRootClassWrapper(property);
		invocationHandler = Proxy.getInvocationHandler(specialRootClassWrapper);
		specialRootClassTarget = (PersistentClass)wrapperField.get(invocationHandler);
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
		assertNotNull(specialRootClassWrapper);
		assertNotNull(specialRootClassTarget);
		assertTrue(specialRootClassTarget instanceof SpecialRootClass);
	}
	
	@Test
	public void testGetWrappedObject() {
		assertSame(rootClassTarget, rootClassWrapper.getWrappedObject());
		assertSame(singleTableSubclassTarget, singleTableSubclassWrapper.getWrappedObject());
		assertSame(joinedSubclassTarget, joinedSubclassWrapper.getWrappedObject());
		assertSame(specialRootClassTarget, specialRootClassWrapper.getWrappedObject());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("foo", rootClassWrapper.getEntityName());
		assertNotEquals("bar", singleTableSubclassWrapper.getEntityName());
		assertNotEquals("raz", joinedSubclassWrapper.getEntityName());
		assertNotEquals("oof", specialRootClassWrapper.getEntityName());
		rootClassTarget.setEntityName("foo");
		singleTableSubclassTarget.setEntityName("bar");
		joinedSubclassTarget.setEntityName("raz");
		specialRootClassTarget.setEntityName("oof");
		assertEquals("foo", rootClassWrapper.getEntityName());
		assertEquals("bar", singleTableSubclassWrapper.getEntityName());
		assertEquals("raz", joinedSubclassWrapper.getEntityName());
		assertEquals("oof", specialRootClassWrapper.getEntityName());
	}
	
	@Test
	public void testGetClassName() {
		assertNotEquals("foo", rootClassWrapper.getClassName());
		assertNotEquals("bar", singleTableSubclassWrapper.getClassName());
		assertNotEquals("raz", joinedSubclassWrapper.getClassName());
		assertNotEquals("oof", specialRootClassWrapper.getClassName());
		rootClassTarget.setClassName("foo");
		singleTableSubclassTarget.setClassName("bar");
		joinedSubclassTarget.setClassName("raz");
		specialRootClassTarget.setClassName("oof");
		assertEquals("foo", rootClassWrapper.getClassName());
		assertEquals("bar", singleTableSubclassWrapper.getClassName());
		assertEquals("raz", joinedSubclassWrapper.getClassName());
		assertEquals("oof", specialRootClassWrapper.getClassName());
	}
	
	@Test
	public void testIsAssignableToRootClass() {
		assertTrue(rootClassWrapper.isAssignableToRootClass());
		assertFalse(singleTableSubclassWrapper.isAssignableToRootClass());
		assertFalse(joinedSubclassWrapper.isAssignableToRootClass());
		assertTrue(specialRootClassWrapper.isAssignableToRootClass());
	}
	
	@Test
	public void testIsRootClass() {
		assertTrue(rootClassWrapper.isRootClass());
		assertFalse(singleTableSubclassWrapper.isRootClass());
		assertFalse(joinedSubclassWrapper.isRootClass());
		assertFalse(specialRootClassWrapper.isRootClass());
	}
	
	@Test
	public void testGetIdentifierProperty() {
		assertNull(rootClassWrapper.getIdentifierProperty());
		assertNull(singleTableSubclassWrapper.getIdentifierProperty());
		assertNull(joinedSubclassWrapper.getIdentifierProperty());
		assertNull(specialRootClassWrapper.getIdentifierProperty());
		Property property = new Property();
		((RootClass)rootClassTarget).setIdentifierProperty(property);
		assertSame(property, rootClassWrapper.getIdentifierProperty());
		assertSame(property, singleTableSubclassWrapper.getIdentifierProperty());
		assertSame(property, joinedSubclassWrapper.getIdentifierProperty());
		assertNull(specialRootClassWrapper.getIdentifierProperty());
		((RootClass)specialRootClassTarget).setIdentifierProperty(property);
		assertSame(property, specialRootClassWrapper.getIdentifierProperty());
	}
	
	@Test
	public void testHasIdentifierProperty() {
		assertFalse(rootClassWrapper.hasIdentifierProperty());
		assertFalse(singleTableSubclassWrapper.hasIdentifierProperty());
		assertFalse(joinedSubclassWrapper.hasIdentifierProperty());
		assertFalse(specialRootClassWrapper.hasIdentifierProperty());
		((RootClass)rootClassTarget).setIdentifierProperty(new Property());
		assertTrue(rootClassWrapper.hasIdentifierProperty());
		assertTrue(singleTableSubclassWrapper.hasIdentifierProperty());
		assertTrue(joinedSubclassWrapper.hasIdentifierProperty());
		assertFalse(specialRootClassWrapper.hasIdentifierProperty());
	}
	
	@Test
	public void testIsInstanceOfRootClass() {
		assertTrue(rootClassWrapper.isInstanceOfRootClass());
		assertFalse(singleTableSubclassWrapper.isInstanceOfRootClass());
		assertFalse(joinedSubclassWrapper.isInstanceOfRootClass());
		assertTrue(specialRootClassWrapper.isInstanceOfRootClass());
	}
	
	@Test
	public void testIsInstanceOfSubclass() {
		assertFalse(rootClassWrapper.isInstanceOfSubclass());
		assertTrue(singleTableSubclassWrapper.isInstanceOfSubclass());
		assertTrue(joinedSubclassWrapper.isInstanceOfSubclass());
		assertFalse(specialRootClassWrapper.isInstanceOfSubclass());
	}
	
	@Test
	public void testGetRootClass() {
		assertSame(rootClassWrapper.getRootClass(), rootClassTarget);
		assertSame(singleTableSubclassWrapper.getRootClass(), rootClassTarget);
		assertSame(joinedSubclassWrapper.getRootClass(), rootClassTarget);
		assertSame(specialRootClassWrapper.getRootClass(), specialRootClassTarget);
	}
	
	@Test
	public void testGetPropertyClosureIterator() {
		Iterator<Property> propertyClosureIterator = rootClassWrapper.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		propertyClosureIterator = singleTableSubclassWrapper.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		propertyClosureIterator = joinedSubclassWrapper.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		propertyClosureIterator = specialRootClassWrapper.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		Property property = new Property();
		rootClassTarget.addProperty(property);
		propertyClosureIterator = rootClassWrapper.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());
		assertSame(property, propertyClosureIterator.next());
		propertyClosureIterator = singleTableSubclassWrapper.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());	
		assertSame(property, propertyClosureIterator.next());
		propertyClosureIterator = joinedSubclassWrapper.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());	
		assertSame(property, propertyClosureIterator.next());
		propertyClosureIterator = specialRootClassWrapper.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());	
		specialRootClassTarget.addProperty(property);
		propertyClosureIterator = specialRootClassWrapper.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());	
		assertSame(property, propertyClosureIterator.next());
	}
	
	@Test
	public void testGetSuperclass() {
		assertNull(rootClassWrapper.getSuperclass());
		assertSame(rootClassTarget, singleTableSubclassTarget.getSuperclass());
		assertSame(rootClassTarget, joinedSubclassTarget.getSuperclass());
		assertNull(specialRootClassWrapper.getSuperclass());
	}
	
	@Test
	public void testGetPropertyIterator() {
		Iterator<Property> propertyIterator = rootClassWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		propertyIterator = singleTableSubclassWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		propertyIterator = joinedSubclassWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		propertyIterator = specialRootClassWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		Property property = new Property();
		rootClassTarget.addProperty(property);
		propertyIterator = rootClassWrapper.getPropertyIterator();
		assertSame(property, propertyIterator.next());
		propertyIterator = singleTableSubclassWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		singleTableSubclassTarget.addProperty(property);
		propertyIterator = singleTableSubclassWrapper.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		propertyIterator = joinedSubclassWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		joinedSubclassTarget.addProperty(property);
		propertyIterator = joinedSubclassWrapper.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		propertyIterator = specialRootClassWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		specialRootClassTarget.addProperty(property);
		propertyIterator = specialRootClassWrapper.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
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
		try {
			singleTableSubclassWrapper.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					t.getMessage());
		}
		try {
			joinedSubclassWrapper.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					t.getMessage());
		}
		Property p = new Property();
		p.setName("foo");
		rootClassTarget.addProperty(p);
		assertSame(p, rootClassWrapper.getProperty("foo"));
		assertSame(p, singleTableSubclassWrapper.getProperty("foo"));
		assertSame(p, joinedSubclassWrapper.getProperty("foo"));
		try {
			specialRootClassWrapper.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					t.getMessage());
		}
		specialRootClassTarget.addProperty(p);
		assertSame(p, specialRootClassWrapper.getProperty("foo"));
		try {
			rootClassWrapper.getProperty();
			fail();
		} catch (Throwable t) {
			assertEquals(
					"getProperty() is only allowed on SpecialRootClass", 
					t.getMessage());
		}
		try {
			singleTableSubclassWrapper.getProperty();
			fail();
		} catch (Throwable t) {
			assertEquals(
					"getProperty() is only allowed on SpecialRootClass", 
					t.getMessage());
		}
		try {
			joinedSubclassWrapper.getProperty();
			fail();
		} catch (Throwable t) {
			assertEquals(
					"getProperty() is only allowed on SpecialRootClass", 
					t.getMessage());
		}
		assertSame(property, specialRootClassWrapper.getProperty());
	}
	
	@Test
	public void testGetTable() {
		assertNull(rootClassWrapper.getTable());
		assertNull(singleTableSubclassWrapper.getTable());
		assertNull(joinedSubclassWrapper.getTable());
		assertNull(specialRootClassWrapper.getTable());
		Table table = new Table("test");
		((RootClass)rootClassTarget).setTable(table);
		assertSame(table, rootClassWrapper.getTable());
		assertSame(table, singleTableSubclassWrapper.getTable());
		assertNull(joinedSubclassWrapper.getTable());
		((JoinedSubclass)joinedSubclassTarget).setTable(table);
		assertSame(table, joinedSubclassWrapper.getTable());
		assertNull(specialRootClassWrapper.getTable());
		((RootClass)specialRootClassTarget).setTable(table);
		assertSame(table, specialRootClassWrapper.getTable());
	}
	
	@Test 
	public void testIsAbstract() {
		assertNull(rootClassWrapper.isAbstract());
		assertNull(singleTableSubclassWrapper.isAbstract());
		assertNull(joinedSubclassWrapper.isAbstract());
		assertNull(specialRootClassWrapper.isAbstract());
		rootClassTarget.setAbstract(true);
		singleTableSubclassTarget.setAbstract(true);
		joinedSubclassTarget.setAbstract(true);
		specialRootClassTarget.setAbstract(true);
		assertTrue(rootClassWrapper.isAbstract());
		assertTrue(singleTableSubclassWrapper.isAbstract());
		assertTrue(joinedSubclassWrapper.isAbstract());
		assertTrue(specialRootClassWrapper.isAbstract());
		rootClassTarget.setAbstract(false);
		singleTableSubclassTarget.setAbstract(false);
		joinedSubclassTarget.setAbstract(false);
		specialRootClassTarget.setAbstract(false);
		assertFalse(rootClassWrapper.isAbstract());
		assertFalse(singleTableSubclassWrapper.isAbstract());
		assertFalse(joinedSubclassWrapper.isAbstract());
		assertFalse(specialRootClassWrapper.isAbstract());
	}
	
	@Test
	public void testGetDiscriminator() {
		assertNull(rootClassWrapper.getDiscriminator());
		assertNull(singleTableSubclassWrapper.getDiscriminator());
		assertNull(joinedSubclassWrapper.getDiscriminator());
		assertNull(specialRootClassWrapper.getDiscriminator());
		Value value = createValue();
		((RootClass)rootClassTarget).setDiscriminator(value);
		assertSame(value, rootClassWrapper.getDiscriminator());
		assertSame(value, singleTableSubclassWrapper.getDiscriminator());
		assertSame(value, joinedSubclassWrapper.getDiscriminator());
		((RootClass)specialRootClassTarget).setDiscriminator(value);
		assertSame(value, specialRootClassWrapper.getDiscriminator());
	}
	
	@Test
	public void testGetIdentifier() {
		assertNull(rootClassWrapper.getIdentifier());
		assertNull(singleTableSubclassWrapper.getIdentifier());
		assertNull(joinedSubclassWrapper.getIdentifier());
		assertNull(specialRootClassWrapper.getIdentifier());
		KeyValue value = createValue();
		((RootClass)rootClassTarget).setIdentifier(value);
		assertSame(value, rootClassWrapper.getIdentifier());
		assertSame(value, singleTableSubclassWrapper.getIdentifier());
		assertSame(value, joinedSubclassWrapper.getIdentifier());
		((RootClass)specialRootClassTarget).setIdentifier(value);
		assertSame(value, specialRootClassWrapper.getIdentifier());
	}
	
	@Test
	public void testGetJoinIterator() {
		assertFalse(rootClassWrapper.getJoinIterator().hasNext());
		assertFalse(singleTableSubclassWrapper.getJoinIterator().hasNext());
		assertFalse(joinedSubclassWrapper.getJoinIterator().hasNext());
		assertFalse(specialRootClassWrapper.getJoinIterator().hasNext());
		Join join = new Join();
		rootClassTarget.addJoin(join);
		Iterator<Join> joinIterator = rootClassWrapper.getJoinIterator();
		assertSame(join, joinIterator.next());	
		singleTableSubclassTarget.addJoin(join);
		joinIterator = singleTableSubclassWrapper.getJoinIterator();
		assertSame(join, joinIterator.next());	
		joinedSubclassTarget.addJoin(join);
		joinIterator = joinedSubclassWrapper.getJoinIterator();
		assertSame(join, joinIterator.next());	
		specialRootClassTarget.addJoin(join);
		joinIterator = specialRootClassWrapper.getJoinIterator();
		assertSame(join, joinIterator.next());	
	}
	
	@Test
	public void testGetVersion() {
		assertNull(rootClassWrapper.getVersion());
		assertNull(singleTableSubclassWrapper.getVersion());
		assertNull(joinedSubclassWrapper.getVersion());
		assertNull(specialRootClassWrapper.getVersion());
		Property versionTarget = new Property();
		((RootClass)rootClassTarget).setVersion(versionTarget);
		assertSame(versionTarget, rootClassWrapper.getVersion());
		assertSame(versionTarget, singleTableSubclassWrapper.getVersion());
		assertSame(versionTarget, joinedSubclassWrapper.getVersion());
		((RootClass)specialRootClassTarget).setVersion(versionTarget);
		assertSame(versionTarget, specialRootClassWrapper.getVersion());
	}
	
	@Test
	public void testSetClassName() {
		assertNull(rootClassTarget.getClassName());
		assertNull(singleTableSubclassTarget.getClassName());
		assertNull(joinedSubclassTarget.getClassName());
		assertNull(specialRootClassTarget.getClassName());
		rootClassWrapper.setClassName("foo");
		singleTableSubclassWrapper.setClassName("bar");
		joinedSubclassWrapper.setClassName("oof");
		specialRootClassWrapper.setClassName("rab");
		assertEquals("foo", rootClassTarget.getClassName());
		assertEquals("bar", singleTableSubclassTarget.getClassName());
		assertEquals("oof", joinedSubclassTarget.getClassName());
		assertEquals("rab", specialRootClassTarget.getClassName());
	}
	
	@Test
	public void testSetEntityName() {
		assertNull(rootClassTarget.getEntityName());
		assertNull(singleTableSubclassTarget.getEntityName());
		assertNull(joinedSubclassTarget.getEntityName());
		assertNull(specialRootClassTarget.getEntityName());
		rootClassWrapper.setEntityName("foo");
		singleTableSubclassWrapper.setEntityName("bar");
		joinedSubclassWrapper.setEntityName("oof");
		specialRootClassWrapper.setEntityName("rab");
		assertEquals("foo", rootClassTarget.getEntityName());
		assertEquals("bar", singleTableSubclassTarget.getEntityName());
		assertEquals("oof", joinedSubclassTarget.getEntityName());
		assertEquals("rab", specialRootClassTarget.getEntityName());
	}
	
	@Test
	public void testSetDiscriminatorValue() {
		assertNull(rootClassTarget.getDiscriminatorValue());
		assertNull(singleTableSubclassTarget.getDiscriminatorValue());
		assertNull(joinedSubclassTarget.getDiscriminatorValue());
		assertNull(specialRootClassTarget.getDiscriminatorValue());
		rootClassWrapper.setDiscriminatorValue("foo");
		singleTableSubclassWrapper.setDiscriminatorValue("bar");
		joinedSubclassWrapper.setDiscriminatorValue("oof");
		specialRootClassWrapper.setDiscriminatorValue("rab");
		assertEquals("foo", rootClassTarget.getDiscriminatorValue());
		assertEquals("bar", singleTableSubclassTarget.getDiscriminatorValue());
		assertEquals("oof", joinedSubclassTarget.getDiscriminatorValue());
		assertEquals("rab", specialRootClassTarget.getDiscriminatorValue());
	}
	
	@Test
	public void testSetAbstract() {
		assertNull(rootClassTarget.isAbstract());
		assertNull(singleTableSubclassTarget.isAbstract());
		assertNull(joinedSubclassTarget.isAbstract());
		assertNull(specialRootClassTarget.isAbstract());
		rootClassWrapper.setAbstract(true);
		singleTableSubclassWrapper.setAbstract(true);
		joinedSubclassWrapper.setAbstract(true);
		specialRootClassWrapper.setAbstract(true);
		assertTrue(rootClassTarget.isAbstract());
		assertTrue(singleTableSubclassTarget.isAbstract());
		assertTrue(joinedSubclassTarget.isAbstract());
		assertTrue(specialRootClassTarget.isAbstract());
		rootClassWrapper.setAbstract(false);
		singleTableSubclassWrapper.setAbstract(false);
		joinedSubclassWrapper.setAbstract(false);
		specialRootClassWrapper.setAbstract(false);
		assertFalse(rootClassTarget.isAbstract());		
		assertFalse(singleTableSubclassTarget.isAbstract());		
		assertFalse(joinedSubclassTarget.isAbstract());		
		assertFalse(specialRootClassTarget.isAbstract());		
	}
	
	@Test
	public void testAddProperty() {
		try {
			rootClassTarget.getProperty("foo");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					e.getMessage());
		}
		try {
			singleTableSubclassTarget.getProperty("foo");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					e.getMessage());
		}
		try {
			joinedSubclassTarget.getProperty("foo");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					e.getMessage());
		}
		try {
			specialRootClassTarget.getProperty("foo");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					e.getMessage());
		}
		Property propertyTarget = new Property();
		propertyTarget.setName("foo");
		rootClassWrapper.addProperty(propertyTarget);
		assertSame(rootClassTarget.getProperty("foo"), propertyTarget);
		assertSame(singleTableSubclassTarget.getProperty("foo"), propertyTarget);
		assertSame(joinedSubclassTarget.getProperty("foo"), propertyTarget);
		try {
			specialRootClassTarget.getProperty("foo");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					e.getMessage());
		}
		specialRootClassWrapper.addProperty(propertyTarget);
		assertSame(specialRootClassWrapper.getProperty("foo"), propertyTarget);
	}
	
	@Test
	public void testIsInstanceOfJoinedSubclass() {
		assertFalse(rootClassWrapper.isInstanceOfJoinedSubclass());
		assertFalse(singleTableSubclassWrapper.isInstanceOfJoinedSubclass());
		assertTrue(joinedSubclassWrapper.isInstanceOfJoinedSubclass());
		assertFalse(specialRootClassWrapper.isInstanceOfJoinedSubclass());
	}
	
	@Test
	public void testSetTable() {
		Table table = new Table("");
		assertNull(rootClassTarget.getTable());
		assertNull(singleTableSubclassTarget.getTable());
		rootClassWrapper.setTable(table);
		assertSame(table, rootClassTarget.getTable());
		assertSame(table, singleTableSubclassTarget.getTable());
		try {
			singleTableSubclassWrapper.setTable(new Table(""));
			fail();
		} catch (RuntimeException e) {
			assertEquals(e.getMessage(), "Method 'setTable(Table)' is not supported.");
		}
		assertNull(joinedSubclassTarget.getTable());
		joinedSubclassWrapper.setTable(table);
		assertSame(table, joinedSubclassTarget.getTable());
		assertNull(specialRootClassTarget.getTable());
		specialRootClassWrapper.setTable(table);
		assertSame(table, specialRootClassTarget.getTable());
	}	
	
	@Test
	public void testSetKey() {
		KeyValue valueTarget = createValue();
		assertNull(rootClassTarget.getKey());
		assertNull(singleTableSubclassTarget.getKey());
		assertNull(joinedSubclassTarget.getKey());
		assertNull(specialRootClassTarget.getKey());
		try {
			rootClassWrapper.setKey(valueTarget);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(KeyValue) is only allowed on JoinedSubclass", e.getMessage());
		}
		try {
			singleTableSubclassWrapper.setKey(valueTarget);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(KeyValue) is only allowed on JoinedSubclass", e.getMessage());
		}
		joinedSubclassWrapper.setKey(valueTarget);
		assertSame(valueTarget, joinedSubclassTarget.getKey());
		try {
			specialRootClassWrapper.setKey(valueTarget);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(KeyValue) is only allowed on JoinedSubclass", e.getMessage());
		}
	}
	
	@Test
	public void testIsInstanceOfSpecialRootClass() {
		assertFalse(rootClassWrapper.isInstanceOfSpecialRootClass());
		assertFalse(singleTableSubclassWrapper.isInstanceOfSpecialRootClass());
		assertFalse(joinedSubclassWrapper.isInstanceOfSpecialRootClass());
		assertTrue(specialRootClassWrapper.isInstanceOfSpecialRootClass());
	}
	
	@Test
	public void testGetParentProperty() {
		try {
			rootClassWrapper.getParentProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getParentProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
		try {
			singleTableSubclassWrapper.getParentProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getParentProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
		try {
			joinedSubclassWrapper.getParentProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getParentProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
		assertNull(specialRootClassWrapper.getParentProperty());
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		Component component = new Component(DummyMetadataBuildingContext.INSTANCE, pc);
		component.setParentProperty("foo");
		Property property = new Property();
		property.setValue(component);
		property.setPersistentClass(pc);
		specialRootClassWrapper = PersistentClassWrapperFactory.createSpecialRootClassWrapper(property);
		Property parentProperty = specialRootClassWrapper.getParentProperty();
		assertNotNull(parentProperty);
		assertEquals("foo", parentProperty.getName());
	}
	
	@Test
	public void testSetIdentifierProperty() {
		Property property = new Property();
		assertNull(rootClassTarget.getIdentifierProperty());
		rootClassWrapper.setIdentifierProperty(property);
		assertSame(property, rootClassTarget.getIdentifierProperty());
		assertNull(specialRootClassTarget.getIdentifierProperty());
		specialRootClassWrapper.setIdentifierProperty(property);
		assertSame(property, specialRootClassTarget.getIdentifierProperty());
		try {
			singleTableSubclassWrapper.setIdentifierProperty(property);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setIdentifierProperty(Property) is only allowed on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassWrapper.setIdentifierProperty(property);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setIdentifierProperty(Property) is only allowed on RootClass instances", e.getMessage());
		}
	}

	@Test
	public void testSetIdentifier() {
		KeyValue valueTarget = createValue();
		assertNull(rootClassTarget.getIdentifier());
		assertNull(singleTableSubclassTarget.getIdentifier());
		assertNull(joinedSubclassTarget.getIdentifier());
		rootClassWrapper.setIdentifier(valueTarget);
		assertSame(valueTarget, rootClassTarget.getIdentifier());
		assertSame(valueTarget, singleTableSubclassTarget.getIdentifier());
		assertSame(valueTarget, joinedSubclassTarget.getIdentifier());
		try {
			singleTableSubclassWrapper.setIdentifier(valueTarget);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setIdentifier(KeyValue)' can only be called on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassWrapper.setIdentifier(valueTarget);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setIdentifier(KeyValue)' can only be called on RootClass instances", e.getMessage());
		}
		assertNull(specialRootClassTarget.getIdentifier());
		specialRootClassWrapper.setIdentifier(valueTarget);
		assertSame(valueTarget, specialRootClassTarget.getIdentifier());
	}
	
	@Test
	public void testSetDiscriminator() throws Exception {
		Value valueTarget = createValue();
		assertNull(rootClassTarget.getDiscriminator());
		assertNull(singleTableSubclassTarget.getDiscriminator());
		assertNull(joinedSubclassTarget.getDiscriminator());
		assertNull(specialRootClassTarget.getDiscriminator());
		rootClassWrapper.setDiscriminator(valueTarget);
		assertSame(valueTarget, rootClassTarget.getDiscriminator());
		assertSame(valueTarget, singleTableSubclassTarget.getDiscriminator());
		assertSame(valueTarget, joinedSubclassTarget.getDiscriminator());
		try {
			singleTableSubclassWrapper.setDiscriminator(valueTarget);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setDiscriminator(Value)' can only be called on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassWrapper.setDiscriminator(valueTarget);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setDiscriminator(Value)' can only be called on RootClass instances", e.getMessage());
		}
		assertNull(specialRootClassTarget.getDiscriminator());
		specialRootClassWrapper.setDiscriminator(valueTarget);
		assertSame(valueTarget, specialRootClassTarget.getDiscriminator());
	}
	
	@Test
	public void testSetProxyInterfaceName() {
		assertNull(rootClassTarget.getProxyInterfaceName());
		rootClassWrapper.setProxyInterfaceName("foo");
		assertEquals("foo", rootClassTarget.getProxyInterfaceName());
		assertNull(singleTableSubclassTarget.getProxyInterfaceName());
		singleTableSubclassWrapper.setProxyInterfaceName("bar");
		assertEquals("bar", singleTableSubclassTarget.getProxyInterfaceName());
		assertNull(joinedSubclassTarget.getProxyInterfaceName());
		joinedSubclassWrapper.setProxyInterfaceName("oof");
		assertEquals("oof", joinedSubclassTarget.getProxyInterfaceName());
		assertNull(specialRootClassTarget.getProxyInterfaceName());
		specialRootClassWrapper.setProxyInterfaceName("rab");
		assertEquals("rab", specialRootClassTarget.getProxyInterfaceName());
	}
	
	@Test
	public void testSetLazy() {
		rootClassWrapper.setLazy(true);
		assertTrue(rootClassTarget.isLazy());
		rootClassWrapper.setLazy(false);
		assertFalse(rootClassTarget.isLazy());
		singleTableSubclassWrapper.setLazy(true);
		assertTrue(singleTableSubclassTarget.isLazy());
		singleTableSubclassWrapper.setLazy(false);
		assertFalse(singleTableSubclassTarget.isLazy());
		joinedSubclassWrapper.setLazy(true);
		assertTrue(joinedSubclassTarget.isLazy());
		joinedSubclassWrapper.setLazy(false);
		assertFalse(joinedSubclassTarget.isLazy());
		specialRootClassWrapper.setLazy(true);
		assertTrue(specialRootClassTarget.isLazy());
		specialRootClassWrapper.setLazy(false);
		assertFalse(specialRootClassTarget.isLazy());
	}
	
	@Test
	public void testGetSubclassIterator() {
		Iterator<?> subclassIterator = rootClassWrapper.getSubclassIterator();
		assertFalse(subclassIterator.hasNext());
		Subclass firstSubclass = new Subclass(rootClassTarget, DummyMetadataBuildingContext.INSTANCE);
		firstSubclass.setEntityName("first");
		rootClassTarget.addSubclass(firstSubclass);
		subclassIterator = rootClassWrapper.getSubclassIterator();
		assertTrue(subclassIterator.hasNext());
		assertSame(firstSubclass, subclassIterator.next());
		subclassIterator = singleTableSubclassWrapper.getSubclassIterator();
		assertFalse(subclassIterator.hasNext());
		Subclass secondSubclass = new Subclass(singleTableSubclassTarget, DummyMetadataBuildingContext.INSTANCE);
		secondSubclass.setEntityName("second");
		singleTableSubclassTarget.addSubclass(secondSubclass);
		subclassIterator = singleTableSubclassWrapper.getSubclassIterator();
		assertTrue(subclassIterator.hasNext());
		assertSame(secondSubclass, subclassIterator.next());
		subclassIterator = joinedSubclassWrapper.getSubclassIterator();
		assertFalse(subclassIterator.hasNext());
		Subclass thirdSubclass = new Subclass(joinedSubclassTarget, DummyMetadataBuildingContext.INSTANCE);
		thirdSubclass.setEntityName("third");
		joinedSubclassTarget.addSubclass(thirdSubclass);
		subclassIterator = joinedSubclassWrapper.getSubclassIterator();
		assertTrue(subclassIterator.hasNext());
		assertSame(thirdSubclass, subclassIterator.next());
		subclassIterator = specialRootClassWrapper.getSubclassIterator();
		assertFalse(subclassIterator.hasNext());
		Subclass fourthSubclass = new Subclass(joinedSubclassTarget, DummyMetadataBuildingContext.INSTANCE);
		fourthSubclass.setEntityName("four");
		specialRootClassTarget.addSubclass(fourthSubclass);
		subclassIterator = specialRootClassWrapper.getSubclassIterator();
		assertTrue(subclassIterator.hasNext());
		assertSame(fourthSubclass, subclassIterator.next());
	}
	
	@Test
	public void testIsCustomDeleteCallable() {
		rootClassTarget.setCustomSQLDelete("foo", false, null);
		assertFalse(rootClassWrapper.isCustomDeleteCallable());
		rootClassTarget.setCustomSQLDelete("bar", true, null);
		assertTrue(rootClassWrapper.isCustomDeleteCallable());
		singleTableSubclassTarget.setCustomSQLDelete("foo", false, null);
		assertFalse(singleTableSubclassWrapper.isCustomDeleteCallable());
		singleTableSubclassTarget.setCustomSQLDelete("bar", true, null);
		assertTrue(singleTableSubclassWrapper.isCustomDeleteCallable());
		joinedSubclassTarget.setCustomSQLDelete("foo", false, null);
		assertFalse(joinedSubclassWrapper.isCustomDeleteCallable());
		joinedSubclassTarget.setCustomSQLDelete("bar", true, null);
		assertTrue(joinedSubclassWrapper.isCustomDeleteCallable());
		specialRootClassTarget.setCustomSQLDelete("foo", false, null);
		assertFalse(specialRootClassWrapper.isCustomDeleteCallable());
		specialRootClassTarget.setCustomSQLDelete("bar", true, null);
		assertTrue(specialRootClassWrapper.isCustomDeleteCallable());
	}
	
	@Test
	public void testIsCustomInsertCallable() {
		rootClassTarget.setCustomSQLInsert("bar", false, null);
		assertFalse(rootClassWrapper.isCustomInsertCallable());
		rootClassTarget.setCustomSQLInsert("foo", true, null);
		assertTrue(rootClassWrapper.isCustomInsertCallable());
		singleTableSubclassTarget.setCustomSQLInsert("foo", false, null);
		assertFalse(singleTableSubclassWrapper.isCustomInsertCallable());
		singleTableSubclassTarget.setCustomSQLInsert("bar", true, null);
		assertTrue(singleTableSubclassWrapper.isCustomInsertCallable());
		joinedSubclassTarget.setCustomSQLInsert("foo", false, null);
		assertFalse(joinedSubclassWrapper.isCustomInsertCallable());
		joinedSubclassTarget.setCustomSQLInsert("bar", true, null);
		assertTrue(joinedSubclassWrapper.isCustomInsertCallable());
		specialRootClassTarget.setCustomSQLInsert("foo", false, null);
		assertFalse(specialRootClassWrapper.isCustomInsertCallable());
		specialRootClassTarget.setCustomSQLInsert("bar", true, null);
		assertTrue(specialRootClassWrapper.isCustomInsertCallable());
	}
	
	@Test
	public void testIsCustomUpdateCallable() {
		rootClassTarget.setCustomSQLUpdate("foo", false, null);
		assertFalse(rootClassWrapper.isCustomUpdateCallable());
		rootClassTarget.setCustomSQLUpdate("bar", true, null);
		assertTrue(rootClassWrapper.isCustomUpdateCallable());
		singleTableSubclassTarget.setCustomSQLUpdate("foo", false, null);
		assertFalse(singleTableSubclassWrapper.isCustomUpdateCallable());
		singleTableSubclassTarget.setCustomSQLUpdate("bar", true, null);
		assertTrue(singleTableSubclassWrapper.isCustomUpdateCallable());
		joinedSubclassTarget.setCustomSQLUpdate("foo", false, null);
		assertFalse(joinedSubclassWrapper.isCustomUpdateCallable());
		joinedSubclassTarget.setCustomSQLUpdate("bar", true, null);
		assertTrue(joinedSubclassWrapper.isCustomUpdateCallable());
		specialRootClassTarget.setCustomSQLUpdate("foo", false, null);
		assertFalse(specialRootClassWrapper.isCustomUpdateCallable());
		specialRootClassTarget.setCustomSQLUpdate("bar", true, null);
		assertTrue(specialRootClassWrapper.isCustomUpdateCallable());
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
