package org.hibernate.tool.metadata;

import java.util.Properties;

import org.hibernate.cfg.JPAConfiguration;

public class JpaMetadataSources extends JPAConfiguration {

	public JpaMetadataSources(String persistenceUnit, Properties properties) {
		super(persistenceUnit, properties);
	}
	
}
