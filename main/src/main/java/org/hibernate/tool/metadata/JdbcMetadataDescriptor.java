package org.hibernate.tool.metadata;

import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.JDBCBinder;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

public class JdbcMetadataDescriptor 
	extends JDBCMetaDataConfiguration 
	implements MetadataDescriptor {
	
	protected Metadata metadata = null;

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

	public Metadata buildMetadata() {
//		readFromJDBC();
		return metadata;
	}
    
	public void readFromJDBC() {
		MetadataBuildingContext metadataBuildingContext = 
				getMetadataBuildingContext();
		metadata = getMetadataCollector()
				.buildMetadataInstance(metadataBuildingContext);
		JDBCBinder binder = new JDBCBinder(
				getServiceRegistry(), 
				getProperties(), 
				metadataBuildingContext, 
				getReverseEngineeringStrategy(), 
				preferBasicCompositeIds());
		binder.readFromDatabase(
				null, 
				null, 
				buildMapping(metadata));		
	}
	
	private MetadataBuildingContext getMetadataBuildingContext() {
		return new MetadataBuildingContextRootImpl(
					getMetadataBuildingOptions(), 
					getClassLoaderAccess(), 
					getMetadataCollector());					
	}
	
}
