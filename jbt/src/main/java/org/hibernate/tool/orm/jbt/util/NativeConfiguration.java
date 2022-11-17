package org.hibernate.tool.orm.jbt.util;

import org.hibernate.cfg.Configuration;
import org.xml.sax.EntityResolver;

public class NativeConfiguration extends Configuration {
	
	@SuppressWarnings("unused")
	private EntityResolver entityResolver = null;
	
	public void setEntityResolver(EntityResolver entityResolver) {
		// This method is not supported anymore in class Configuration from Hibernate 5+
		// Only caching the EntityResolver for bookkeeping purposes
		this.entityResolver = entityResolver;
	}

}
