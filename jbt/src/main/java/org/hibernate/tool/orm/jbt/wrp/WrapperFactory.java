package org.hibernate.tool.orm.jbt.wrp;

import java.util.Map;

import org.hibernate.mapping.Property;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
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

	public Object createPropertyWrapper() {
		return new Property();
	}

}
