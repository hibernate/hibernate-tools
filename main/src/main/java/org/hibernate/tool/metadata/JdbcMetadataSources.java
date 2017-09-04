package org.hibernate.tool.metadata;

import java.util.Properties;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

public class JdbcMetadataSources 
	extends JDBCMetaDataConfiguration 
	implements MetadataSources {
	
	public JdbcMetadataSources(
			ReverseEngineeringStrategy reverseEngineeringStrategy, 
			Properties properties) {
		if (properties != null) {
			getProperties().putAll(properties);
		}
		if (reverseEngineeringStrategy != null) {
			setReverseEngineeringStrategy(reverseEngineeringStrategy);
		}
		readFromJDBC();
	}

}
