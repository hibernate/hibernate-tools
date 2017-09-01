package org.hibernate.cfg;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.BasicTypeRegistration;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.metadata.MetadataSources;
import org.hibernate.type.BasicType;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.TypeFactory;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

public class PojoMetaDataConfiguration extends Configuration implements MetadataSources {

	private StandardServiceRegistry serviceRegistry = null;
	
	private InFlightMetadataCollectorImpl metadataCollector;
	private ClassLoaderAccess classLoaderAccess = null;
	private MetadataBuildingOptions metadataBuildingOptions = null;
	private MetadataBuildingContext metadataBuildingContext = null;
	private Metadata metadata = null;
	
	public Metadata buildMetadata() {
		return getMetadataCollector().buildMetadataInstance(getMetadataBuildingContext());
	}
	
	public void addClass(PersistentClass persistentClass) {
		getMetadataCollector().addEntityBinding(persistentClass);
	}
    
	public Metadata getMetadata() {
		if (metadata == null) {
			metadata = buildMetadata();
		}
		return metadata;
	}
	
	public MetadataImplementor getMetadataImplementor() {
		return getMetadataCollector();
	}
	
	private ClassLoaderAccess getClassLoaderAccess() {
		if (classLoaderAccess == null) {
			MetadataBuildingOptions options = getMetadataBuildingOptions();		
			ClassLoaderService classLoaderService = 
					options.getServiceRegistry().getService( 
							ClassLoaderService.class );
			classLoaderAccess = new ClassLoaderAccessImpl(
					options.getTempClassLoader(),
					classLoaderService
			);			
		}
		return classLoaderAccess;
	}
	
	private MetadataBuildingOptions getMetadataBuildingOptions() {
		if (metadataBuildingOptions == null) {
			metadataBuildingOptions = 
					new MetadataBuildingOptionsImpl( getServiceRegistry() );
		}
		return metadataBuildingOptions;
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
	
	private MetadataBuildingContext getMetadataBuildingContext() {
		if (metadataBuildingContext == null) {
			metadataBuildingContext = new MetadataBuildingContextRootImpl(
					getMetadataBuildingOptions(), 
					getClassLoaderAccess(), 
					getMetadataCollector());					
		}
		return metadataBuildingContext;
	}
	
	private StandardServiceRegistry getServiceRegistry(){
		if(serviceRegistry == null){
			serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(getProperties())
				.build();
		}
		return serviceRegistry;
	}
	
	private static BasicTypeRegistry handleTypes(MetadataBuildingOptions options) {
		final ClassLoaderService classLoaderService = options.getServiceRegistry().getService( ClassLoaderService.class );

		// ultimately this needs to change a little bit to account for HHH-7792
		final BasicTypeRegistry basicTypeRegistry = new BasicTypeRegistry();

		final TypeContributions typeContributions = new TypeContributions() {
			public void contributeType(BasicType type) {
				basicTypeRegistry.register( type );
			}

			public void contributeType(BasicType type, String... keys) {
				basicTypeRegistry.register( type, keys );
			}

			public void contributeType(UserType type, String... keys) {
				basicTypeRegistry.register( type, keys );
			}

			public void contributeType(CompositeUserType type, String... keys) {
				basicTypeRegistry.register( type, keys );
			}
		};

		// add Dialect contributed types
		final Dialect dialect = options.getServiceRegistry().getService( JdbcServices.class ).getDialect();
		dialect.contributeTypes( typeContributions, options.getServiceRegistry() );

		// add TypeContributor contributed types.
		for ( TypeContributor contributor : classLoaderService.loadJavaServices( TypeContributor.class ) ) {
			contributor.contribute( typeContributions, options.getServiceRegistry() );
		}

		// add explicit application registered types
		for ( BasicTypeRegistration basicTypeRegistration : options.getBasicTypeRegistrations() ) {
			basicTypeRegistry.register(
					basicTypeRegistration.getBasicType(),
					basicTypeRegistration.getRegistrationKeys()
			);
		}

		return basicTypeRegistry;
	}

}
