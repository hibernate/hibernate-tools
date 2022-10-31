package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;

public class WrapperFactory {

	public Object createArtifactCollectorWrapper() {
		return new DefaultArtifactCollector();
	}
	
	public Object createCfg2HbmWrapper() {
		return new Cfg2HbmTool();
	}

	public Object createNamingStrategyWrapper() {
		return new DefaultNamingStrategy();
	}

	public Object createReverseEngineeringSettingsWrapper() {
		return new RevengSettings(null);
	}

	public Object createReverseEngineeringStrategyWrapper() {
		return new DefaultStrategy();
	}

}
