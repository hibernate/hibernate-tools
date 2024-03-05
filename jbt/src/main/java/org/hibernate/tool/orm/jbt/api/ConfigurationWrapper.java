package org.hibernate.tool.orm.jbt.api;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ConfigurationWrapper extends Wrapper {

	default String getProperty(String property) { return ((Configuration)getWrappedObject()).getProperty(property); }
	
}
