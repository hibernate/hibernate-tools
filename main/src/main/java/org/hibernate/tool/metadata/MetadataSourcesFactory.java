package org.hibernate.tool.metadata;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

public class MetadataSourcesFactory {
	
	public static MetadataSources createJdbcSources(
			ReverseEngineeringStrategy reverseEngineeringStrategy, 
			Properties properties,
			boolean preferBasicCompositeIds) {
		return new JdbcMetadataSources(
				reverseEngineeringStrategy, 
				properties,
				preferBasicCompositeIds);
	}
	
	public static MetadataSources createJpaSources(String persistenceUnit, Properties properties) {
		return new JpaMetadataSources(persistenceUnit, properties);
	}
	
	public static MetadataSources createNativeSources(
			File cfgXmlFile,
			File[] mappingFiles,
			Properties properties) {
		return new NativeMetadataSources(
				cfgXmlFile, 
				mappingFiles, 
				properties);
	}
	
	public static MetadataSources createPojoSources() {
		return new PojoMetadataSources();
	}
	
}
