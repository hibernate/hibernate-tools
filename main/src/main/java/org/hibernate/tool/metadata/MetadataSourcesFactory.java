package org.hibernate.tool.metadata;

public class MetadataSourcesFactory {
	
	public MetadataSources createJdbcSources() {
		return new JdbcMetadataSources();
	}
	
}
