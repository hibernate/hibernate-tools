package org.hibernate.tool.metadata;

import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.cfg.JDBCBinder;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.TypeFactory;
import org.hibernate.type.TypeResolver;

public class JdbcMetadataDescriptor 
	extends JDBCMetaDataConfiguration 
	implements MetadataDescriptor {
	
	private Metadata metadata = null;
	private InFlightMetadataCollectorImpl metadataCollector;

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
	
	private InFlightMetadataCollectorImpl getMetadataCollector() {
		if (metadataCollector == null) {
			MetadataBuildingOptions options = getMetadataBuildingOptions();		
			BasicTypeRegistry basicTypeRegistry = handleTypes( options );
			metadataCollector = 
					new InFlightMetadataCollectorImpl(
					options,
					new TypeResolver( basicTypeRegistry, new TypeFactory() )
			);			
		}
		return metadataCollector;
	}
	
}
