package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
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
import org.junit.jupiter.api.Test;

public class ValueWrapperFactoryTest {
	
	@Test
	public void testCreateArrayWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Value arrayWrapper = ValueWrapperFactory.createArrayWrapper(persistentClassWrapper);
		assertTrue(arrayWrapper instanceof Array);
		assertSame(((Array)arrayWrapper).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateBagWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Value bagWrapper = ValueWrapperFactory.createBagWrapper(persistentClassWrapper);
		assertTrue(bagWrapper instanceof Bag);
		assertSame(((Bag)bagWrapper).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateListWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Value listWrapper = ValueWrapperFactory.createListWrapper(persistentClassWrapper);
		assertTrue(listWrapper instanceof List);
		assertSame(((List)listWrapper).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateManyToOneWrapper() {
		Table table = new Table("", "foo");
		Value manyToOneWrapper = ValueWrapperFactory.createManyToOneWrapper(table);
		assertTrue(manyToOneWrapper instanceof ManyToOne);
		assertSame(table, manyToOneWrapper.getTable());
	}

	@Test
	public void testCreateMapWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Value mapWrapper = ValueWrapperFactory.createMapWrapper(persistentClassWrapper);
		assertTrue(mapWrapper instanceof Map);
		assertSame(((Map)mapWrapper).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateOneToManyWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Table tableTarget = new Table("", "foo");
		((RootClass)persistentClassTarget).setTable(tableTarget);
		Value oneToManyWrapper = ValueWrapperFactory.createOneToManyWrapper(persistentClassWrapper);
		assertTrue(oneToManyWrapper instanceof OneToMany);
		assertSame(((OneToMany)oneToManyWrapper).getTable(), tableTarget);
	}
	
	@Test
	public void testCreateOneToOneWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Table tableTarget = new Table("", "foo");
		((RootClass)persistentClassTarget).setTable(tableTarget);
		persistentClassTarget.setEntityName("bar");
		Value oneToOneWrapper = ValueWrapperFactory.createOneToOneWrapper(persistentClassWrapper);
		assertTrue(oneToOneWrapper instanceof OneToOne);
		assertEquals(((OneToOne)oneToOneWrapper).getEntityName(), "bar");
		assertSame(((OneToOne)oneToOneWrapper).getTable(), tableTarget);
	}
	
	@Test
	public void testCreatePrimitiveArray() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Value primitiveArrayWrapper = ValueWrapperFactory.createPrimitiveArrayWrapper(persistentClassWrapper);
		assertTrue(primitiveArrayWrapper instanceof PrimitiveArray);
		assertSame(((PrimitiveArray)primitiveArrayWrapper).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateSetWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Value setWrapper = ValueWrapperFactory.createSetWrapper(persistentClassWrapper);
		assertTrue(setWrapper instanceof Set);
		assertSame(((Set)setWrapper).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateSimpleValueWrapper() {
		Value simpleValueWrapper = ValueWrapperFactory.createSimpleValue();
		assertNotNull(simpleValueWrapper);
		assertTrue(simpleValueWrapper instanceof SimpleValue);
	}
	
}
