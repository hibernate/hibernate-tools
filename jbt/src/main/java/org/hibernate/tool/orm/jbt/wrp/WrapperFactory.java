package org.hibernate.tool.orm.jbt.wrp;

import java.util.Map;
import java.util.Properties;

import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
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
		return ValueWrapperFactory.createArrayWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public Object createBagWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createBagWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public Object createListWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createListWrapper((PersistentClassWrapper)persistentClassWrapper);
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
		return ValueWrapperFactory.createManyToOneWrapper((Table)table);
	}

	public Object createMapWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createMapWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public Object createOneToManyWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createOneToManyWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public Object createOneToOneWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createOneToOneWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public Object createPrimitiveArrayWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createPrimitiveArrayWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public Object createSetWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createSetWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public Object createSimpleValueWrapper() {
		return ValueWrapperFactory.createSimpleValueWrapper();
	}

	public Object createComponentWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createComponentWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

}
