package org.hibernate.tool.metadata;

import java.util.Properties;

import org.hibernate.MappingException;
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
import org.hibernate.cfg.JDBCBinder;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.type.BasicType;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

public class JdbcMetadataDescriptor 
	extends JDBCMetaDataConfiguration 
	implements MetadataDescriptor {
	
	private Metadata metadata = null;
	private InFlightMetadataCollectorImpl metadataCollector = null;
	private ClassLoaderAccess classLoaderAccess = null;
	private MetadataBuildingOptions metadataBuildingOptions = null;
	private StandardServiceRegistry serviceRegistry = null;

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
    
	private void readFromJDBC() {
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
	
	private BasicTypeRegistry handleTypes(MetadataBuildingOptions options) {
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
	
	private StandardServiceRegistry getServiceRegistry(){
		if(serviceRegistry == null){
			serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(getProperties())
				.build();
		}
		return serviceRegistry;
	}
	
	private Mapping buildMapping(final Metadata metadata) {
		return new Mapping() {
			/**
			 * Returns the identifier type of a mapped class
			 */
			public Type getIdentifierType(String persistentClass) throws MappingException {
				final PersistentClass pc = metadata.getEntityBinding(persistentClass);
				if (pc==null) throw new MappingException("persistent class not known: " + persistentClass);
				return pc.getIdentifier().getType();
			}

			public String getIdentifierPropertyName(String persistentClass) throws MappingException {
				final PersistentClass pc = metadata.getEntityBinding(persistentClass);
				if (pc==null) throw new MappingException("persistent class not known: " + persistentClass);
				if ( !pc.hasIdentifierProperty() ) return null;
				return pc.getIdentifierProperty().getName();
			}

            public Type getReferencedPropertyType(String persistentClass, String propertyName) throws MappingException
            {
				final PersistentClass pc = metadata.getEntityBinding(persistentClass);
				if (pc==null) throw new MappingException("persistent class not known: " + persistentClass);
				Property prop = pc.getProperty(propertyName);
				if (prop==null)  throw new MappingException("property not known: " + persistentClass + '.' + propertyName);
				return prop.getType();
			}

			public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
				return null;
			}
		};
	}
}
