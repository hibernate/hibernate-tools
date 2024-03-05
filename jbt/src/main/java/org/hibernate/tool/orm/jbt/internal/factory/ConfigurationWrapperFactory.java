package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.api.ConfigurationWrapper;

public class ConfigurationWrapperFactory {

	public static ConfigurationWrapper createConfigurationWrapper(final Configuration wrappedConfiguration) {
		return new ConfigurationWrapper() {
			@Override public Configuration getWrappedObject() { return wrappedConfiguration; }
		};
	}
	
}
