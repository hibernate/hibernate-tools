package org.hibernate.tool.orm.jbt.wrp;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;
import org.hibernate.tool.orm.jbt.wrp.DatabaseReaderWrapperFactory.DatabaseReaderWrapper;
import org.hibernate.tool.orm.jbt.wrp.HqlCompletionProposalWrapperFactory.HqlCompletionProposalWrapper;
import org.hibernate.tool.orm.jbt.wrp.ValueWrapperFactory.ValueWrapper;
import org.junit.jupiter.api.Test;

public class WrapperFactoryTest {
	
	@Test
	public void testCreateArtifactCollectorWrapper() {
		Object artifactCollectorWrapper = WrapperFactory.createArtifactCollectorWrapper();
		assertNotNull(artifactCollectorWrapper);
		assertTrue(artifactCollectorWrapper instanceof DefaultArtifactCollector);
	}
	
	@Test
	public void testCreateCfg2HbmWrapper() {
		Object cfg2HbmWrapper = WrapperFactory.createCfg2HbmWrapper();
		assertNotNull(cfg2HbmWrapper);
		assertTrue(cfg2HbmWrapper instanceof Cfg2HbmTool);
	}
	
	@Test
	public void testCreateNamingStrategyWrapper() {
		Object namingStrategyWrapper = WrapperFactory.createNamingStrategyWrapper(DefaultNamingStrategy.class.getName());
		assertNotNull(namingStrategyWrapper);
		assertTrue(namingStrategyWrapper instanceof NamingStrategyWrapperFactory.StrategyClassNameProvider);
		assertEquals(
				((NamingStrategyWrapperFactory.StrategyClassNameProvider)namingStrategyWrapper).getStrategyClassName(),
				DefaultNamingStrategy.class.getName());
		assertTrue(namingStrategyWrapper instanceof NamingStrategy);
		namingStrategyWrapper = null;
		assertNull(namingStrategyWrapper);
		try {
			namingStrategyWrapper = WrapperFactory.createNamingStrategyWrapper("foo");
			fail();
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Exception while looking up class 'foo'");
		}
		assertNull(namingStrategyWrapper);
	}
	
	@Test
	public void testCreateOverrideRepositoryWrapper() {
		Object overrideRepositoryWrapper = WrapperFactory.createOverrideRepositoryWrapper();
		assertNotNull(overrideRepositoryWrapper);
		assertTrue(overrideRepositoryWrapper instanceof OverrideRepository);
	}
	
	@Test
	public void testCreateRevengStrategyWrapper() throws Exception {
		Field delegateField = DelegatingStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		Object reverseEngineeringStrategyWrapper = WrapperFactory
				.createRevengStrategyWrapper();
		assertNotNull(reverseEngineeringStrategyWrapper);
		assertTrue(reverseEngineeringStrategyWrapper instanceof DefaultStrategy);
		reverseEngineeringStrategyWrapper = null;
		assertNull(reverseEngineeringStrategyWrapper);
		RevengStrategy delegate = new TestRevengStrategy();
		reverseEngineeringStrategyWrapper = WrapperFactory
				.createRevengStrategyWrapper(
						TestDelegatingStrategy.class.getName(), 
						delegate);
		assertNotNull(reverseEngineeringStrategyWrapper);
		assertTrue(reverseEngineeringStrategyWrapper instanceof TestDelegatingStrategy);
		assertSame(delegateField.get(reverseEngineeringStrategyWrapper), delegate);
	}
	
	@Test
	public void testCreateRevengSettingsWrapper() {
		Object reverseEngineeringSettingsWrapper = null;
		RevengStrategy strategy = new DefaultStrategy();
		reverseEngineeringSettingsWrapper = WrapperFactory.createRevengSettingsWrapper(strategy);
		assertNotNull(reverseEngineeringSettingsWrapper);
		assertTrue(reverseEngineeringSettingsWrapper instanceof RevengSettings);
		assertSame(strategy, ((RevengSettings)reverseEngineeringSettingsWrapper).getRootStrategy());
	}
	
