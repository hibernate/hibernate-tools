package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;

public class WrapperFactory {

	public Object createArtifactCollectorWrapper() {
		return new DefaultArtifactCollector();
	}
	
	public Object createCfg2HbmWrapper() {
		return new Cfg2HbmTool();
	}

}
