package org.hibernate.tool.metadata;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

public class MetadataSourcesFactory {
	
	public static MetadataDescriptor createJdbcSources(
			ReverseEngineeringStrategy reverseEngineeringStrategy, 
			Properties properties,
			boolean preferBasicCompositeIds) {
		return new JdbcMetadataDescriptor(
				reverseEngineeringStrategy, 
				properties,
				preferBasicCompositeIds);
	}
	
	public static MetadataDescriptor createJpaSources(String persistenceUnit, Properties properties) {
		return new JpaMetadataDescriptor(persistenceUnit, properties);
	}
	
	public static MetadataDescriptor createNativeSources(
			File cfgXmlFile,
			File[] mappingFiles,
			Properties properties) {
		return new NativeMetadataSources(
				cfgXmlFile, 
				mappingFiles, 
				properties);
	}
	
	public static MetadataDescriptor createPojoSources() {
		return new PojoMetadataSources();
	}
	
}