	@Test
	public void testCreateNativeConfigurationWrapper() {
		Object configurationWrapper = WrapperFactory.createNativeConfigurationWrapper();
		assertNotNull(configurationWrapper);
		assertTrue(configurationWrapper instanceof NativeConfiguration);
	}
		
	@Test
	public void testCreateRevengConfigurationWrapper() {
		Object configurationWrapper = WrapperFactory.createRevengConfigurationWrapper();
		assertNotNull(configurationWrapper);
		assertTrue(configurationWrapper instanceof RevengConfiguration);
	}
		
	@Test
	public void testCreateJpaConfigurationWrapper() {
		Object configurationWrapper = WrapperFactory.createJpaConfigurationWrapper(null, null);
		assertNotNull(configurationWrapper);
		assertTrue(configurationWrapper instanceof JpaConfiguration);
	}
	
	@Test
	public void testCreateColumnWrapper() {
		Object columnWrapper = WrapperFactory.createColumnWrapper(null);
		assertNotNull(columnWrapper);
		assertTrue(columnWrapper instanceof ColumnWrapper);
	}
	
	@Test
	public void testCreateRootClassWrapper() {
		Object rootClassWrapper = WrapperFactory.createRootClassWrapper();
		assertNotNull(rootClassWrapper);
		assertTrue(rootClassWrapper instanceof PersistentClassWrapper);
		assertTrue(((PersistentClassWrapper)rootClassWrapper).getWrappedObject() instanceof RootClass);
	}
	
	@Test
	public void testCreateSingleTableSubclassWrapper() {
		Object rootClassWrapper = WrapperFactory.createRootClassWrapper();
		Object singleTableSubclassWrapper = WrapperFactory.createSingleTableSubClassWrapper(
				rootClassWrapper);
		assertNotNull(singleTableSubclassWrapper);
		assertTrue(singleTableSubclassWrapper instanceof PersistentClassWrapper);
		PersistentClass persistentClass = ((PersistentClassWrapper)singleTableSubclassWrapper).getWrappedObject();
		assertTrue(persistentClass instanceof SingleTableSubclass);
		assertSame(
				((SingleTableSubclass)persistentClass).getRootClass(), 
				((PersistentClassWrapper)rootClassWrapper).getWrappedObject());
	}
	
	@Test
	public void testCreateJoinedSubclassWrapper() {
		Object rootClassWrapper = WrapperFactory.createRootClassWrapper();
		Object joinedTableSubclassWrapper = WrapperFactory.createJoinedTableSubClassWrapper(
				rootClassWrapper);
		assertNotNull(joinedTableSubclassWrapper);
		assertTrue(joinedTableSubclassWrapper instanceof PersistentClassWrapper);
		PersistentClass persistentClass = ((PersistentClassWrapper)joinedTableSubclassWrapper).getWrappedObject();
		assertTrue(persistentClass instanceof JoinedSubclass);
		assertSame(
				((JoinedSubclass)persistentClass).getRootClass(), 
				((PersistentClassWrapper)rootClassWrapper).getWrappedObject());
	}
	
	@Test
	public void testCreateSpecialRootClassWrapper() {
		Object propertyWrapper = WrapperFactory.createPropertyWrapper();
		Object specialRootClassWrapper = WrapperFactory.createSpecialRootClassWrapper(propertyWrapper);
		assertNotNull(specialRootClassWrapper);
		assertTrue(specialRootClassWrapper instanceof PersistentClassWrapper);
		PersistentClass persistentClass = ((PersistentClassWrapper)specialRootClassWrapper).getWrappedObject();
		assertTrue(persistentClass instanceof SpecialRootClass);
		assertSame(
				((SpecialRootClass)persistentClass).getProperty(), 
				propertyWrapper);		
	}
	
	@Test
	public void testCreatePropertyWrapper() {
		Object propertyWrapper = WrapperFactory.createPropertyWrapper();
		assertNotNull(propertyWrapper);
		assertTrue(propertyWrapper instanceof Property);
	}
	
