package org.hibernate.tool.orm.jbt.api;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.xml.sax.EntityResolver;

public interface ConfigurationWrapper extends Wrapper {

	default String getProperty(String property) { return ((Configuration)getWrappedObject()).getProperty(property); }
	default ConfigurationWrapper addFile(File file) { ((Configuration)getWrappedObject()).addFile(file); return this; }
	default void setProperty(String name, String value) { ((Configuration)getWrappedObject()).setProperty(name, value); }
	default ConfigurationWrapper setProperties(Properties properties) { ((Configuration)getWrappedObject()).setProperties(properties); return this; }
	default void setEntityResolver(EntityResolver entityResolver) {
		Object wrappedObject = getWrappedObject();
		if (wrappedObject instanceof NativeConfiguration) ((NativeConfiguration)wrappedObject).setEntityResolver(entityResolver);
		if (wrappedObject instanceof RevengConfiguration) ((RevengConfiguration)wrappedObject).setEntityResolver(entityResolver);
		if (wrappedObject instanceof JpaConfiguration) ((JpaConfiguration)wrappedObject).setEntityResolver(entityResolver);
	}
	
}
