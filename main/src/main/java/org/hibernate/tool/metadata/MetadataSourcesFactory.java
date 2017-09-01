package org.hibernate.tool.metadata;

import java.util.Properties;

public class MetadataSourcesFactory {
	
	public MetadataSources createJdbcSources() {
		return new JdbcMetadataSources();
	}
	
	public MetadataSources createJpaSources(String persistenceUnit, Properties properties) {
		return new JpaMetadataSources(persistenceUnit, properties);
	}
	
}
