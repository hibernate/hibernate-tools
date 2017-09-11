package org.hibernate.tool.metadata;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

public class JdbcMetadataSources 
	extends JDBCMetaDataConfiguration 
	implements MetadataSources {
	
	public JdbcMetadataSources(
			ReverseEngineeringStrategy reverseEngineeringStrategy, 
			Properties properties,
			boolean preferBasicCompositeIds) {
		this(null, null, reverseEngineeringStrategy, properties, preferBasicCompositeIds);
	}

		public JdbcMetadataSources(
			File cfgXmlFile, 
			File[] mappingFiles, 
			ReverseEngineeringStrategy reverseEngineeringStrategy, 
			Properties properties,
			boolean preferBasicCompositeIds) {
		if (properties != null) {
			getProperties().putAll(properties);
		}
		if (reverseEngineeringStrategy != null) {
			setReverseEngineeringStrategy(reverseEngineeringStrategy);
		}
		setPreferBasicCompositeIds(preferBasicCompositeIds);
		readFromJDBC(); 
	}

}
