package org.hibernate.tool.orm.jbt.util;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.xml.sax.EntityResolver;

public class NativeConfiguration extends Configuration {
	
	@SuppressWarnings("unused")
	private EntityResolver entityResolver = null;
	
	@SuppressWarnings("unused")
	private NamingStrategy namingStrategy = null;
	
	public void setEntityResolver(EntityResolver entityResolver) {
		// This method is not supported anymore in class Configuration from Hibernate 5+
		// Only caching the EntityResolver for bookkeeping purposes
		this.entityResolver = entityResolver;
	}
	
	public void setNamingStrategy(NamingStrategy namingStrategy) {
		// The method Configuration.setNamingStrategy() is not supported 
		// anymore from Hibernate 5+.
		// Naming strategies can be configured using the 
		// AvailableSettings.IMPLICIT_NAMING_STRATEGY property.
		// Only caching the NamingStrategy for bookkeeping purposes
		this.namingStrategy = namingStrategy;
	}
	
	

}
