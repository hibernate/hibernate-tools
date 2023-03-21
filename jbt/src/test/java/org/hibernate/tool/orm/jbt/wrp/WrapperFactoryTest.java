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
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
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
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;
import org.hibernate.tool.orm.jbt.wrp.DatabaseReaderWrapperFactory.DatabaseReaderWrapper;
import org.hibernate.tool.orm.jbt.wrp.HqlCompletionProposalWrapperFactory.HqlCompletionProposalWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WrapperFactoryTest {
	
	private WrapperFactory wrapperFactory = null;
	
	@BeforeEach
	public void beforeEach() {
		wrapperFactory = new WrapperFactory();
	}
	
	@Test
	public void testCreateArtifactCollectorWrapper() {
		Object artifactCollectorWrapper = wrapperFactory.createArtifactCollectorWrapper();
		assertNotNull(artifactCollectorWrapper);
		assertTrue(artifactCollectorWrapper instanceof DefaultArtifactCollector);
	}
	
	@Test
	public void testCreateCfg2HbmWrapper() {
		Object cfg2HbmWrapper = wrapperFactory.createCfg2HbmWrapper();
		assertNotNull(cfg2HbmWrapper);
		assertTrue(cfg2HbmWrapper instanceof Cfg2HbmTool);
	}
	
	@Test
	public void testCreateNamingStrategyWrapper() {
		Object namingStrategyWrapper = wrapperFactory.createNamingStrategyWrapper(DefaultNamingStrategy.class.getName());
		assertNotNull(namingStrategyWrapper);
		assertTrue(namingStrategyWrapper instanceof NamingStrategyWrapperFactory.StrategyClassNameProvider);
		assertEquals(
				((NamingStrategyWrapperFactory.StrategyClassNameProvider)namingStrategyWrapper).getStrategyClassName(),
				DefaultNamingStrategy.class.getName());
		assertTrue(namingStrategyWrapper instanceof NamingStrategy);
		namingStrategyWrapper = null;
		assertNull(namingStrategyWrapper);
		try {
			namingStrategyWrapper = wrapperFactory.createNamingStrategyWrapper("foo");
			fail();
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Exception while looking up class 'foo'");
		}
		assertNull(namingStrategyWrapper);
	}
	
	@Test
	public void testCreateOverrideRepositoryWrapper() {
		Object overrideRepositoryWrapper = wrapperFactory.createOverrideRepositoryWrapper();
		assertNotNull(overrideRepositoryWrapper);
		assertTrue(overrideRepositoryWrapper instanceof OverrideRepository);
	}
	
	@Test
	public void testCreateRevengStrategyWrapper() throws Exception {
		Field delegateField = DelegatingStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		Object reverseEngineeringStrategyWrapper = wrapperFactory
				.createRevengStrategyWrapper();
		assertNotNull(reverseEngineeringStrategyWrapper);
		assertTrue(reverseEngineeringStrategyWrapper instanceof DefaultStrategy);
		reverseEngineeringStrategyWrapper = null;
		assertNull(reverseEngineeringStrategyWrapper);
		RevengStrategy delegate = new TestRevengStrategy();
		reverseEngineeringStrategyWrapper = wrapperFactory
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
		reverseEngineeringSettingsWrapper = wrapperFactory.createRevengSettingsWrapper(strategy);
		assertNotNull(reverseEngineeringSettingsWrapper);
		assertTrue(reverseEngineeringSettingsWrapper instanceof RevengSettings);
		assertSame(strategy, ((RevengSettings)reverseEngineeringSettingsWrapper).getRootStrategy());
	}
	
	@Test
	public void testCreateNativeConfigurationWrapper() {
		Object configurationWrapper = wrapperFactory.createNativeConfigurationWrapper();
		assertNotNull(configurationWrapper);
		assertTrue(configurationWrapper instanceof NativeConfiguration);
	}
		
	@Test
	public void testCreateRevengConfigurationWrapper() {
		Object configurationWrapper = wrapperFactory.createRevengConfigurationWrapper();
		assertNotNull(configurationWrapper);
		assertTrue(configurationWrapper instanceof RevengConfiguration);
	}
		
	@Test
	public void testCreateJpaConfigurationWrapper() {
		Object configurationWrapper = wrapperFactory.createJpaConfigurationWrapper(null, null);
		assertNotNull(configurationWrapper);
		assertTrue(configurationWrapper instanceof JpaConfiguration);
	}
	
	@Test
	public void testCreateColumnWrapper() {
		Object columnWrapper = wrapperFactory.createColumnWrapper(null);
		assertNotNull(columnWrapper);
		assertTrue(columnWrapper instanceof ColumnWrapper);
	}
	
	@Test
	public void testCreateRootClassWrapper() {
		Object rootClassWrapper = wrapperFactory.createRootClassWrapper();
		assertNotNull(rootClassWrapper);
		assertTrue(rootClassWrapper instanceof PersistentClassWrapper);
		assertTrue(((PersistentClassWrapper)rootClassWrapper).getWrappedObject() instanceof RootClass);
	}
	
	@Test
	public void testCreateSingleTableSubclassWrapper() {
		Object rootClassWrapper = wrapperFactory.createRootClassWrapper();
		Object singleTableSubclassWrapper = wrapperFactory.createSingleTableSubClassWrapper(
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
		Object rootClassWrapper = wrapperFactory.createRootClassWrapper();
		Object joinedTableSubclassWrapper = wrapperFactory.createJoinedTableSubClassWrapper(
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
		Object propertyWrapper = wrapperFactory.createPropertyWrapper();
		Object specialRootClassWrapper = wrapperFactory.createSpecialRootClassWrapper(propertyWrapper);
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
		Object propertyWrapper = wrapperFactory.createPropertyWrapper();
		assertNotNull(propertyWrapper);
		assertTrue(propertyWrapper instanceof Property);
	}
	
	@Test
	public void testCreateHqlCompletionProposalWrapper() {
		HQLCompletionProposal hqlCompletionProposalTarget = 
				new HQLCompletionProposal(HQLCompletionProposal.PROPERTY, Integer.MAX_VALUE);
		Object hqlCompletionProposalWrapper = 
				wrapperFactory.createHqlCompletionProposalWrapper(hqlCompletionProposalTarget);
		assertNotNull(hqlCompletionProposalWrapper);
		assertTrue(hqlCompletionProposalWrapper instanceof HqlCompletionProposalWrapper);
	}
		
	@Test
	public void testCreateArrayWrapper() {
		Object persistentClassWrapper = wrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object arrayWrapper = wrapperFactory.createArrayWrapper(persistentClassWrapper);
		assertTrue(arrayWrapper instanceof Array);
		assertSame(((Array)arrayWrapper).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateBagWrapper() {
		Object persistentClassWrapper = wrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object bagWrapper = wrapperFactory.createBagWrapper(persistentClassWrapper);
		assertTrue(bagWrapper instanceof Bag);
		assertSame(((Bag)bagWrapper).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateListWrapper() {
		Object persistentClassWrapper = wrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object listWrapper = wrapperFactory.createListWrapper(persistentClassWrapper);
		assertTrue(listWrapper instanceof List);
		assertSame(((List)listWrapper).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateDatabaseReaderWrapper() {
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		RevengStrategy strategy = new DefaultStrategy();
		Object databaseReaderWrapper = wrapperFactory.createDatabaseReaderWrapper(
				properties, strategy);
		assertNotNull(databaseReaderWrapper);
		assertTrue(databaseReaderWrapper instanceof DatabaseReaderWrapper);
	}
	
	@Test
	public void testCreateTableWrapper() {
		Object tableWrapper = wrapperFactory.createTableWrapper("foo");
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
		Value manyToOneWrapper = wrapperFactory.createManyToOneWrapper(table);
		assertTrue(manyToOneWrapper instanceof ManyToOne);
		assertSame(table, manyToOneWrapper.getTable());
	}

	@Test
	public void testCreateMapWrapper() {
		Object persistentClassWrapper = wrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object mapWrapper = wrapperFactory.createMapWrapper(persistentClassWrapper);
		assertTrue(mapWrapper instanceof Map);
		assertSame(((Map)mapWrapper).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateOneToManyWrapper() {
		Object persistentClassWrapper = wrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Table tableWrapper = (Table)wrapperFactory.createTableWrapper("foo");
		((RootClass)persistentClassTarget).setTable(tableWrapper);
		Object oneToManyWrapper = wrapperFactory.createOneToManyWrapper(persistentClassWrapper);
		assertTrue(oneToManyWrapper instanceof OneToMany);
		assertSame(((OneToMany)oneToManyWrapper).getTable(), tableWrapper);
	}
	
	@Test
	public void testCreateOneToOneWrapper() {
		PersistentClassWrapper persistentClassWrapper = PersistentClassWrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = persistentClassWrapper.getWrappedObject();
		Table tableTarget = new Table("", "foo");
		((RootClass)persistentClassTarget).setTable(tableTarget);
		persistentClassTarget.setEntityName("bar");
		Value oneToOneWrapper = wrapperFactory.createOneToOneWrapper(persistentClassWrapper);
		assertTrue(oneToOneWrapper instanceof OneToOne);
		assertEquals(((OneToOne)oneToOneWrapper).getEntityName(), "bar");
		assertSame(((OneToOne)oneToOneWrapper).getTable(), tableTarget);
	}
	
	@Test
	public void testCreatePrimitiveArrayWrapper() {
		Object persistentClassWrapper = wrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object primitiveArrayWrapper = wrapperFactory.createPrimitiveArrayWrapper(persistentClassWrapper);
		assertTrue(primitiveArrayWrapper instanceof PrimitiveArray);
		assertSame(((PrimitiveArray)primitiveArrayWrapper).getOwner(), persistentClassTarget);
	}

	@Test
	public void testCreateSetWrapper() {
		Object persistentClassWrapper = wrapperFactory.createRootClassWrapper();
		PersistentClass persistentClassTarget = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		Object setWrapper = wrapperFactory.createSetWrapper(persistentClassWrapper);
		assertTrue(setWrapper instanceof Set);
		assertSame(((Set)setWrapper).getOwner(), persistentClassTarget);
	}
	
	@Test
	public void testCreateSimpleValueWrapper() {
		Value simpleValueWrapper = wrapperFactory.createSimpleValueWrapper();
		assertNotNull(simpleValueWrapper);
		assertTrue(simpleValueWrapper instanceof SimpleValue);
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
