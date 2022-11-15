package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.orm.jbt.util.ReflectUtil;

public class WrapperFactory {

	public Object createArtifactCollectorWrapper() {
		return new DefaultArtifactCollector();
	}
	
	public Object createCfg2HbmWrapper() {
		return new Cfg2HbmTool();
	}

	public Object createNamingStrategyWrapper(String namingStrategyClassName) {
		return NamingStrategyWrapper.create(namingStrategyClassName);
	}
	
	public Object createOverrideRepositoryWrapper() {
		return new OverrideRepository();
	}

	public Object createReverseEngineeringSettingsWrapper(Object revengStrategy) {
		assert revengStrategy != null;
		assert revengStrategy instanceof RevengStrategy;
		return new RevengSettings((RevengStrategy)(revengStrategy));
	}

	public Object createReverseEngineeringStrategyWrapper(String revengStrategyClassName) {
		if (revengStrategyClassName == null) {
			return new DefaultStrategy();
		} else {
			return new DelegatingStrategy(
					(RevengStrategy)ReflectUtil.createInstance(revengStrategyClassName));
		}
	}

}
