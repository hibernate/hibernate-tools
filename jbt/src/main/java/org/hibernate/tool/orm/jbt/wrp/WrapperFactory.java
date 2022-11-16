package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;

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

}
