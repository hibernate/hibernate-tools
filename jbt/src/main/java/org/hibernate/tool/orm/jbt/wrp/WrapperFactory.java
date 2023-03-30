package org.hibernate.tool.orm.jbt.wrp;

import java.util.Map;
import java.util.Properties;

import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;

public class WrapperFactory {

	public Object createArtifactCollectorWrapper() {
		return new DefaultArtifactCollector();
	}
	
	public Object createCfg2HbmWrapper() {
		return new Cfg2HbmTool();
	}

	public Object createNamingStrategyWrapper(String namingStrategyClassName) {
		return NamingStrategyWrapperFactory.create(namingStrategyClassName);
	}
	
	public Object createOverrideRepositoryWrapper() {
		return new OverrideRepository();
	}
	
	public Object createRevengStrategyWrapper(Object...objects) {
		return RevengStrategyWrapperFactory.create(objects);
	}

	public Object createRevengSettingsWrapper(Object revengStrategy) {
		return new RevengSettings((RevengStrategy)(revengStrategy));
	}
	
	public Object createNativeConfigurationWrapper() {
		return new NativeConfiguration();
	}
	
	public Object createRevengConfigurationWrapper() {
		return new RevengConfiguration();
	}

	public Object createJpaConfigurationWrapper(
			String persistenceUnit, 
			Map<?,?> properties) {
		return new JpaConfiguration(persistenceUnit, properties);
	}

	public Object createColumnWrapper(String name) {
		return new ColumnWrapper(name);
	}

	public Object createRootClassWrapper() {
		return PersistentClassWrapperFactory.createRootClassWrapper();
	}

	public Object createSingleTableSubClassWrapper(Object persistentClassWrapper) {
		return PersistentClassWrapperFactory
				.createSingleTableSubclassWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public Object createJoinedTableSubClassWrapper(Object persistentClassWrapper) {
		return PersistentClassWrapperFactory
				.createJoinedSubclassWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public Object createSpecialRootClassWrapper(Object property) {
		return PersistentClassWrapperFactory
				.createSpecialRootClassWrapper((Property)property);
	}

	public Object createPropertyWrapper() {
		return new Property();
	}

	public Object createHqlCompletionProposalWrapper(Object hqlCompletionProposalTarget) {
		return HqlCompletionProposalWrapperFactory
				.createHqlCompletionProposalWrapper(
						(HQLCompletionProposal)hqlCompletionProposalTarget);
	}
	
	public Object createArrayWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createValueWrapper(
				new Array(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject()));
	}

	public Object createBagWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createValueWrapper(
				new Bag(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject()));
	}

	public Object createListWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createValueWrapper(
				new List(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject()));
	}
	
	public Object createDatabaseReaderWrapper(Properties properties, Object revengStrategy) {
		return DatabaseReaderWrapperFactory.createDatabaseReaderWrapper(
				properties, 
				(RevengStrategy)revengStrategy);
	}

	public Object createTableWrapper(String name) {
		Table result = new Table("Hibernate Tools", name);
		result.setPrimaryKey(new PrimaryKey(result));
		return result;
	}

	public Object createManyToOneWrapper(Object table) {
		return ValueWrapperFactory.createValueWrapper(
				new ManyToOne(
						DummyMetadataBuildingContext.INSTANCE, 
						(Table)table));
	}

	public Object createMapWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createValueWrapper(
				new org.hibernate.mapping.Map(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject()));
	}

	public Object createOneToManyWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createValueWrapper(
				new OneToMany(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject()));
	}

	public Object createOneToOneWrapper(Object persistentClassWrapper) {
		PersistentClass pc = (PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject();
		return ValueWrapperFactory.createValueWrapper(
				new OneToOne(
						DummyMetadataBuildingContext.INSTANCE, 
						pc.getTable(),
						pc));
	}

	public Object createPrimitiveArrayWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createValueWrapper(
				new PrimitiveArray(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject()));
	}

	public Object createSetWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createValueWrapper(
				new Set(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject()));
	}

	public Object createSimpleValueWrapper() {
		return ValueWrapperFactory.createValueWrapper(new BasicValue(DummyMetadataBuildingContext.INSTANCE));
	}

	public Object createComponentWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createValueWrapper(
				new Component(
						DummyMetadataBuildingContext.INSTANCE, 
						(PersistentClass)((Wrapper)persistentClassWrapper).getWrappedObject()));
	}

}
