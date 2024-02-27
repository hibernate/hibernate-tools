package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.orm.jbt.api.Cfg2HbmToolWrapper;

public class Cfg2HbmToolWrapperFactory {

	public static Cfg2HbmToolWrapper createCfg2HbmToolWrapper() {
		Cfg2HbmTool wrappedCfg2HbmTool = new Cfg2HbmTool();
		return new Cfg2HbmToolWrapper() {
			@Override public Cfg2HbmTool getWrappedObject() { return wrappedCfg2HbmTool; }
		};
	}

}
