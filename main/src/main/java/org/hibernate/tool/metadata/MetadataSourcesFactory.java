package org.hibernate.tool.metadata;

import java.util.Properties;

public class MetadataSourcesFactory {
	
	public static MetadataSources createJdbcSources() {
		return new JdbcMetadataSources();
	}
	
	public static MetadataSources createJpaSources(String persistenceUnit, Properties properties) {
		return new JpaMetadataSources(persistenceUnit, properties);
	}
	
	public static MetadataSources createNativeSources() {
		return new NativeMetadataSources();
	}
	
	public static MetadataSources createPojoSources() {
		return new PojoMetadataSources();
	}
	
}
