package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import java.util.Properties;

import org.hibernate.FetchMode;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Fetchable;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.internal.factory.ColumnWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.PersistentClassWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.TableWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.DummyMetadataBuildingContext;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.hibernate.type.BagType;
import org.hibernate.type.BasicType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.IdentifierBagType;
import org.hibernate.type.ListType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.MapType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.SetType;
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

	private Table wrappedTable = null;
	private TableWrapper tableWrapper = null;

	@BeforeEach
	public void beforeEach() {
		wrappedPersistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		persistentClassWrapper = PersistentClassWrapperFactory.createPersistentClassWrapper(wrappedPersistentClass);

		tableWrapper = TableWrapperFactory.createTableWrapper("HT");
		wrappedTable = (Table)tableWrapper.getWrappedObject();

		wrappedArrayValue = new Array(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		arrayValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedArrayValue);

		wrappedBagValue = new Bag(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		bagValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedBagValue);

		wrappedListValue = new List(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		listValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedListValue);

		wrappedManyToOneValue = new ManyToOne(DummyMetadataBuildingContext.INSTANCE, wrappedTable);
		manyToOneValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedManyToOneValue);

		wrappedMapValue = new Map(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		mapValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedMapValue);

		wrappedOneToManyValue = new OneToMany(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		oneToManyValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedOneToManyValue);

		wrappedOneToOneValue = new OneToOne(DummyMetadataBuildingContext.INSTANCE, wrappedTable, wrappedPersistentClass);
		oneToOneValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedOneToOneValue);

		wrappedPrimitiveArrayValue = new PrimitiveArray(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		primitiveArrayValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedPrimitiveArrayValue);

		wrappedSetValue = new Set(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		setValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedSetValue);

		wrappedSimpleValue = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		simpleValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedSimpleValue);

		wrappedComponentValue = new Component(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		componentValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedComponentValue);

		wrappedDependantValue = new DependantValue(DummyMetadataBuildingContext.INSTANCE, wrappedTable,
				(KeyValue) wrappedSimpleValue);
		dependantValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedDependantValue);

		wrappedAnyValue = new Any(DummyMetadataBuildingContext.INSTANCE, wrappedTable);
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

	@Test
	public void testIsCollection() {
		assertTrue(arrayValueWrapper.isCollection());
		assertTrue(bagValueWrapper.isCollection());
		assertTrue(listValueWrapper.isCollection());
		assertFalse(manyToOneValueWrapper.isCollection());
		assertTrue(mapValueWrapper.isCollection());
		assertFalse(oneToManyValueWrapper.isCollection());
		assertFalse(oneToOneValueWrapper.isCollection());
		assertTrue(primitiveArrayValueWrapper.isCollection());
		assertTrue(setValueWrapper.isCollection());
		assertFalse(simpleValueWrapper.isCollection());
		assertFalse(componentValueWrapper.isCollection());
		assertFalse(dependantValueWrapper.isCollection());
		assertFalse(anyValueWrapper.isCollection());
		assertTrue(identifierBagValueWrapper.isCollection());
	}

	@Test
	public void testGetCollectionElement() {
		assertNull(arrayValueWrapper.getCollectionElement());
		((Collection)wrappedArrayValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, arrayValueWrapper.getCollectionElement().getWrappedObject());
		assertNull(bagValueWrapper.getCollectionElement());
		((Collection)wrappedBagValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue,bagValueWrapper.getCollectionElement().getWrappedObject());
		assertNull(listValueWrapper.getCollectionElement());
		((Collection)wrappedListValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, listValueWrapper.getCollectionElement().getWrappedObject());
		assertNull(manyToOneValueWrapper.getCollectionElement());
		assertNull(mapValueWrapper.getCollectionElement());
		((Collection)wrappedMapValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, mapValueWrapper.getCollectionElement().getWrappedObject());
		assertNull(oneToManyValueWrapper.getCollectionElement());
		assertNull(oneToOneValueWrapper.getCollectionElement());
		assertNull(primitiveArrayValueWrapper.getCollectionElement());
		((Collection)wrappedPrimitiveArrayValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, primitiveArrayValueWrapper.getCollectionElement().getWrappedObject());
		assertNull(setValueWrapper.getCollectionElement());
		((Collection)wrappedSetValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, setValueWrapper.getCollectionElement().getWrappedObject());
		assertNull(simpleValueWrapper.getCollectionElement());
		assertNull(componentValueWrapper.getCollectionElement());
		assertNull(dependantValueWrapper.getCollectionElement());
		assertNull(anyValueWrapper.getCollectionElement());
		assertNull(identifierBagValueWrapper.getCollectionElement());
		((Collection)wrappedIdentifierBagValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, identifierBagValueWrapper.getCollectionElement().getWrappedObject());
	}

	@Test 
	public void testIsOneToMany() {
		assertFalse(arrayValueWrapper.isOneToMany());
		assertFalse(bagValueWrapper.isOneToMany());
		assertFalse(listValueWrapper.isOneToMany());
		assertFalse(manyToOneValueWrapper.isOneToMany());
		assertFalse(mapValueWrapper.isOneToMany());
		assertTrue(oneToManyValueWrapper.isOneToMany());
		assertFalse(oneToOneValueWrapper.isOneToMany());
		assertFalse(primitiveArrayValueWrapper.isOneToMany());
		assertFalse(setValueWrapper.isOneToMany());
		assertFalse(simpleValueWrapper.isOneToMany());
		assertFalse(componentValueWrapper.isOneToMany());
		assertFalse(dependantValueWrapper.isOneToMany());
		assertFalse(anyValueWrapper.isOneToMany());
		assertFalse(identifierBagValueWrapper.isOneToMany());
	}
	
	@Test 
	public void testIsManyToOne() {
		assertFalse(arrayValueWrapper.isManyToOne());
		assertFalse(bagValueWrapper.isManyToOne());
		assertFalse(listValueWrapper.isManyToOne());
		assertTrue(manyToOneValueWrapper.isManyToOne());
		assertFalse(mapValueWrapper.isManyToOne());
		assertFalse(oneToManyValueWrapper.isManyToOne());
		assertFalse(oneToOneValueWrapper.isManyToOne());
		assertFalse(primitiveArrayValueWrapper.isManyToOne());
		assertFalse(setValueWrapper.isManyToOne());
		assertFalse(simpleValueWrapper.isManyToOne());
		assertFalse(componentValueWrapper.isManyToOne());
		assertFalse(dependantValueWrapper.isManyToOne());
		assertFalse(anyValueWrapper.isManyToOne());
		assertFalse(identifierBagValueWrapper.isManyToOne());
	}

	@Test 
	public void testIsOneToOne() {
		assertFalse(arrayValueWrapper.isOneToOne());
		assertFalse(bagValueWrapper.isOneToOne());
		assertFalse(listValueWrapper.isOneToOne());
		assertFalse(manyToOneValueWrapper.isOneToOne());
		assertFalse(mapValueWrapper.isOneToOne());
		assertFalse(oneToManyValueWrapper.isOneToOne());
		assertTrue(oneToOneValueWrapper.isOneToOne());
		assertFalse(primitiveArrayValueWrapper.isOneToOne());
		assertFalse(setValueWrapper.isOneToOne());
		assertFalse(simpleValueWrapper.isOneToOne());
		assertFalse(componentValueWrapper.isOneToOne());
		assertFalse(dependantValueWrapper.isOneToOne());
		assertFalse(anyValueWrapper.isOneToOne());
		assertFalse(identifierBagValueWrapper.isOneToOne());
	}

	@Test 
	public void testIsMap() {
		assertFalse(arrayValueWrapper.isMap());
		assertFalse(bagValueWrapper.isMap());
		assertFalse(listValueWrapper.isMap());
		assertFalse(manyToOneValueWrapper.isMap());
		assertTrue(mapValueWrapper.isMap());
		assertFalse(oneToManyValueWrapper.isMap());
		assertFalse(oneToOneValueWrapper.isMap());
		assertFalse(primitiveArrayValueWrapper.isMap());
		assertFalse(setValueWrapper.isMap());
		assertFalse(simpleValueWrapper.isMap());
		assertFalse(componentValueWrapper.isMap());
		assertFalse(dependantValueWrapper.isMap());
		assertFalse(anyValueWrapper.isMap());
		assertFalse(identifierBagValueWrapper.isMap());
	}
	
	@Test
	public void testIsComponent() {
		assertFalse(arrayValueWrapper.isComponent());
		assertFalse(bagValueWrapper.isComponent());
		assertFalse(listValueWrapper.isComponent());
		assertFalse(manyToOneValueWrapper.isComponent());
		assertFalse(mapValueWrapper.isComponent());
		assertFalse(oneToManyValueWrapper.isComponent());
		assertFalse(oneToOneValueWrapper.isComponent());
		assertFalse(primitiveArrayValueWrapper.isComponent());
		assertFalse(setValueWrapper.isComponent());
		assertFalse(simpleValueWrapper.isComponent());
		assertTrue(componentValueWrapper.isComponent());
		assertFalse(dependantValueWrapper.isComponent());
		assertFalse(anyValueWrapper.isComponent());
		assertFalse(identifierBagValueWrapper.isComponent());
	}

	@Test 
	public void testIsEmbedded() {
		assertFalse(arrayValueWrapper.isEmbedded());
		assertFalse(bagValueWrapper.isEmbedded());
		assertFalse(listValueWrapper.isEmbedded());
		assertFalse(manyToOneValueWrapper.isEmbedded());
		assertFalse(mapValueWrapper.isEmbedded());
		assertFalse(oneToManyValueWrapper.isEmbedded());
		assertFalse(oneToOneValueWrapper.isEmbedded());
		assertFalse(primitiveArrayValueWrapper.isEmbedded());
		assertFalse(setValueWrapper.isEmbedded());
		assertFalse(simpleValueWrapper.isEmbedded());
		assertFalse(componentValueWrapper.isEmbedded());
		((Component)wrappedComponentValue).setEmbedded(true);
		assertTrue(componentValueWrapper.isEmbedded());
		assertFalse(dependantValueWrapper.isEmbedded());
		assertFalse(anyValueWrapper.isEmbedded());
		assertFalse(identifierBagValueWrapper.isEmbedded());
	}

	@Test
	public void testIsToOne() {
		assertFalse(arrayValueWrapper.isToOne());
		assertFalse(bagValueWrapper.isToOne());
		assertFalse(listValueWrapper.isToOne());
		assertTrue(manyToOneValueWrapper.isToOne());
		assertFalse(mapValueWrapper.isToOne());
		assertFalse(oneToManyValueWrapper.isToOne());
		assertTrue(oneToOneValueWrapper.isToOne());
		assertFalse(primitiveArrayValueWrapper.isToOne());
		assertFalse(setValueWrapper.isToOne());
		assertFalse(simpleValueWrapper.isToOne());
		assertFalse(componentValueWrapper.isToOne());
		assertFalse(dependantValueWrapper.isToOne());
		assertFalse(anyValueWrapper.isToOne());
		assertFalse(identifierBagValueWrapper.isToOne());
	}
	
	@Test
	public void testGetTable() {
		((RootClass)wrappedPersistentClass).setTable(null);
		assertNull(arrayValueWrapper.getTable());
		((RootClass)wrappedPersistentClass).setTable(wrappedTable);
		assertSame(wrappedTable, arrayValueWrapper.getTable().getWrappedObject());
		((RootClass)wrappedPersistentClass).setTable(null);
		assertNull(bagValueWrapper.getTable());
		((RootClass)wrappedPersistentClass).setTable(wrappedTable);
		assertSame(wrappedTable, bagValueWrapper.getTable().getWrappedObject());
		((RootClass)wrappedPersistentClass).setTable(null);
		assertNull(listValueWrapper.getTable());
		((RootClass)wrappedPersistentClass).setTable(wrappedTable);
		assertSame(wrappedTable, listValueWrapper.getTable().getWrappedObject());
		((ManyToOne)wrappedManyToOneValue).setTable(null);
		assertNull(manyToOneValueWrapper.getTable());
		((ManyToOne)wrappedManyToOneValue).setTable(wrappedTable);
		assertSame(wrappedTable, manyToOneValueWrapper.getTable().getWrappedObject());
		((RootClass)wrappedPersistentClass).setTable(null);
		assertNull(mapValueWrapper.getTable());
		((RootClass)wrappedPersistentClass).setTable(wrappedTable);
		assertSame(wrappedTable, mapValueWrapper.getTable().getWrappedObject());
		assertNull(oneToManyValueWrapper.getTable());
		((RootClass)wrappedPersistentClass).setTable(wrappedTable);
		wrappedOneToManyValue = new OneToMany(DummyMetadataBuildingContext.INSTANCE, wrappedPersistentClass);
		oneToManyValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedOneToManyValue);
		assertSame(wrappedTable, oneToManyValueWrapper.getTable().getWrappedObject());
		assertSame(wrappedTable, oneToOneValueWrapper.getTable().getWrappedObject());
		((OneToOne)wrappedOneToOneValue).setTable(null);
		assertNull(oneToOneValueWrapper.getTable());
		((RootClass)wrappedPersistentClass).setTable(null);
		assertNull(primitiveArrayValueWrapper.getTable());
		((RootClass)wrappedPersistentClass).setTable(wrappedTable);
		assertSame(wrappedTable, primitiveArrayValueWrapper.getTable().getWrappedObject());
		((RootClass)wrappedPersistentClass).setTable(null);
		assertNull(setValueWrapper.getTable());
		((RootClass)wrappedPersistentClass).setTable(wrappedTable);
		assertSame(wrappedTable, setValueWrapper.getTable().getWrappedObject());
		((SimpleValue)wrappedSimpleValue).setTable(null);
		assertNull(simpleValueWrapper.getTable());
		((SimpleValue)wrappedSimpleValue).setTable(wrappedTable);
		assertSame(wrappedTable, simpleValueWrapper.getTable().getWrappedObject());
		((Component)wrappedComponentValue).setTable(null);
		assertNull(componentValueWrapper.getTable());
		((Component)wrappedComponentValue).setTable(wrappedTable);
		assertSame(wrappedTable, componentValueWrapper.getTable().getWrappedObject());
		((SimpleValue)wrappedDependantValue).setTable(null);
		assertNull(dependantValueWrapper.getTable());
		((SimpleValue)wrappedDependantValue).setTable(wrappedTable);
		assertSame(wrappedTable, dependantValueWrapper.getTable().getWrappedObject());
		assertSame(wrappedTable, anyValueWrapper.getTable().getWrappedObject());
		((Any)wrappedAnyValue).setTable(null);
		assertNull(anyValueWrapper.getTable());
		((RootClass)wrappedPersistentClass).setTable(null);
		assertNull(identifierBagValueWrapper.getTable());
		((RootClass)wrappedPersistentClass).setTable(wrappedTable);
		assertSame(wrappedTable, identifierBagValueWrapper.getTable().getWrappedObject());
	}

	@Test
	public void testGetType() {
		((SimpleValue)wrappedSimpleValue).setTypeName("java.lang.Integer");
		TypeWrapper type = simpleValueWrapper.getType();
		assertEquals("integer", type.getName());
		((Collection)wrappedArrayValue).setElement(wrappedSimpleValue);
		type = arrayValueWrapper.getType();
		assertEquals("[Ljava.lang.Integer;(null)", type.getName());
		assertTrue(type.getWrappedObject() instanceof ArrayType);
		((Collection)wrappedBagValue).setElement(wrappedSimpleValue);
		type = bagValueWrapper.getType();
		assertEquals("java.util.Collection(null)", type.getName());
		assertTrue(type.getWrappedObject() instanceof BagType);
		((Collection)wrappedListValue).setElement(wrappedSimpleValue);
		type = listValueWrapper.getType();
		assertEquals("java.util.List(null)", type.getName());
		assertTrue(type.getWrappedObject() instanceof ListType);
		type = manyToOneValueWrapper.getType();
		assertEquals(null, type.getName());
		assertTrue(type.getWrappedObject() instanceof ManyToOneType);
		((Collection)wrappedMapValue).setElement(wrappedSimpleValue);
		type = mapValueWrapper.getType();
		assertEquals("java.util.Map(null)", type.getName());
		assertTrue(type.getWrappedObject() instanceof MapType);
		type = oneToManyValueWrapper.getType();
		assertEquals(null, type.getName());
		assertTrue(type.getWrappedObject() instanceof ManyToOneType);
		type = oneToOneValueWrapper.getType();
		assertEquals(null, type.getName());
		assertTrue(type.getWrappedObject() instanceof OneToOneType);
		((Collection)wrappedPrimitiveArrayValue).setElement(wrappedSimpleValue);
		type = primitiveArrayValueWrapper.getType();
		assertEquals("[I(null)", type.getName());
		assertTrue(type.getWrappedObject() instanceof ArrayType);
		((Collection)wrappedSetValue).setElement(wrappedSimpleValue);
		type = setValueWrapper.getType();
		assertEquals("java.util.Set(null)", type.getName());
		assertTrue(type.getWrappedObject() instanceof SetType);
		((Component)wrappedComponentValue).setComponentClassName("java.lang.String");
		type = componentValueWrapper.getType();
		assertEquals("component[]", type.getName());
		assertTrue(type.getWrappedObject() instanceof ComponentType);
		type = dependantValueWrapper.getType();
		assertEquals("integer", type.getName());
		assertTrue(type.getWrappedObject() instanceof BasicType);
		((Any)wrappedAnyValue).setIdentifierType("java.lang.Integer");
		type = anyValueWrapper.getType();
		assertEquals("object", type.getName());
		assertTrue(type.getWrappedObject() instanceof AnyType);
		((Collection)wrappedIdentifierBagValue).setElement(wrappedSimpleValue);
		type = identifierBagValueWrapper.getType();
		assertEquals("java.util.Collection(null)", type.getName());
		assertTrue(type.getWrappedObject() instanceof IdentifierBagType);
	}
	
	@Test
	public void testSetElement() {
		assertNull(((Collection)wrappedArrayValue).getElement());
		arrayValueWrapper.setElement(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedArrayValue).getElement());
		assertNull(((Collection)wrappedBagValue).getElement());
		bagValueWrapper.setElement(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedBagValue).getElement());
		assertNull(((Collection)wrappedListValue).getElement());
		listValueWrapper.setElement(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedListValue).getElement());
		// next call has no effect
		manyToOneValueWrapper.setElement(simpleValueWrapper);
		assertNull(((Collection)wrappedMapValue).getElement());
		mapValueWrapper.setElement(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedMapValue).getElement());
		// next call has no effect
		oneToManyValueWrapper.setElement(simpleValueWrapper);
		// next call has no effect
		oneToOneValueWrapper.setElement(simpleValueWrapper);
		assertNull(((Collection)wrappedPrimitiveArrayValue).getElement());
		primitiveArrayValueWrapper.setElement(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedPrimitiveArrayValue).getElement());
		assertNull(((Collection)wrappedSetValue).getElement());
		setValueWrapper.setElement(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedSetValue).getElement());
		// next call has no effect
		simpleValueWrapper.setElement(arrayValueWrapper);
		// next call has no effect
		componentValueWrapper.setElement(arrayValueWrapper);
		// next call has no effect
		dependantValueWrapper.setElement(arrayValueWrapper);
		// next call has no effect
		anyValueWrapper.setElement(arrayValueWrapper);
		assertNull(((Collection)wrappedIdentifierBagValue).getElement());
		identifierBagValueWrapper.setElement(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedIdentifierBagValue).getElement());
	}
	
	@Test
	public void testSetCollectionTable() {
		assertNull(((Collection)wrappedArrayValue).getCollectionTable());
		arrayValueWrapper.setCollectionTable(tableWrapper);
		assertSame(wrappedTable, ((Collection)wrappedArrayValue).getCollectionTable());
		assertNull(((Collection)wrappedBagValue).getCollectionTable());
		bagValueWrapper.setCollectionTable(tableWrapper);
		assertSame(wrappedTable, ((Collection)wrappedBagValue).getCollectionTable());
		assertNull(((Collection)wrappedListValue).getCollectionTable());
		listValueWrapper.setCollectionTable(tableWrapper);
		assertSame(wrappedTable, ((Collection)wrappedListValue).getCollectionTable());
		// next call has no effect
		manyToOneValueWrapper.setCollectionTable(tableWrapper);
		assertNull(((Collection)wrappedMapValue).getCollectionTable());
		mapValueWrapper.setCollectionTable(tableWrapper);
		assertSame(wrappedTable, ((Collection)wrappedMapValue).getCollectionTable());
		// next call has no effect
		oneToManyValueWrapper.setCollectionTable(tableWrapper);
		// next call has no effect
		oneToOneValueWrapper.setCollectionTable(tableWrapper);
		assertNull(((Collection)wrappedPrimitiveArrayValue).getCollectionTable());
		primitiveArrayValueWrapper.setCollectionTable(tableWrapper);
		assertSame(wrappedTable, ((Collection)wrappedPrimitiveArrayValue).getCollectionTable());
		assertNull(((Collection)wrappedSetValue).getCollectionTable());
		setValueWrapper.setCollectionTable(tableWrapper);
		assertSame(wrappedTable, ((Collection)wrappedSetValue).getCollectionTable());
		// next call has no effect
		simpleValueWrapper.setCollectionTable(tableWrapper);
		// next call has no effect
		componentValueWrapper.setCollectionTable(tableWrapper);
		// next call has no effect
		dependantValueWrapper.setCollectionTable(tableWrapper);
		// next call has no effect
		anyValueWrapper.setCollectionTable(tableWrapper);
		assertNull(((Collection)wrappedIdentifierBagValue).getCollectionTable());
		identifierBagValueWrapper.setCollectionTable(tableWrapper);
		assertSame(wrappedTable, ((Collection)wrappedIdentifierBagValue).getCollectionTable());
	}
	
	@Test
	public void testSetTable() {
		assertNull(wrappedArrayValue.getTable());
		arrayValueWrapper.setTable(tableWrapper);
		assertNull(wrappedArrayValue.getTable());
		assertNull(wrappedBagValue.getTable());
		bagValueWrapper.setTable(tableWrapper);
		assertNull(wrappedBagValue.getTable());
		assertNull(wrappedListValue.getTable());
		listValueWrapper.setTable(tableWrapper);
		assertNull(wrappedListValue.getTable());
		assertSame(wrappedTable, wrappedManyToOneValue.getTable());
		manyToOneValueWrapper.setTable(null);
		assertNull(wrappedManyToOneValue.getTable());
		assertNull(wrappedMapValue.getTable());
		mapValueWrapper.setTable(tableWrapper);
		assertNull(wrappedMapValue.getTable());
		assertNull(wrappedOneToManyValue.getTable());
		oneToManyValueWrapper.setTable(tableWrapper);
		assertNull(wrappedOneToManyValue.getTable());
		assertSame(wrappedTable, wrappedOneToOneValue.getTable());
		oneToOneValueWrapper.setTable(null);
		assertNull(wrappedOneToOneValue.getTable());
		assertNull(wrappedPrimitiveArrayValue.getTable());
		primitiveArrayValueWrapper.setTable(tableWrapper);
		assertNull(wrappedPrimitiveArrayValue.getTable());
		assertNull(wrappedSetValue.getTable());
		setValueWrapper.setTable(tableWrapper);
		assertNull(wrappedSetValue.getTable());
		assertNull(wrappedSimpleValue.getTable());
		simpleValueWrapper.setTable(tableWrapper);
		assertSame(wrappedTable, wrappedSimpleValue.getTable());
		assertNull(wrappedComponentValue.getTable());
		componentValueWrapper.setTable(tableWrapper);
		assertSame(wrappedTable, wrappedComponentValue.getTable());
		assertSame(wrappedTable, wrappedDependantValue.getTable());
		dependantValueWrapper.setTable(null);
		assertNull(wrappedDependantValue.getTable());
		assertSame(wrappedTable, wrappedAnyValue.getTable());
		anyValueWrapper.setTable(null);
		assertNull(wrappedAnyValue.getTable());
		assertNull(wrappedIdentifierBagValue.getTable());
		identifierBagValueWrapper.setTable(tableWrapper);
		assertNull(wrappedIdentifierBagValue.getTable());
	}
	
	@Test 
	public void testIsList() {
		assertTrue(arrayValueWrapper.isList());
		assertFalse(bagValueWrapper.isList());
		assertTrue(listValueWrapper.isList());
		assertFalse(manyToOneValueWrapper.isList());
		assertFalse(mapValueWrapper.isList());
		assertFalse(oneToManyValueWrapper.isList());
		assertFalse(oneToOneValueWrapper.isList());
		assertTrue(primitiveArrayValueWrapper.isList());
		assertFalse(setValueWrapper.isList());
		assertFalse(simpleValueWrapper.isList());
		assertFalse(componentValueWrapper.isList());
		assertFalse(dependantValueWrapper.isList());
		assertFalse(anyValueWrapper.isList());
		assertFalse(identifierBagValueWrapper.isList());
	}
	
	@Test
	public void testSetIndex() {
		assertNull(((IndexedCollection)wrappedArrayValue).getIndex());
		arrayValueWrapper.setIndex(simpleValueWrapper);
		assertSame(wrappedSimpleValue,((IndexedCollection)wrappedArrayValue).getIndex());
		// next call has no effect
		bagValueWrapper.setIndex(simpleValueWrapper);
		assertNull(((IndexedCollection)wrappedListValue).getIndex());
		listValueWrapper.setIndex(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((IndexedCollection)wrappedListValue).getIndex());
		// next call has no effect
		manyToOneValueWrapper.setIndex(simpleValueWrapper);
		assertNull(((IndexedCollection)wrappedMapValue).getIndex());
		mapValueWrapper.setIndex(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((IndexedCollection)wrappedMapValue).getIndex());
		// next call has no effect
		oneToManyValueWrapper.setIndex(simpleValueWrapper);
		// next call has no effect
		oneToOneValueWrapper.setIndex(simpleValueWrapper);
		assertNull(((IndexedCollection)wrappedPrimitiveArrayValue).getIndex());
		primitiveArrayValueWrapper.setIndex(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((IndexedCollection)wrappedPrimitiveArrayValue).getIndex());
		// next call has no effect
		setValueWrapper.setIndex(simpleValueWrapper);
		// next call has no effect
		simpleValueWrapper.setIndex(simpleValueWrapper);
		// next call has no effect
		componentValueWrapper.setIndex(simpleValueWrapper);
		// next call has no effect
		dependantValueWrapper.setIndex(simpleValueWrapper);
		// next call has no effect
		anyValueWrapper.setIndex(simpleValueWrapper);
		// next call has no effect
		identifierBagValueWrapper.setIndex(simpleValueWrapper);
	}
	
	@Test
	public void testSetTypeName() {
		assertNull(((Collection)wrappedArrayValue).getTypeName());
		arrayValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((Collection)wrappedArrayValue).getTypeName());
		assertNull(((Collection)wrappedBagValue).getTypeName());
		bagValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((Collection)wrappedBagValue).getTypeName());
		assertNull(((Collection)wrappedListValue).getTypeName());
		listValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((Collection)wrappedListValue).getTypeName());
		assertNull(((SimpleValue)wrappedManyToOneValue).getTypeName());
		manyToOneValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)wrappedManyToOneValue).getTypeName());
		assertNull(((Collection)wrappedMapValue).getTypeName());
		mapValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((Collection)wrappedMapValue).getTypeName());
		// next call has no effect
		oneToManyValueWrapper.setTypeName("foobar");
		assertNull(((SimpleValue)wrappedOneToOneValue).getTypeName());
		oneToOneValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)wrappedOneToOneValue).getTypeName());
		assertNull(((Collection)wrappedPrimitiveArrayValue).getTypeName());
		primitiveArrayValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((Collection)wrappedPrimitiveArrayValue).getTypeName());
		assertNull(((Collection)wrappedSetValue).getTypeName());
		setValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((Collection)wrappedSetValue).getTypeName());
		assertNull(((SimpleValue)wrappedSimpleValue).getTypeName());
		simpleValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)wrappedSimpleValue).getTypeName());
		assertNull(((SimpleValue)wrappedComponentValue).getTypeName());
		componentValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)wrappedComponentValue).getTypeName());
		assertNull(((SimpleValue)wrappedDependantValue).getTypeName());
		dependantValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)wrappedDependantValue).getTypeName());
		assertNull(((SimpleValue)wrappedAnyValue).getTypeName());
		anyValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)wrappedAnyValue).getTypeName());
		assertNull(((Collection)wrappedIdentifierBagValue).getTypeName());
		identifierBagValueWrapper.setTypeName("foobar");
		assertEquals("foobar", ((Collection)wrappedIdentifierBagValue).getTypeName());
	}
	
	@Test
	public void testGetComponentClassName() {
		assertNull(arrayValueWrapper.getComponentClassName());
		assertNull(bagValueWrapper.getComponentClassName());
		assertNull(listValueWrapper.getComponentClassName());
		assertNull(manyToOneValueWrapper.getComponentClassName());
		assertNull(mapValueWrapper.getComponentClassName());
		assertNull(oneToManyValueWrapper.getComponentClassName());
		assertNull(oneToOneValueWrapper.getComponentClassName());
		assertNull(primitiveArrayValueWrapper.getComponentClassName());
		assertNull(setValueWrapper.getComponentClassName());
		assertNull(simpleValueWrapper.getComponentClassName());
		assertNull(componentValueWrapper.getComponentClassName());
		((Component)wrappedComponentValue).setComponentClassName("foobar");
		assertEquals("foobar", componentValueWrapper.getComponentClassName());
		assertNull(dependantValueWrapper.getComponentClassName());
		assertNull(anyValueWrapper.getComponentClassName());
		assertNull(identifierBagValueWrapper.getComponentClassName());
	}
	
	@Test
	public void testGetColumnIterator() {
		Iterator<ColumnWrapper> columnIterator = null;
		Column column = new Column("foo");
		// collection values have no columns
		assertFalse(arrayValueWrapper.getColumnIterator().hasNext());
		assertFalse(bagValueWrapper.getColumnIterator().hasNext());
		assertFalse(listValueWrapper.getColumnIterator().hasNext());
		assertFalse(mapValueWrapper.getColumnIterator().hasNext());
		assertFalse(primitiveArrayValueWrapper.getColumnIterator().hasNext());
		assertFalse(setValueWrapper.getColumnIterator().hasNext());
		assertFalse(identifierBagValueWrapper.getColumnIterator().hasNext());
		// one to many value columns are the ones of the associated class
		RootClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		BasicValue kv = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		kv.setTable(new Table(""));
		pc.setIdentifier(kv);
		((OneToMany)wrappedOneToManyValue).setAssociatedClass(pc);
		assertFalse(oneToManyValueWrapper.getColumnIterator().hasNext());
		kv.addColumn(column);
		columnIterator = oneToManyValueWrapper.getColumnIterator();
		ColumnWrapper cw = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(cw.getWrappedObject(), column);
		// simple value case
		((SimpleValue)wrappedSimpleValue).setTable(new Table(""));
		assertFalse(simpleValueWrapper.getColumnIterator().hasNext());
		((SimpleValue)wrappedSimpleValue).addColumn(column);
		columnIterator = simpleValueWrapper.getColumnIterator();
		cw = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(cw.getWrappedObject(), column);
		// component value case
		assertFalse(componentValueWrapper.getColumnIterator().hasNext());
		Property p = new Property();
		p.setValue(kv);
		((Component)wrappedComponentValue).addProperty(p);
		columnIterator = componentValueWrapper.getColumnIterator();
		cw = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(cw.getWrappedObject(), column);
		// many to one value
		assertFalse(manyToOneValueWrapper.getColumnIterator().hasNext());
		((ManyToOne)wrappedManyToOneValue).addColumn(column);
		columnIterator = manyToOneValueWrapper.getColumnIterator();
		cw = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(cw.getWrappedObject(), column);
		// one to one value
		assertFalse(oneToOneValueWrapper.getColumnIterator().hasNext());
		((OneToOne)wrappedOneToOneValue).addColumn(column);
		columnIterator = oneToOneValueWrapper.getColumnIterator();
		cw = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(cw.getWrappedObject(), column);
		// dependant value case
		((DependantValue)wrappedDependantValue).setTable(new Table(""));
		assertFalse(dependantValueWrapper.getColumnIterator().hasNext());
		((DependantValue)wrappedDependantValue).addColumn(column);
		columnIterator = dependantValueWrapper.getColumnIterator();
		cw = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(cw.getWrappedObject(), column);
		// any value case
		((Any)wrappedAnyValue).setTable(new Table(""));
		assertFalse(anyValueWrapper.getColumnIterator().hasNext());
		((Any)wrappedAnyValue).addColumn(column);
		columnIterator = anyValueWrapper.getColumnIterator();
		cw = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(cw.getWrappedObject(), column);
	}
	
	@Test
	public void testIsTypeSpecified() {
		try {
			arrayValueWrapper.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			bagValueWrapper.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			listValueWrapper.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			mapValueWrapper.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			oneToManyValueWrapper.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			primitiveArrayValueWrapper.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			setValueWrapper.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			identifierBagValueWrapper.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		assertFalse(manyToOneValueWrapper.isTypeSpecified());
		((ManyToOne)wrappedManyToOneValue).setTypeName("foo");
		assertFalse(manyToOneValueWrapper.isTypeSpecified());
		((ManyToOne)wrappedManyToOneValue).setReferencedEntityName("foo");
		assertTrue(manyToOneValueWrapper.isTypeSpecified());
		assertFalse(oneToOneValueWrapper.isTypeSpecified());
		((OneToOne)wrappedOneToOneValue).setTypeName("foo");
		assertFalse(oneToOneValueWrapper.isTypeSpecified());
		((OneToOne)wrappedOneToOneValue).setReferencedEntityName("foo");
		assertTrue(manyToOneValueWrapper.isTypeSpecified());
		assertFalse(simpleValueWrapper.isTypeSpecified());
		((SimpleValue)wrappedSimpleValue).setTypeName("foo");
		assertTrue(simpleValueWrapper.isTypeSpecified());
		assertFalse(componentValueWrapper.isTypeSpecified());
		((Component)wrappedComponentValue).setTypeName("foo");
		assertTrue(componentValueWrapper.isTypeSpecified());
		assertFalse(dependantValueWrapper.isTypeSpecified());
		((DependantValue)wrappedDependantValue).setTypeName("foo");
		assertTrue(dependantValueWrapper.isTypeSpecified());
		assertFalse(anyValueWrapper.isTypeSpecified());
		((Any)wrappedAnyValue).setTypeName("foo");
		assertTrue(anyValueWrapper.isTypeSpecified());
	}
	
	@Test
	public void testGetCollectionTable() {
		assertNull(arrayValueWrapper.getCollectionTable());
		((Collection)wrappedArrayValue).setCollectionTable(wrappedTable);
		assertSame(wrappedTable, arrayValueWrapper.getCollectionTable().getWrappedObject());
		assertNull(bagValueWrapper.getCollectionTable());
		((Collection)wrappedBagValue).setCollectionTable(wrappedTable);
		assertSame(wrappedTable, bagValueWrapper.getCollectionTable().getWrappedObject());
		assertNull(listValueWrapper.getCollectionTable());
		((Collection)wrappedListValue).setCollectionTable(wrappedTable);
		assertSame(wrappedTable, listValueWrapper.getCollectionTable().getWrappedObject());
		assertNull(manyToOneValueWrapper.getCollectionTable());
		assertNull(mapValueWrapper.getCollectionTable());
		((Collection)wrappedMapValue).setCollectionTable(wrappedTable);
		assertSame(wrappedTable, mapValueWrapper.getCollectionTable().getWrappedObject());
		assertNull(oneToManyValueWrapper.getCollectionTable());
		assertNull(oneToOneValueWrapper.getCollectionTable());
		assertNull(primitiveArrayValueWrapper.getCollectionTable());
		((Collection)wrappedPrimitiveArrayValue).setCollectionTable(wrappedTable);
		assertSame(wrappedTable, primitiveArrayValueWrapper.getCollectionTable().getWrappedObject());
		assertNull(setValueWrapper.getCollectionTable());
		((Collection)wrappedSetValue).setCollectionTable(wrappedTable);
		assertSame(wrappedTable, setValueWrapper.getCollectionTable().getWrappedObject());
		assertNull(simpleValueWrapper.getCollectionTable());
		assertNull(componentValueWrapper.getCollectionTable());
		assertNull(dependantValueWrapper.getCollectionTable());
		assertNull(anyValueWrapper.getCollectionTable());
		assertNull(identifierBagValueWrapper.getCollectionTable());
		((Collection)wrappedIdentifierBagValue).setCollectionTable(wrappedTable);
		assertSame(wrappedTable, identifierBagValueWrapper.getCollectionTable().getWrappedObject());
	}
	
	@Test
	public void testGetKey() {
		assertNull(arrayValueWrapper.getKey());
		((Collection)wrappedArrayValue).setKey((KeyValue)wrappedSimpleValue);
		assertSame(wrappedSimpleValue, arrayValueWrapper.getKey().getWrappedObject());
		assertNull(bagValueWrapper.getKey());
		((Collection)wrappedBagValue).setKey((KeyValue)wrappedSimpleValue);
		assertSame(wrappedSimpleValue, bagValueWrapper.getKey().getWrappedObject());
		assertNull(listValueWrapper.getKey());
		((Collection)wrappedListValue).setKey((KeyValue)wrappedSimpleValue);
		assertSame(wrappedSimpleValue, listValueWrapper.getKey().getWrappedObject());
		assertNull(mapValueWrapper.getKey());
		((Collection)wrappedMapValue).setKey((KeyValue)wrappedSimpleValue);
		assertSame(wrappedSimpleValue, mapValueWrapper.getKey().getWrappedObject());
		assertNull(primitiveArrayValueWrapper.getKey());
		((Collection)wrappedPrimitiveArrayValue).setKey((KeyValue)wrappedSimpleValue);
		assertSame(wrappedSimpleValue, primitiveArrayValueWrapper.getKey().getWrappedObject());
		assertNull(setValueWrapper.getKey());
		((Collection)wrappedSetValue).setKey((KeyValue)wrappedSimpleValue);
		assertSame(wrappedSimpleValue, setValueWrapper.getKey().getWrappedObject());
		assertNull(identifierBagValueWrapper.getKey());
		((Collection)wrappedIdentifierBagValue).setKey((KeyValue)wrappedSimpleValue);
		assertSame(wrappedSimpleValue, identifierBagValueWrapper.getKey().getWrappedObject());
		try {
			simpleValueWrapper.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
		try {
			manyToOneValueWrapper.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
		try {
			oneToOneValueWrapper.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
		try {
			componentValueWrapper.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
		try {
			dependantValueWrapper.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
		try {
			anyValueWrapper.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
	}
	
	@Test
	public void testGetIndex() {
		assertNull(arrayValueWrapper.getIndex());
		((IndexedCollection)wrappedArrayValue).setIndex(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, arrayValueWrapper.getIndex().getWrappedObject());
		assertNull(bagValueWrapper.getIndex());
		assertNull(listValueWrapper.getIndex());
		((IndexedCollection)wrappedListValue).setIndex(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, listValueWrapper.getIndex().getWrappedObject());
		assertNull(manyToOneValueWrapper.getIndex());
		assertNull(mapValueWrapper.getIndex());
		((IndexedCollection)wrappedMapValue).setIndex(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, mapValueWrapper.getIndex().getWrappedObject());
		assertNull(oneToManyValueWrapper.getIndex());
		assertNull(oneToOneValueWrapper.getIndex());
		assertNull(primitiveArrayValueWrapper.getIndex());
		((IndexedCollection)wrappedPrimitiveArrayValue).setIndex(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, primitiveArrayValueWrapper.getIndex().getWrappedObject());
		assertNull(setValueWrapper.getIndex());
		assertNull(simpleValueWrapper.getIndex());
		assertNull(componentValueWrapper.getIndex());
		assertNull(dependantValueWrapper.getIndex());
		assertNull(anyValueWrapper.getIndex());
		assertNull(identifierBagValueWrapper.getIndex());
	}	
	
	@Test
	public void testGetElementClassName() {
		// only supported by array values
		assertNull(arrayValueWrapper.getElementClassName());
		((Array)wrappedArrayValue).setElementClassName("foo");
		assertEquals("foo", arrayValueWrapper.getElementClassName());
		assertNull(primitiveArrayValueWrapper.getElementClassName());
		((PrimitiveArray)wrappedPrimitiveArrayValue).setElementClassName("foo");
		assertEquals("foo", primitiveArrayValueWrapper.getElementClassName());
		try {
			bagValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			listValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			setValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			mapValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			simpleValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			manyToOneValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			oneToOneValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			oneToManyValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			componentValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			dependantValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			anyValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			identifierBagValueWrapper.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
	}
		
	@Test
	public void testGetTypeName() {
		assertNull(arrayValueWrapper.getTypeName());
		((Collection)wrappedArrayValue).setTypeName("foobar");
		assertEquals("foobar", arrayValueWrapper.getTypeName());
		assertNull(bagValueWrapper.getTypeName());
		((Collection)wrappedBagValue).setTypeName("foobar");
		assertEquals("foobar", bagValueWrapper.getTypeName());
		assertNull(listValueWrapper.getTypeName());
		((Collection)wrappedListValue).setTypeName("foobar");
		assertEquals("foobar", listValueWrapper.getTypeName());
		assertNull(manyToOneValueWrapper.getTypeName());
		((SimpleValue)wrappedManyToOneValue).setTypeName("foobar");
		assertEquals("foobar", manyToOneValueWrapper.getTypeName());
		assertNull(mapValueWrapper.getTypeName());
		((Collection)wrappedMapValue).setTypeName("foobar");
		assertEquals("foobar", mapValueWrapper.getTypeName());
		assertNull(oneToManyValueWrapper.getTypeName());
		assertNull(oneToOneValueWrapper.getTypeName());
		((SimpleValue)wrappedOneToOneValue).setTypeName("foobar");
		assertEquals("foobar", oneToOneValueWrapper.getTypeName());
		assertNull(primitiveArrayValueWrapper.getTypeName());
		((Collection)wrappedPrimitiveArrayValue).setTypeName("foobar");
		assertEquals("foobar", primitiveArrayValueWrapper.getTypeName());
		assertNull(setValueWrapper.getTypeName());
		((Collection)wrappedSetValue).setTypeName("foobar");
		assertEquals("foobar", setValueWrapper.getTypeName());
		assertNull(simpleValueWrapper.getTypeName());
		((SimpleValue)wrappedSimpleValue).setTypeName("foobar");
		assertEquals("foobar", simpleValueWrapper.getTypeName());
		assertNull(componentValueWrapper.getTypeName());
		((SimpleValue)wrappedComponentValue).setTypeName("foobar");
		assertEquals("foobar", componentValueWrapper.getTypeName());
		assertNull(dependantValueWrapper.getTypeName());
		((SimpleValue)wrappedDependantValue).setTypeName("foobar");
		assertEquals("foobar", dependantValueWrapper.getTypeName());
		assertNull(anyValueWrapper.getTypeName());
		((SimpleValue)wrappedAnyValue).setTypeName("foobar");
		assertEquals("foobar", dependantValueWrapper.getTypeName());
		assertNull(identifierBagValueWrapper.getTypeName());
		((Collection)wrappedIdentifierBagValue).setTypeName("foobar");
		assertEquals("foobar", identifierBagValueWrapper.getTypeName());
	}
	
	@Test
	public void testIsDependantValue() {
		assertFalse(arrayValueWrapper.isDependantValue());
		assertFalse(bagValueWrapper.isDependantValue());
		assertFalse(listValueWrapper.isDependantValue());
		assertFalse(manyToOneValueWrapper.isDependantValue());
		assertFalse(mapValueWrapper.isDependantValue());
		assertFalse(oneToManyValueWrapper.isDependantValue());
		assertFalse(oneToOneValueWrapper.isDependantValue());
		assertFalse(primitiveArrayValueWrapper.isDependantValue());
		assertFalse(setValueWrapper.isDependantValue());
		assertFalse(simpleValueWrapper.isDependantValue());
		assertFalse(componentValueWrapper.isDependantValue());
		assertTrue(dependantValueWrapper.isDependantValue());
		assertFalse(anyValueWrapper.isDependantValue());
		assertFalse(identifierBagValueWrapper.isDependantValue());
	}
	
	@Test
	public void testIsAny() {
		assertFalse(arrayValueWrapper.isAny());
		assertFalse(bagValueWrapper.isAny());
		assertFalse(listValueWrapper.isAny());
		assertFalse(manyToOneValueWrapper.isAny());
		assertFalse(mapValueWrapper.isAny());
		assertFalse(oneToManyValueWrapper.isAny());
		assertFalse(oneToOneValueWrapper.isAny());
		assertFalse(primitiveArrayValueWrapper.isAny());
		assertFalse(setValueWrapper.isAny());
		assertFalse(simpleValueWrapper.isAny());
		assertFalse(componentValueWrapper.isAny());
		assertFalse(dependantValueWrapper.isAny());
		assertTrue(anyValueWrapper.isAny());
		assertFalse(identifierBagValueWrapper.isAny());
	}
	
	@Test
	public void testIsSet() {
		assertFalse(arrayValueWrapper.isSet());
		assertFalse(bagValueWrapper.isSet());
		assertFalse(listValueWrapper.isSet());
		assertFalse(manyToOneValueWrapper.isSet());
		assertFalse(mapValueWrapper.isSet());
		assertFalse(oneToManyValueWrapper.isSet());
		assertFalse(oneToOneValueWrapper.isSet());
		assertFalse(primitiveArrayValueWrapper.isSet());
		assertTrue(setValueWrapper.isSet());
		assertFalse(simpleValueWrapper.isSet());
		assertFalse(componentValueWrapper.isSet());
		assertFalse(dependantValueWrapper.isSet());
		assertFalse(anyValueWrapper.isSet());
		assertFalse(identifierBagValueWrapper.isSet());
	}
	
	@Test
	public void testIsPrimitiveArray() {
		assertFalse(arrayValueWrapper.isPrimitiveArray());
		assertFalse(bagValueWrapper.isPrimitiveArray());
		assertFalse(listValueWrapper.isPrimitiveArray());
		assertFalse(manyToOneValueWrapper.isPrimitiveArray());
		assertFalse(mapValueWrapper.isPrimitiveArray());
		assertFalse(oneToManyValueWrapper.isPrimitiveArray());
		assertFalse(oneToOneValueWrapper.isPrimitiveArray());
		assertTrue(primitiveArrayValueWrapper.isPrimitiveArray());
		assertFalse(setValueWrapper.isPrimitiveArray());
		assertFalse(simpleValueWrapper.isPrimitiveArray());
		assertFalse(componentValueWrapper.isPrimitiveArray());
		assertFalse(dependantValueWrapper.isPrimitiveArray());
		assertFalse(anyValueWrapper.isPrimitiveArray());
		assertFalse(identifierBagValueWrapper.isPrimitiveArray());
	}
		
	@Test
	public void testIsArray() {
		assertTrue(arrayValueWrapper.isArray());
		assertFalse(bagValueWrapper.isArray());
		assertFalse(listValueWrapper.isArray());
		assertFalse(manyToOneValueWrapper.isArray());
		assertFalse(mapValueWrapper.isArray());
		assertFalse(oneToManyValueWrapper.isArray());
		assertFalse(oneToOneValueWrapper.isArray());
		assertTrue(primitiveArrayValueWrapper.isArray());
		assertFalse(setValueWrapper.isArray());
		assertFalse(simpleValueWrapper.isArray());
		assertFalse(componentValueWrapper.isArray());
		assertFalse(dependantValueWrapper.isArray());
		assertFalse(anyValueWrapper.isArray());
		assertFalse(identifierBagValueWrapper.isArray());
	}
		
	@Test
	public void testIsIdentifierBag() {
		assertFalse(arrayValueWrapper.isIdentifierBag());
		assertFalse(bagValueWrapper.isIdentifierBag());
		assertFalse(listValueWrapper.isIdentifierBag());
		assertFalse(manyToOneValueWrapper.isIdentifierBag());
		assertFalse(mapValueWrapper.isIdentifierBag());
		assertFalse(oneToManyValueWrapper.isIdentifierBag());
		assertFalse(oneToOneValueWrapper.isIdentifierBag());
		assertFalse(primitiveArrayValueWrapper.isIdentifierBag());
		assertFalse(setValueWrapper.isIdentifierBag());
		assertFalse(simpleValueWrapper.isIdentifierBag());
		assertFalse(componentValueWrapper.isIdentifierBag());
		assertFalse(dependantValueWrapper.isIdentifierBag());
		assertFalse(anyValueWrapper.isIdentifierBag());
		assertTrue(identifierBagValueWrapper.isIdentifierBag());
	}
	
	@Test
	public void testIsBag() {
		assertFalse(arrayValueWrapper.isBag());
		assertTrue(bagValueWrapper.isBag());
		assertFalse(listValueWrapper.isBag());
		assertFalse(manyToOneValueWrapper.isBag());
		assertFalse(mapValueWrapper.isBag());
		assertFalse(oneToManyValueWrapper.isBag());
		assertFalse(oneToOneValueWrapper.isBag());
		assertFalse(primitiveArrayValueWrapper.isBag());
		assertFalse(setValueWrapper.isBag());
		assertFalse(simpleValueWrapper.isBag());
		assertFalse(componentValueWrapper.isBag());
		assertFalse(dependantValueWrapper.isBag());
		assertFalse(anyValueWrapper.isBag());
		assertFalse(identifierBagValueWrapper.isBag());
	}

	@Test
	public void testGetReferencedEntityName() {
		assertNull(manyToOneValueWrapper.getReferencedEntityName());
		((ManyToOne)wrappedManyToOneValue).setReferencedEntityName("foobar");
		assertEquals("foobar", manyToOneValueWrapper.getReferencedEntityName());
		assertNull(oneToManyValueWrapper.getReferencedEntityName());
		((OneToMany)wrappedOneToManyValue).setReferencedEntityName("foobar");
		assertEquals("foobar", oneToManyValueWrapper.getReferencedEntityName());
		assertNull(oneToOneValueWrapper.getReferencedEntityName());
		((OneToOne)wrappedOneToOneValue).setReferencedEntityName("foobar");
		assertEquals("foobar", oneToOneValueWrapper.getReferencedEntityName());
		try {
			arrayValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			bagValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			listValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			mapValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			primitiveArrayValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			setValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			simpleValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			componentValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			dependantValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			anyValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			identifierBagValueWrapper.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
	}

	@Test
	public void testGetEntityName() {
		try {
			arrayValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			bagValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			listValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			manyToOneValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			mapValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			oneToManyValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		assertNull(oneToOneValueWrapper.getEntityName());
		wrappedPersistentClass.setEntityName("foobar");
		wrappedOneToOneValue = new OneToOne(DummyMetadataBuildingContext.INSTANCE, wrappedTable, wrappedPersistentClass);
		oneToOneValueWrapper = ValueWrapperFactory.createValueWrapper(wrappedOneToOneValue);
		assertEquals("foobar", oneToOneValueWrapper.getEntityName());
		try {
			primitiveArrayValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			setValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			simpleValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			componentValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			dependantValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			anyValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			identifierBagValueWrapper.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
	}
	
	@Test
	public void testGetPropertyIterator() {
		// only the component values have properties
		Iterator<PropertyWrapper> propertyIterator = componentValueWrapper.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		Property p = new Property();
		((Component)wrappedComponentValue).addProperty(p);
		propertyIterator = componentValueWrapper.getPropertyIterator();
		PropertyWrapper propertyWrapper = propertyIterator.next();
		assertFalse(propertyIterator.hasNext());
		assertSame(p, propertyWrapper.getWrappedObject());
		// other values do not support 'getPropertyIterator()'
		try {
			arrayValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			bagValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			listValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			manyToOneValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			mapValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			oneToManyValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			oneToOneValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			primitiveArrayValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			setValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			simpleValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			dependantValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			anyValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			identifierBagValueWrapper.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
	}
	
	@Test
	public void testAddColumn() {
		Column column = new Column("foo");
		ColumnWrapper columnWrapper = ColumnWrapperFactory.createColumnWrapper(column);
		assertFalse(wrappedManyToOneValue.getColumns().contains(column));
		manyToOneValueWrapper.addColumn(columnWrapper);
		assertTrue(wrappedManyToOneValue.getColumns().contains(column));
		assertFalse(wrappedOneToOneValue.getColumns().contains(column));
		oneToOneValueWrapper.addColumn(columnWrapper);
		assertTrue(wrappedOneToOneValue.getColumns().contains(column));
		((BasicValue)wrappedSimpleValue).setTable(wrappedTable);
		assertFalse(wrappedSimpleValue.getColumns().contains(column));
		simpleValueWrapper.addColumn(columnWrapper);
		assertTrue(wrappedSimpleValue.getColumns().contains(column));
		assertFalse(wrappedDependantValue.getColumns().contains(column));
		dependantValueWrapper.addColumn(columnWrapper);
		assertTrue(wrappedDependantValue.getColumns().contains(column));
		assertFalse(wrappedAnyValue.getColumns().contains(column));
		anyValueWrapper.addColumn(columnWrapper);
		assertTrue(wrappedAnyValue.getColumns().contains(column));
		try {
			arrayValueWrapper.addColumn(columnWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			bagValueWrapper.addColumn(columnWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			listValueWrapper.addColumn(columnWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			mapValueWrapper.addColumn(columnWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			oneToManyValueWrapper.addColumn(columnWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			primitiveArrayValueWrapper.addColumn(columnWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			setValueWrapper.addColumn(columnWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			componentValueWrapper.addColumn(columnWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("Cant add a column to a component"));
		}
		try {
			identifierBagValueWrapper.addColumn(columnWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
	}
	
	@Test
	public void testSetTypeParameters() {
		Properties properties = new Properties();
		assertNull(((Collection)wrappedArrayValue).getTypeParameters());
		arrayValueWrapper.setTypeParameters(properties);
		assertSame(((Collection)wrappedArrayValue).getTypeParameters(), properties);
		assertNull(((Collection)wrappedBagValue).getTypeParameters());
		bagValueWrapper.setTypeParameters(properties);
		assertSame(((Collection)wrappedBagValue).getTypeParameters(), properties);
		assertNull(((Collection)wrappedListValue).getTypeParameters());
		listValueWrapper.setTypeParameters(properties);
		assertSame(((Collection)wrappedListValue).getTypeParameters(), properties);
		assertNull(((SimpleValue)wrappedManyToOneValue).getTypeParameters());
		manyToOneValueWrapper.setTypeParameters(properties);
		assertSame(((SimpleValue)wrappedManyToOneValue).getTypeParameters(), properties);
		assertNull(((Collection)wrappedMapValue).getTypeParameters());
		mapValueWrapper.setTypeParameters(properties);
		assertSame(((Collection)wrappedMapValue).getTypeParameters(), properties);
		assertNull(((SimpleValue)wrappedOneToOneValue).getTypeParameters());
		oneToOneValueWrapper.setTypeParameters(properties);
		assertSame(((SimpleValue)wrappedOneToOneValue).getTypeParameters(), properties);
		assertNull(((Collection)wrappedPrimitiveArrayValue).getTypeParameters());
		primitiveArrayValueWrapper.setTypeParameters(properties);
		assertSame(((Collection)wrappedPrimitiveArrayValue).getTypeParameters(), properties);
		assertNull(((Collection)wrappedSetValue).getTypeParameters());
		setValueWrapper.setTypeParameters(properties);
		assertSame(((Collection)wrappedSetValue).getTypeParameters(), properties);
		assertNull(((SimpleValue)wrappedSimpleValue).getTypeParameters());
		simpleValueWrapper.setTypeParameters(properties);
		assertSame(((SimpleValue)wrappedSimpleValue).getTypeParameters(), properties);
		assertNull(((SimpleValue)wrappedComponentValue).getTypeParameters());
		componentValueWrapper.setTypeParameters(properties);
		assertSame(((SimpleValue)wrappedComponentValue).getTypeParameters(), properties);
		assertNull(((SimpleValue)wrappedDependantValue).getTypeParameters());
		dependantValueWrapper.setTypeParameters(properties);
		assertSame(((SimpleValue)wrappedDependantValue).getTypeParameters(), properties);
		assertNull(((SimpleValue)wrappedAnyValue).getTypeParameters());
		anyValueWrapper.setTypeParameters(properties);
		assertSame(((SimpleValue)wrappedAnyValue).getTypeParameters(), properties);
		assertNull(((Collection)wrappedIdentifierBagValue).getTypeParameters());
		identifierBagValueWrapper.setTypeParameters(properties);
		assertSame(((Collection)wrappedIdentifierBagValue).getTypeParameters(), properties);
		try {
			oneToManyValueWrapper.setTypeParameters(properties);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setTypeParameters(Properties)'"));
		}
	}
	
	@Test
	public void testGetForeignKeyName() {
		assertNull(manyToOneValueWrapper.getForeignKeyName());
		((SimpleValue)wrappedManyToOneValue).setForeignKeyName("foo");
		assertEquals("foo", manyToOneValueWrapper.getForeignKeyName());
		assertNull(oneToOneValueWrapper.getForeignKeyName());
		((SimpleValue)wrappedOneToOneValue).setForeignKeyName("foo");
		assertEquals("foo", oneToOneValueWrapper.getForeignKeyName());
		assertNull(simpleValueWrapper.getForeignKeyName());
		((SimpleValue)wrappedSimpleValue).setForeignKeyName("foo");
		assertEquals("foo", simpleValueWrapper.getForeignKeyName());
		assertNull(componentValueWrapper.getForeignKeyName());
		((SimpleValue)wrappedComponentValue).setForeignKeyName("foo");
		assertEquals("foo", componentValueWrapper.getForeignKeyName());
		assertNull(dependantValueWrapper.getForeignKeyName());
		((SimpleValue)wrappedDependantValue).setForeignKeyName("foo");
		assertEquals("foo", dependantValueWrapper.getForeignKeyName());
		assertNull(anyValueWrapper.getForeignKeyName());
		((SimpleValue)wrappedAnyValue).setForeignKeyName("foo");
		assertEquals("foo", anyValueWrapper.getForeignKeyName());
		try {
			arrayValueWrapper.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			bagValueWrapper.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			listValueWrapper.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			mapValueWrapper.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			oneToManyValueWrapper.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			primitiveArrayValueWrapper.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			setValueWrapper.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			identifierBagValueWrapper.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
	}
	
	@Test
	public void testGetOwner() {
		assertSame(wrappedPersistentClass, arrayValueWrapper.getOwner().getWrappedObject());
		assertSame(wrappedPersistentClass, bagValueWrapper.getOwner().getWrappedObject());
		assertSame(wrappedPersistentClass, listValueWrapper.getOwner().getWrappedObject());
		assertSame(wrappedPersistentClass, mapValueWrapper.getOwner().getWrappedObject());
		assertSame(wrappedPersistentClass, primitiveArrayValueWrapper.getOwner().getWrappedObject());
		assertSame(wrappedPersistentClass, setValueWrapper.getOwner().getWrappedObject());
		assertSame(wrappedPersistentClass, identifierBagValueWrapper.getOwner().getWrappedObject());
		assertSame(wrappedPersistentClass, componentValueWrapper.getOwner().getWrappedObject());
		try {
			manyToOneValueWrapper.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
		try {
			oneToManyValueWrapper.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
		try {
			oneToOneValueWrapper.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
		try {
			simpleValueWrapper.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
		try {
			dependantValueWrapper.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
		try {
			anyValueWrapper.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
	}
	
	@Test
	public void testGetElement() {
		assertNull(arrayValueWrapper.getElement());
		((Collection)wrappedArrayValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, arrayValueWrapper.getElement().getWrappedObject());
		assertNull(bagValueWrapper.getElement());
		((Collection)wrappedBagValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, bagValueWrapper.getElement().getWrappedObject());
		assertNull(listValueWrapper.getElement());
		((Collection)wrappedListValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, listValueWrapper.getElement().getWrappedObject());
		assertNull(manyToOneValueWrapper.getElement());
		assertNull(mapValueWrapper.getElement());
		((Collection)wrappedMapValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, mapValueWrapper.getElement().getWrappedObject());
		assertNull(oneToManyValueWrapper.getElement());
		assertNull(oneToOneValueWrapper.getElement());
		assertNull(primitiveArrayValueWrapper.getElement());
		((Collection)wrappedPrimitiveArrayValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, primitiveArrayValueWrapper.getElement().getWrappedObject());
		assertNull(setValueWrapper.getElement());
		((Collection)wrappedSetValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, setValueWrapper.getElement().getWrappedObject());
		assertNull(simpleValueWrapper.getElement());
		assertNull(componentValueWrapper.getElement());
		assertNull(dependantValueWrapper.getElement());
		assertNull(anyValueWrapper.getElement());
		assertNull(identifierBagValueWrapper.getElement());
		((Collection)wrappedIdentifierBagValue).setElement(wrappedSimpleValue);
		assertSame(wrappedSimpleValue, identifierBagValueWrapper.getElement().getWrappedObject());
	}
	
	@Test
	public void testGetParentProperty() {
		assertNull(componentValueWrapper.getParentProperty());
		((Component)wrappedComponentValue).setParentProperty("foo");
		assertEquals("foo", componentValueWrapper.getParentProperty());
		try {
			arrayValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			bagValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			listValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			manyToOneValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			oneToOneValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			mapValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			oneToManyValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			primitiveArrayValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			setValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			simpleValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			dependantValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			anyValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			identifierBagValueWrapper.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
	}
	
	@Test
	public void testSetElementClassName() {
		assertNull(((Array)wrappedArrayValue).getElementClassName());
		arrayValueWrapper.setElementClassName("foo");
		assertEquals("foo", ((Array)wrappedArrayValue).getElementClassName());
		assertNull(((Array)wrappedPrimitiveArrayValue).getElementClassName());
		primitiveArrayValueWrapper.setElementClassName("foo");
		assertEquals("foo", ((Array)wrappedPrimitiveArrayValue).getElementClassName());
		try {
			bagValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			listValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			manyToOneValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			mapValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			oneToManyValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			oneToOneValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			setValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			simpleValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			componentValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			dependantValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			anyValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			identifierBagValueWrapper.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
	}
	
	@Test
	public void testSetKey() {
		assertNull(((Collection)wrappedArrayValue).getKey());
		arrayValueWrapper.setKey(simpleValueWrapper);
		assertSame(wrappedSimpleValue,((Collection)wrappedArrayValue).getKey());
		assertNull(((Collection)wrappedBagValue).getKey());
		bagValueWrapper.setKey(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedBagValue).getKey());
		assertNull(((Collection)wrappedListValue).getKey());
		listValueWrapper.setKey(simpleValueWrapper);
		assertSame(wrappedSimpleValue,((Collection)wrappedListValue).getKey());
		assertNull(((Collection)wrappedMapValue).getKey());
		mapValueWrapper.setKey(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedMapValue).getKey());
		assertNull(((Collection)wrappedPrimitiveArrayValue).getKey());
		primitiveArrayValueWrapper.setKey(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedPrimitiveArrayValue).getKey());
		assertNull(((Collection)wrappedSetValue).getKey());
		setValueWrapper.setKey(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedSetValue).getKey());
		assertNull(((Collection)wrappedIdentifierBagValue).getKey());
		identifierBagValueWrapper.setKey(simpleValueWrapper);
		assertSame(wrappedSimpleValue, ((Collection)wrappedIdentifierBagValue).getKey());
		try {
			manyToOneValueWrapper.setKey(simpleValueWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			oneToManyValueWrapper.setKey(simpleValueWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			oneToOneValueWrapper.setKey(simpleValueWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			simpleValueWrapper.setKey(simpleValueWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			componentValueWrapper.setKey(simpleValueWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			dependantValueWrapper.setKey(simpleValueWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			anyValueWrapper.setKey(simpleValueWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
	}
	
	@Test
	public void testSetFetchModeJoin() {
		assertNotEquals(FetchMode.JOIN, wrappedArrayValue.getFetchMode());
		arrayValueWrapper.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, wrappedArrayValue.getFetchMode());
		assertNotEquals(FetchMode.JOIN, wrappedBagValue.getFetchMode());
		bagValueWrapper.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, wrappedBagValue.getFetchMode());
		assertNotEquals(FetchMode.JOIN, wrappedListValue.getFetchMode());
		listValueWrapper.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, wrappedListValue.getFetchMode());
		assertNotEquals(FetchMode.JOIN, wrappedManyToOneValue.getFetchMode());
		manyToOneValueWrapper.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, wrappedManyToOneValue.getFetchMode());
		assertNotEquals(FetchMode.JOIN, wrappedOneToOneValue.getFetchMode());
		oneToOneValueWrapper.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, wrappedOneToOneValue.getFetchMode());
		assertNotEquals(FetchMode.JOIN, wrappedMapValue.getFetchMode());
		mapValueWrapper.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, wrappedMapValue.getFetchMode());
		assertNotEquals(FetchMode.JOIN, wrappedPrimitiveArrayValue.getFetchMode());
		primitiveArrayValueWrapper.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, wrappedPrimitiveArrayValue.getFetchMode());
		assertNotEquals(FetchMode.JOIN, wrappedSetValue.getFetchMode());
		setValueWrapper.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, wrappedSetValue.getFetchMode());
		assertNotEquals(FetchMode.JOIN, wrappedIdentifierBagValue.getFetchMode());
		identifierBagValueWrapper.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, wrappedIdentifierBagValue.getFetchMode());
		try {
			oneToManyValueWrapper.setFetchModeJoin();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setFetchModeJoin()'"));
		}
		try {
			simpleValueWrapper.setFetchModeJoin();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setFetchModeJoin()'"));
		}
		try {
			componentValueWrapper.setFetchModeJoin();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setFetchModeJoin()'"));
		}
		try {
			dependantValueWrapper.setFetchModeJoin();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setFetchModeJoin()'"));
		}
		try {
			anyValueWrapper.setFetchModeJoin();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setFetchModeJoin()'"));
		}
	}
	
	@Test
	public void testIsInverse() {
		assertFalse(arrayValueWrapper.isInverse());
		((Collection)wrappedArrayValue).setInverse(true);
		assertTrue(arrayValueWrapper.isInverse());
		assertFalse(bagValueWrapper.isInverse());
		((Collection)wrappedBagValue).setInverse(true);
		assertTrue(bagValueWrapper.isInverse());
		assertFalse(listValueWrapper.isInverse());
		((Collection)wrappedListValue).setInverse(true);
		assertTrue(listValueWrapper.isInverse());
		assertFalse(mapValueWrapper.isInverse());
		((Collection)wrappedMapValue).setInverse(true);
		assertTrue(mapValueWrapper.isInverse());
		assertFalse(setValueWrapper.isInverse());
		((Collection)wrappedSetValue).setInverse(true);
		assertTrue(setValueWrapper.isInverse());
		assertFalse(identifierBagValueWrapper.isInverse());
		((Collection)wrappedIdentifierBagValue).setInverse(true);
		assertTrue(identifierBagValueWrapper.isInverse());
		assertFalse(primitiveArrayValueWrapper.isInverse());
		((Collection)wrappedPrimitiveArrayValue).setInverse(true);
		assertTrue(primitiveArrayValueWrapper.isInverse());
		try {
			manyToOneValueWrapper.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			oneToManyValueWrapper.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			oneToOneValueWrapper.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			simpleValueWrapper.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			componentValueWrapper.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			anyValueWrapper.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			dependantValueWrapper.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
	}
	
	@Test
	public void testGetAssociatedClass() {
		try {
			arrayValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			bagValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			listValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			setValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			primitiveArrayValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			mapValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			identifierBagValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			manyToOneValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			oneToOneValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			simpleValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			componentValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			anyValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			dependantValueWrapper.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		assertNull(oneToManyValueWrapper.getAssociatedClass());
		((OneToMany)wrappedOneToManyValue).setAssociatedClass(wrappedPersistentClass);
		assertSame(wrappedPersistentClass, oneToManyValueWrapper.getAssociatedClass().getWrappedObject());
	}
	
	@Test
	public void testSetLazy() {
		arrayValueWrapper.setLazy(true);
		assertTrue(((Fetchable)wrappedArrayValue).isLazy());
		arrayValueWrapper.setLazy(false);
		assertFalse(((Fetchable)wrappedArrayValue).isLazy());
		bagValueWrapper.setLazy(true);
		assertTrue(((Fetchable)wrappedBagValue).isLazy());
		bagValueWrapper.setLazy(false);
		assertFalse(((Fetchable)wrappedBagValue).isLazy());
		listValueWrapper.setLazy(true);
		assertTrue(((Fetchable)wrappedListValue).isLazy());
		listValueWrapper.setLazy(false);
		assertFalse(((Fetchable)wrappedListValue).isLazy());
		primitiveArrayValueWrapper.setLazy(true);
		assertTrue(((Fetchable)wrappedPrimitiveArrayValue).isLazy());
		primitiveArrayValueWrapper.setLazy(false);
		assertFalse(((Fetchable)wrappedPrimitiveArrayValue).isLazy());
		setValueWrapper.setLazy(true);
		assertTrue(((Fetchable)wrappedSetValue).isLazy());
		setValueWrapper.setLazy(false);
		assertFalse(((Fetchable)wrappedSetValue).isLazy());
		mapValueWrapper.setLazy(true);
		assertTrue(((Fetchable)wrappedMapValue).isLazy());
		mapValueWrapper.setLazy(false);
		assertFalse(((Fetchable)wrappedMapValue).isLazy());
		identifierBagValueWrapper.setLazy(true);
		assertTrue(((Fetchable)wrappedIdentifierBagValue).isLazy());
		identifierBagValueWrapper.setLazy(false);
		assertFalse(((Fetchable)wrappedIdentifierBagValue).isLazy());
		anyValueWrapper.setLazy(true);
		assertTrue(((Any)wrappedAnyValue).isLazy());
		anyValueWrapper.setLazy(false);
		assertFalse(((Any)wrappedAnyValue).isLazy());
		manyToOneValueWrapper.setLazy(true);
		assertTrue(((Fetchable)wrappedManyToOneValue).isLazy());
		manyToOneValueWrapper.setLazy(false);
		assertFalse(((Fetchable)wrappedManyToOneValue).isLazy());
		oneToOneValueWrapper.setLazy(true);
		assertTrue(((Fetchable)wrappedOneToOneValue).isLazy());
		oneToOneValueWrapper.setLazy(false);
		assertFalse(((Fetchable)wrappedOneToOneValue).isLazy());
		try {
			simpleValueWrapper.setLazy(true);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setLazy(boolean)'"));
		}
		try {
			componentValueWrapper.setLazy(true);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setLazy(boolean)'"));
		}
		try {
			dependantValueWrapper.setLazy(true);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setLazy(boolean)'"));
		}
		try {
			oneToManyValueWrapper.setLazy(true);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setLazy(boolean)'"));
		}
	}
	
	@Test
	public void testSetRole() {
		assertNull(((Collection)wrappedArrayValue).getRole());
		arrayValueWrapper.setRole("foo");
		assertEquals("foo", ((Collection)wrappedArrayValue).getRole());
		assertNull(((Collection)wrappedBagValue).getRole());
		bagValueWrapper.setRole("foo");
		assertEquals("foo", ((Collection)wrappedBagValue).getRole());
		assertNull(((Collection)wrappedListValue).getRole());
		listValueWrapper.setRole("foo");
		assertEquals("foo", ((Collection)wrappedListValue).getRole());
		assertNull(((Collection)wrappedMapValue).getRole());
		mapValueWrapper.setRole("foo");
		assertEquals("foo", ((Collection)wrappedMapValue).getRole());
		assertNull(((Collection)wrappedPrimitiveArrayValue).getRole());
		primitiveArrayValueWrapper.setRole("foo");
		assertEquals("foo", ((Collection)wrappedPrimitiveArrayValue).getRole());
		assertNull(((Collection)wrappedSetValue).getRole());
		setValueWrapper.setRole("foo");
		assertEquals("foo", ((Collection)wrappedSetValue).getRole());
		assertNull(((Collection)wrappedIdentifierBagValue).getRole());
		identifierBagValueWrapper.setRole("foo");
		assertEquals("foo", ((Collection)wrappedIdentifierBagValue).getRole());
		try {
			manyToOneValueWrapper.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			oneToManyValueWrapper.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			oneToOneValueWrapper.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			simpleValueWrapper.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			componentValueWrapper.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			dependantValueWrapper.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			anyValueWrapper.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
	}
	
	@Test
	public void testSetReferencedEntityName() {
		assertNull(((ManyToOne)wrappedManyToOneValue).getReferencedEntityName());
		manyToOneValueWrapper.setReferencedEntityName("foobar");
		assertEquals("foobar", ((ManyToOne)wrappedManyToOneValue).getReferencedEntityName());
		assertNull(((OneToMany)wrappedOneToManyValue).getReferencedEntityName());
		oneToManyValueWrapper.setReferencedEntityName("foobar");
		assertEquals("foobar", ((OneToMany)wrappedOneToManyValue).getReferencedEntityName());
		assertNull(((OneToOne)wrappedOneToOneValue).getReferencedEntityName());
		oneToOneValueWrapper.setReferencedEntityName("foobar");
		assertEquals("foobar", ((OneToOne)wrappedOneToOneValue).getReferencedEntityName());
		try {
			arrayValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			bagValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			listValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			mapValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			primitiveArrayValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			setValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			simpleValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			componentValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			dependantValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			anyValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			identifierBagValueWrapper.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
	}
	
	@Test
	public void testSetAssociatedClass() {
		try {
			arrayValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			bagValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			listValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			setValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			primitiveArrayValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			mapValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			identifierBagValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			manyToOneValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			oneToOneValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			simpleValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			componentValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			anyValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			dependantValueWrapper.setAssociatedClass(persistentClassWrapper);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		assertNull(((OneToMany)wrappedOneToManyValue).getAssociatedClass());
		oneToManyValueWrapper.setAssociatedClass(persistentClassWrapper);
		assertSame(wrappedPersistentClass, ((OneToMany)wrappedOneToManyValue).getAssociatedClass());
	}
	
}
