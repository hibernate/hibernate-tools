package org.hibernate.tool.orm.jbt.api.factory;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.RevengStrategyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TableWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.internal.factory.ArtifactCollectorWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.Cfg2HbmToolWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ColumnWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ConfigurationWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.DatabaseReaderWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.EnvironmentWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ExporterWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.HbmExporterWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.HqlCodeAssistWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.HqlCompletionProposalWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.NamingStrategyWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.OverrideRepositoryWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.PersistentClassWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.PropertyWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.RevengSettingsWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.RevengStrategyWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.SchemaExportWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.TableFilterWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.TableWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.TypeFactoryWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ValueWrapperFactory;

public class WrapperFactory {
	
	private WrapperFactory() {}

	public static Object createArtifactCollectorWrapper() {
		return ArtifactCollectorWrapperFactory.createArtifactCollectorWrapper();
	}
	
	public static Object createCfg2HbmWrapper() {
		return Cfg2HbmToolWrapperFactory.createCfg2HbmToolWrapper();
	}

	public static Object createNamingStrategyWrapper(String namingStrategyClassName) {
		return NamingStrategyWrapperFactory.createNamingStrategyWrapper(namingStrategyClassName);
	}
	
	public static Object createOverrideRepositoryWrapper() {
		return OverrideRepositoryWrapperFactory.createOverrideRepositoryWrapper();
	}
	
	public static Object createRevengStrategyWrapper(Object...objects) {
		return RevengStrategyWrapperFactory.createRevengStrategyWrapper(objects);
	}

	public static Object createRevengSettingsWrapper(Object revengStrategyWrapper) {
		return RevengSettingsWrapperFactory.createRevengSettingsWrapper((RevengStrategyWrapper)revengStrategyWrapper);
	}
	
	public static Object createNativeConfigurationWrapper() {
		return ConfigurationWrapperFactory.createNativeConfigurationWrapper();
	}
	
	public static Object createRevengConfigurationWrapper() {
		return ConfigurationWrapperFactory.createRevengConfigurationWrapper();
	}

	public static Object createJpaConfigurationWrapper(String persistenceUnit, Map<?,?> properties) {
		return ConfigurationWrapperFactory.createJpaConfigurationWrapper(persistenceUnit, properties);
	}

	public static Object createColumnWrapper(String name) {
		return ColumnWrapperFactory.createColumnWrapper(name);
	}

	public static Object createRootClassWrapper() {
		return PersistentClassWrapperFactory.createRootClassWrapper();
	}

	public static Object createSingleTableSubClassWrapper(Object persistentClassWrapper) {
		return PersistentClassWrapperFactory.createSingleTableSubClassWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public static Object createJoinedTableSubClassWrapper(Object persistentClassWrapper) {
		return PersistentClassWrapperFactory.createJoinedTableSubClassWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public static Object createSpecialRootClassWrapper(Object propertyWrapper) {
		return PersistentClassWrapperFactory.createSpecialRootClassWrapper(((PropertyWrapper)propertyWrapper));
	}

	public static Object createPropertyWrapper() {
		return PropertyWrapperFactory.createPropertyWrapper();
	}

	public static Object createHqlCompletionProposalWrapper(Object hqlCompletionProposal) {
		return HqlCompletionProposalWrapperFactory.createHqlCompletionProposalWrapper(hqlCompletionProposal);
	}
	
	public static Object createArrayWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createArrayWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public static Object createBagWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createBagWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public static Object createListWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createListWrapper((PersistentClassWrapper)persistentClassWrapper);
	}
	
	public static Object createDatabaseReaderWrapper(Properties properties, Object revengStrategyWrapper) {
		return DatabaseReaderWrapperFactory.createDatabaseReaderWrapper(
				properties, 
				(RevengStrategyWrapper)revengStrategyWrapper);
	}

	public static Object createTableWrapper(String name) {
		return TableWrapperFactory.createTableWrapper(name);
	}

	public static Object createManyToOneWrapper(Object tableWrapper) {
		return ValueWrapperFactory.createManyToOneWrapper((TableWrapper)tableWrapper);
	}

	public static Object createMapWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createMapWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public static Object createOneToManyWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createOneToManyWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public static Object createOneToOneWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createOneToOneWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public static Object createPrimitiveArrayWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createPrimitiveArrayWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public static Object createSetWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createSetWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public static Object createSimpleValueWrapper() {
		return ValueWrapperFactory.createSimpleValueWrapper();
	}

	public static Object createComponentWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createComponentWrapper((PersistentClassWrapper)persistentClassWrapper);
	}
	
	public static Object createDependantValueWrapper(Object tableWrapper, Object valueWrapper) {
		return ValueWrapperFactory.createDependantValueWrapper((TableWrapper)tableWrapper, (ValueWrapper)valueWrapper);
	}

	public static Object createAnyValueWrapper(Object tableWrapper) {
		return ValueWrapperFactory.createAnyValueWrapper((TableWrapper)tableWrapper);
	}

	public static Object createIdentifierBagValueWrapper(Object persistentClassWrapper) {
		return ValueWrapperFactory.createIdentifierBagValueWrapper((PersistentClassWrapper)persistentClassWrapper);
	}

	public static Object createTableFilterWrapper() {
		return TableFilterWrapperFactory.createTableFilterWrapper();
	}

	public static Object createTypeFactoryWrapper() {
		return TypeFactoryWrapperFactory.createTypeFactoryWrapper();
	}

	public static Object createEnvironmentWrapper() {
		return EnvironmentWrapperFactory.createEnvironmentWrapper();
	}

	public static Object createSchemaExport(Object configurationWrapper) {
		return SchemaExportWrapperFactory.createSchemaExportWrapper(
				(ConfigurationWrapper)configurationWrapper);
	}
	
	public static Object createHbmExporterWrapper(Object configurationWrapper, File file) {
		return HbmExporterWrapperFactory.createHbmExporterWrapper(
				(ConfigurationWrapper)configurationWrapper, file);
	}

	public static Object createExporterWrapper(String exporterClassName) {
		return ExporterWrapperFactory.createExporterWrapper(exporterClassName);
	}
	
	public static Object createHqlCodeAssistWrapper(Object configurationWrapper) {
		return HqlCodeAssistWrapperFactory.createHqlCodeAssistWrapper(
				(ConfigurationWrapper)configurationWrapper);
	}

}
