package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.internal.factory.PersistentClassWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValueWrapperTest {

	private ValueWrapper arrayValueWrapper = null;
	private Value wrappedArrayValue = null;
	private ValueWrapper bagValueWrapper = null;
	private Value wrappedBagValue = null;
	private ValueWrapper listValueWrapper = null;
	private Value wrappedListValue = null;
	private ValueWrapper manyToOneValueWrapper = null;
	private Value wrappedManyToOneValue = null;
	private ValueWrapper mapValueWrapper = null;
	private Value wrappedMapValue = null;
	private ValueWrapper oneToManyValueWrapper = null;
	private Value wrappedOneToManyValue = null;
	private ValueWrapper oneToOneValueWrapper = null;
	private Value wrappedOneToOneValue = null;
	private ValueWrapper primitiveArrayValueWrapper = null;
	private Value wrappedPrimitiveArrayValue = null;
	private ValueWrapper setValueWrapper = null;
	private Value wrappedSetValue = null;
	private ValueWrapper simpleValueWrapper = null;
	private Value wrappedSimpleValue = null;
	private ValueWrapper componentValueWrapper = null;
	private Value wrappedComponentValue = null;
	private ValueWrapper dependantValueWrapper = null;
	private Value wrappedDependantValue = null;
	private ValueWrapper anyValueWrapper = null;
	private Value wrappedAnyValue = null;
	private ValueWrapper identifierBagValueWrapper = null;
	private Value wrappedIdentifierBagValue = null;
	
	private PersistentClassWrapper persistentClassWrapper = null;
	private PersistentClass wrappedPersistentClass = null;
	
	private Table table = null;
	
	@BeforeEach 
	public void beforeEach() {
		persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		wrappedPersistentClass = persistentClassWrapper.getWrappedObject();
		
		table = new Table("HT");
		
		wrappedArrayValue = new Array(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		arrayValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedArrayValue);
		
		wrappedBagValue = new Bag(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		bagValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedBagValue);
		
		wrappedListValue = new List(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		listValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedListValue);
		
		wrappedManyToOneValue = new ManyToOne(DummyMetadataBuildingContext.INSTANCE, table);
		manyToOneValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedManyToOneValue);
		
		wrappedMapValue = new Map(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		mapValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedMapValue);
		
		wrappedOneToManyValue = new OneToMany(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		oneToManyValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedOneToManyValue);
		
		wrappedOneToOneValue = new OneToOne(DummyMetadataBuildingContext.INSTANCE, table, wrappedPersistentClass);
		oneToOneValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedOneToOneValue);
		
		wrappedPrimitiveArrayValue = new PrimitiveArray(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		primitiveArrayValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedPrimitiveArrayValue);
		
		wrappedSetValue = new Set(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		setValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedSetValue);
		
		wrappedSimpleValue = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		simpleValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedSimpleValue);
		
		wrappedComponentValue = new Component(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		componentValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedComponentValue);
		
		wrappedDependantValue = new DependantValue(DummyMetadataBuildingContext.INSTANCE, table, (KeyValue)wrappedSimpleValue);
		dependantValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedDependantValue);
				
		wrappedAnyValue = new Any(DummyMetadataBuildingContext.INSTANCE, table);
		anyValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedAnyValue);
		
		wrappedIdentifierBagValue = new IdentifierBag(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		identifierBagValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedIdentifierBagValue);
		
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(arrayValueWrapper);
		assertNotNull(wrappedArrayValue);
		assertNotNull(bagValueWrapper);
		assertNotNull(wrappedBagValue);
		assertNotNull(listValueWrapper);
		assertNotNull(wrappedListValue);
		assertNotNull(manyToOneValueWrapper);
		assertNotNull(wrappedManyToOneValue);
		assertNotNull(mapValueWrapper);
		assertNotNull(wrappedMapValue);
		assertNotNull(oneToManyValueWrapper);
		assertNotNull(wrappedOneToManyValue);
		assertNotNull(oneToOneValueWrapper);
		assertNotNull(wrappedOneToOneValue);
		assertNotNull(primitiveArrayValueWrapper);
		assertNotNull(wrappedPrimitiveArrayValue);
		assertNotNull(setValueWrapper);
		assertNotNull(wrappedSetValue);
		assertNotNull(simpleValueWrapper);
		assertNotNull(wrappedSimpleValue);
		assertNotNull(componentValueWrapper);
		assertNotNull(wrappedComponentValue);
		assertNotNull(dependantValueWrapper);
		assertNotNull(wrappedDependantValue);
		assertNotNull(anyValueWrapper);
		assertNotNull(wrappedAnyValue);
		assertNotNull(identifierBagValueWrapper);
		assertNotNull(wrappedIdentifierBagValue);
	}

	@Test
	public void testIsSimpleValue() {
		assertFalse(arrayValueWrapper.isSimpleValue());
		assertFalse(bagValueWrapper.isSimpleValue());
		assertFalse(listValueWrapper.isSimpleValue());
		assertTrue(manyToOneValueWrapper.isSimpleValue());
		assertFalse(mapValueWrapper.isSimpleValue());
		assertFalse(oneToManyValueWrapper.isSimpleValue());
		assertTrue(oneToOneValueWrapper.isSimpleValue());
		assertFalse(primitiveArrayValueWrapper.isSimpleValue());
		assertFalse(setValueWrapper.isSimpleValue());
		assertTrue(simpleValueWrapper.isSimpleValue());
		assertTrue(componentValueWrapper.isSimpleValue());
		assertTrue(dependantValueWrapper.isSimpleValue());
		assertTrue(anyValueWrapper.isSimpleValue());
		assertFalse(identifierBagValueWrapper.isSimpleValue());
	}

}