	@Test
	public void testCreateHqlCompletionProposalWrapper() {
		HQLCompletionProposal hqlCompletionProposalTarget = 
				new HQLCompletionProposal(HQLCompletionProposal.PROPERTY, Integer.MAX_VALUE);
		Object hqlCompletionProposalWrapper = 
				WrapperFactory.createHqlCompletionProposalWrapper(hqlCompletionProposalTarget);
		assertNotNull(hqlCompletionProposalWrapper);
		assertTrue(hqlCompletionProposalWrapper instanceof HqlCompletionProposalWrapper);
	}
		
	@Test
	public void testCreateArrayWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object arrayWrapper = WrapperFactory.createArrayWrapper(persistentClassWrapper);
		Value wrappedArray = ((ValueWrapper)arrayWrapper).getWrappedObject();
		assertTrue(wrappedArray instanceof Array);
		assertSame(((Array)wrappedArray).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateBagWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object bagWrapper = WrapperFactory.createBagWrapper(persistentClassWrapper);
		Value wrappedBag = ((ValueWrapper)bagWrapper).getWrappedObject();
		assertTrue(wrappedBag instanceof Bag);
		assertSame(((Bag)wrappedBag).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateListWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object listWrapper = WrapperFactory.createListWrapper(persistentClassWrapper);
		Value wrappedList = ((ValueWrapper)listWrapper).getWrappedObject();
		assertTrue(wrappedList instanceof List);
		assertSame(((List)wrappedList).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateDatabaseReaderWrapper() {
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		RevengStrategy strategy = new DefaultStrategy();
		Object databaseReaderWrapper = WrapperFactory.createDatabaseReaderWrapper(
				properties, strategy);
		assertNotNull(databaseReaderWrapper);
		assertTrue(databaseReaderWrapper instanceof DatabaseReaderWrapper);
	}
	
	@Test
	public void testCreateTableWrapper() {
		Object tableWrapper = WrapperFactory.createTableWrapper("foo");
		assertNotNull(tableWrapper);
		assertTrue(tableWrapper instanceof Table);
		Table table = (Table)tableWrapper;
		assertEquals("foo", table.getName());
		PrimaryKey pk = table.getPrimaryKey();
		assertSame(table, pk.getTable());
	}

	@Test
	public void testCreateManyToOneWrapper() {
		Table table = new Table("", "foo");
		Object manyToOneWrapper = WrapperFactory.createManyToOneWrapper(table);
		Value wrappedManyToOne = ((ValueWrapper)manyToOneWrapper).getWrappedObject();
		assertTrue(wrappedManyToOne instanceof ManyToOne);
		assertSame(table, wrappedManyToOne.getTable());
	}

	@Test
	public void testCreateMapWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object mapWrapper = WrapperFactory.createMapWrapper(persistentClassWrapper);
		Value wrappedMap = ((ValueWrapper)mapWrapper).getWrappedObject();
		assertTrue(wrappedMap instanceof Map);
		assertSame(((Map)wrappedMap).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateOneToManyWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Table tableWrapper = (Table)WrapperFactory.createTableWrapper("foo");
		((RootClass)persistentClassTarget).setTable(tableWrapper);
		Object oneToManyWrapper = WrapperFactory.createOneToManyWrapper(persistentClassWrapper);
		Value wrappedOneToMany = ((ValueWrapper)oneToManyWrapper).getWrappedObject();
		assertTrue(wrappedOneToMany instanceof OneToMany);
		assertSame(((OneToMany)wrappedOneToMany).getTable(), tableWrapper);
	}
	
	@Test
	public void testCreateOneToOneWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Table tableTarget = new Table("", "foo");
		((RootClass)persistentClassTarget).setTable(tableTarget);
		persistentClassTarget.setEntityName("bar");
		Object oneToOneWrapper = WrapperFactory.createOneToOneWrapper(persistentClassWrapper);
		Value wrappedOneToOne = ((ValueWrapper)oneToOneWrapper).getWrappedObject();
		assertTrue(wrappedOneToOne instanceof OneToOne);
		assertEquals(((OneToOne)wrappedOneToOne).getEntityName(), "bar");
		assertSame(((OneToOne)wrappedOneToOne).getTable(), tableTarget);
	}
	
	@Test
	public void testCreatePrimitiveArrayWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object primitiveArrayWrapper = WrapperFactory.createPrimitiveArrayWrapper(persistentClassWrapper);
		Value wrappedPrimitiveArray = ((ValueWrapper)primitiveArrayWrapper).getWrappedObject();
		assertTrue(wrappedPrimitiveArray instanceof PrimitiveArray);
		assertSame(((PrimitiveArray)wrappedPrimitiveArray).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateSetWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object setWrapper = WrapperFactory.createSetWrapper(persistentClassWrapper);
		Value wrappedSet = ((ValueWrapper)setWrapper).getWrappedObject();
		assertTrue(wrappedSet instanceof Set);
		assertSame(((Set)wrappedSet).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateSimpleValueWrapper() {
		Object simpleValueWrapper = WrapperFactory.createSimpleValueWrapper();
		Value wrappedSimpleValue = ((ValueWrapper)simpleValueWrapper).getWrappedObject();
		assertTrue(wrappedSimpleValue instanceof SimpleValue);
	}
	
	@Test
	public void testCreateComponentWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object componentWrapper = WrapperFactory.createComponentWrapper(persistentClassWrapper);
		Value wrappedComponent = ((ValueWrapper)componentWrapper).getWrappedObject();
		assertTrue(wrappedComponent instanceof Component);
		assertSame(((Component)wrappedComponent).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateDependantValueWrapper() {
		Table tableTarget = new Table("", "foo");
		Object valueWrapper = WrapperFactory.createSimpleValueWrapper();
		Object dependantValueWrapper = WrapperFactory.createDependantValueWrapper(tableTarget, valueWrapper);
		Value wrappedDependantValue = ((ValueWrapper)dependantValueWrapper).getWrappedObject();
		assertTrue(wrappedDependantValue instanceof DependantValue);
		assertSame(tableTarget, ((DependantValue)wrappedDependantValue).getTable());
		assertSame(
				((DependantValue)wrappedDependantValue).getWrappedValue(), 
				((Wrapper)valueWrapper).getWrappedObject());
	}
	
	@Test
	public void testCreateAnyValueWrapper() {
		Table tableTarget = new Table("", "foo");
		Object anyValueWrapper = WrapperFactory.createAnyValueWrapper(tableTarget);
		Value wrappedAnyValue = ((ValueWrapper)anyValueWrapper).getWrappedObject();
		assertTrue(wrappedAnyValue instanceof Any);
		assertSame(tableTarget, ((Any)wrappedAnyValue).getTable());
	}
	
	@Test
	public void testCreateIdentifierBagValueWrapper() {
		Object persistentClassWrapper = WrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object identifierBagValueWrapper = WrapperFactory.createIdentifierBagValueWrapper(persistentClassWrapper);
		Value wrappedIdentifierBagValue = ((ValueWrapper)identifierBagValueWrapper).getWrappedObject();
		assertTrue(wrappedIdentifierBagValue instanceof IdentifierBag);
		assertSame(((IdentifierBag)wrappedIdentifierBagValue).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateTableFilterWrapper() {
		Object tableFilterWrapper = WrapperFactory.createTableFilterWrapper();
		assertNotNull(tableFilterWrapper);
		assertTrue(tableFilterWrapper instanceof TableFilter);
	}
	
	@Test
	public void testCreateTypeFactoryWrapper() {
		Object typeFactoryWrapper = WrapperFactory.createTypeFactoryWrapper();
		assertSame(TypeFactoryWrapper.INSTANCE, typeFactoryWrapper);
	}
	
	@SuppressWarnings("serial")
	public static class TestNamingStrategy extends DefaultNamingStrategy {}
	
	public static class TestRevengStrategy extends DefaultStrategy {}
	public static class TestDelegatingStrategy extends DelegatingStrategy {
		public TestDelegatingStrategy(RevengStrategy delegate) {
			super(delegate);
		}
	}

}
