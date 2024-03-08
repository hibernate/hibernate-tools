package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.orm.jbt.api.EnvironmentWrapper;

public class EnvironmentWrapperFactory {
	
	static EnvironmentWrapper ENVIRONMENT_WRAPPER_INSTANCE = new EnvironmentWrapper() {};

	public static EnvironmentWrapper createEnvironmentWrapper() {
		return ENVIRONMENT_WRAPPER_INSTANCE;
	}
	
}
