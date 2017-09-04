package org.hibernate.tool.metadata;

import java.util.Properties;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

public class MetadataSourcesFactory {
	
	public static MetadataSources createJdbcSources(
			ReverseEngineeringStrategy reverseEngineeringStrategy, 
			Properties properties,
			boolean preferBasicCompositeIds) {
		return new JdbcMetadataSources(
				reverseEngineeringStrategy, 
				properties);
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
