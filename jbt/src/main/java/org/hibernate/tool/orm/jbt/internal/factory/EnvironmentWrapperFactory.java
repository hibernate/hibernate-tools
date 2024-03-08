package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.orm.jbt.api.EnvironmentWrapper;

public class EnvironmentWrapperFactory {

	public static EnvironmentWrapper createEnvironmentWrapper() {
		return EnvironmentWrapper.INSTANCE;
	}
	
}
