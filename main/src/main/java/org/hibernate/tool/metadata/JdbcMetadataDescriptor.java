package org.hibernate.tool.metadata;

import java.util.Properties;

import org.hibernate.cfg.JDBCBinder;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

public class JdbcMetadataDescriptor 
	extends JDBCMetaDataConfiguration 
	implements MetadataDescriptor {
	
	public JdbcMetadataDescriptor(
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

	public void readFromJDBC() {
		metadata = getMetadataCollector()
				.buildMetadataInstance(getMetadataBuildingContext());
		JDBCBinder binder = new JDBCBinder(
				getServiceRegistry(), 
				getProperties(), 
				getMetadataBuildingContext(), 
				getReverseEngineeringStrategy(), 
				preferBasicCompositeIds());
		binder.readFromDatabase(
				null, 
				null, 
				buildMapping(metadata));		
	}
	
}
