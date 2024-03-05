package org.hibernate.tool.orm.jbt.api;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ConfigurationWrapper extends Wrapper {

	default String getProperty(String property) { return ((Configuration)getWrappedObject()).getProperty(property); }
	default ConfigurationWrapper addFile(File file) { ((Configuration)getWrappedObject()).addFile(file); return this; }
	default void setProperty(String name, String value) { ((Configuration)getWrappedObject()).setProperty(name, value); }
	
}
