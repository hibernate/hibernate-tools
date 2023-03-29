package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.wrp.ValueWrapperFactory.ValueWrapper;
import org.junit.jupiter.api.Test;

public class ValueWrapperFactoryTest {
	
	@Test
	public void testCreateArrayWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		ValueWrapper arrayWrapper = ValueWrapperFactory.createArrayWrapper(persistentClassWrapper);
		Value wrappedArray = arrayWrapper.getWrappedObject();
		assertTrue(wrappedArray instanceof Array);
		assertSame(((Array)wrappedArray).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateBagWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		ValueWrapper bagWrapper = ValueWrapperFactory.createBagWrapper(persistentClassWrapper);
		Value wrappedBag = bagWrapper.getWrappedObject();
		assertTrue(wrappedBag instanceof Bag);
		assertSame(((Bag)wrappedBag).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateListWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		ValueWrapper listWrapper = ValueWrapperFactory.createListWrapper(persistentClassWrapper);
		Value wrappedList = listWrapper.getWrappedObject();
		assertTrue(wrappedList instanceof List);
		assertSame(((List)wrappedList).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateManyToOneWrapper() {
		Table table = new Table("", "foo");
		ValueWrapper manyToOneWrapper = ValueWrapperFactory.createManyToOneWrapper(table);
		Value wrappedManyToOne = manyToOneWrapper.getWrappedObject();
		assertTrue(wrappedManyToOne instanceof ManyToOne);
		assertSame(table, wrappedManyToOne.getTable());
	}

	@Test
	public void testCreateMapWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		ValueWrapper mapWrapper = ValueWrapperFactory.createMapWrapper(persistentClassWrapper);
		Value wrappedMap = mapWrapper.getWrappedObject();
		assertTrue(wrappedMap instanceof Map);
		assertSame(((Map)wrappedMap).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateOneToManyWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Table tableTarget = new Table("", "foo");
		((RootClass)persistentClassTarget).setTable(tableTarget);
		ValueWrapper oneToManyWrapper = ValueWrapperFactory.createOneToManyWrapper(persistentClassWrapper);
		Value wrappedOneToMany = oneToManyWrapper.getWrappedObject();
		assertTrue(wrappedOneToMany instanceof OneToMany);
		assertSame(((OneToMany)wrappedOneToMany).getTable(), tableTarget);
	}
	
	@Test
	public void testCreateOneToOneWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Table tableTarget = new Table("", "foo");
		((RootClass)persistentClassTarget).setTable(tableTarget);
		persistentClassTarget.setEntityName("bar");
		ValueWrapper oneToOneWrapper = ValueWrapperFactory.createOneToOneWrapper(persistentClassWrapper);
		Value wrappedOneToOne = oneToOneWrapper.getWrappedObject();
		assertTrue(wrappedOneToOne instanceof OneToOne);
		assertEquals(((OneToOne)wrappedOneToOne).getEntityName(), "bar");
		assertSame(((OneToOne)wrappedOneToOne).getTable(), tableTarget);
	}
	
	@Test
	public void testCreatePrimitiveArray() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		ValueWrapper primitiveArrayWrapper = ValueWrapperFactory.createPrimitiveArrayWrapper(persistentClassWrapper);
		Value wrappedPrimitiveArray = primitiveArrayWrapper.getWrappedObject();
		assertTrue(wrappedPrimitiveArray instanceof PrimitiveArray);
		assertSame(((PrimitiveArray)wrappedPrimitiveArray).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateSetWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		ValueWrapper setWrapper = ValueWrapperFactory.createSetWrapper(persistentClassWrapper);
		Value wrappedSet = setWrapper.getWrappedObject();
		assertTrue(wrappedSet instanceof Set);
		assertSame(((Set)wrappedSet).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateSimpleValueWrapper() {
		ValueWrapper simpleValueWrapper = ValueWrapperFactory.createSimpleValueWrapper();
		Value wrappedSimpleValue = simpleValueWrapper.getWrappedObject();
		assertNotNull(wrappedSimpleValue);
		assertTrue(wrappedSimpleValue instanceof SimpleValue);
	}
	
	@Test
	public void testCreateComponentValue() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Value componentWrapper = ValueWrapperFactory.createComponentWrapper(persistentClassWrapper);
		assertTrue(componentWrapper instanceof Component);
		assertSame(((Component)componentWrapper).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateValueWrapper() {
		Value valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		Value valueWrapper = ValueWrapperFactory.createValueWrapper(valueTarget);
		assertNotNull(valueWrapper);
		assertSame(valueTarget, ((Wrapper)valueWrapper).getWrappedObject());
	}
	
}
