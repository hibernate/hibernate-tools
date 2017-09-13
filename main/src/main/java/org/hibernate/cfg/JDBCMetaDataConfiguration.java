/*
 * Created on 26-Nov-2004
 *
 */
package org.hibernate.cfg;

import org.dom4j.Element;
import org.hibernate.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.BasicTypeRegistration;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.metadata.MetadataDescriptor;
import org.hibernate.type.BasicType;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author max
 *
 */
public class JDBCMetaDataConfiguration extends Configuration implements MetadataDescriptor {

    private static final Logger log = LoggerFactory.getLogger(JDBCMetaDataConfiguration.class);
	protected ReverseEngineeringStrategy revEngStrategy = new DefaultReverseEngineeringStrategy();
	protected StandardServiceRegistry serviceRegistry = null;
	
	protected InFlightMetadataCollectorImpl metadataCollector;
	protected ClassLoaderAccess classLoaderAccess = null;
	protected MetadataBuildingOptions metadataBuildingOptions = null;
	protected Metadata metadata = null;
	
	public Metadata buildMetadata() {
//		readFromJDBC();
		return metadata;
	}
    
	protected MetadataBuildingOptions getMetadataBuildingOptions() {
		if (metadataBuildingOptions == null) {
			metadataBuildingOptions = 
					new MetadataBuildingOptionsImpl( getServiceRegistry() );
		}
		return metadataBuildingOptions;
	}
	
	protected ClassLoaderAccess getClassLoaderAccess() {
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
	
	protected InFlightMetadataCollectorImpl getMetadataCollector() {
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
	
	protected MetadataBuildingContext getMetadataBuildingContext() {
		return new MetadataBuildingContextRootImpl(
					getMetadataBuildingOptions(), 
					getClassLoaderAccess(), 
					getMetadataCollector());					
	}
	
	public StandardServiceRegistry getServiceRegistry(){
		if(serviceRegistry == null){
			serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(getProperties())
				.build();
		}
		return serviceRegistry;
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
	
	static protected Mapping buildMapping(final Metadata metadata) {
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

	
	private boolean ignoreconfigxmlmapppings = true;
	// set to true and fk's that are part of a primary key will just be mapped as the raw value and as a readonly property. if false, it will be <many-to-one-key-property
    private boolean preferBasicCompositeIds = true;
	
    /**
     * If true, compositeid's will not create key-many-to-one and
     * non-updatable/non-insertable many-to-one will be created instead. 
     * @return
     */
    public boolean preferBasicCompositeIds() {
        return preferBasicCompositeIds ;
    }    
   
    public void setPreferBasicCompositeIds(boolean flag) {
        preferBasicCompositeIds = flag;
    }
	    
    protected void parseMappingElement(Element subelement, String name) {
        if(!ignoreconfigxmlmapppings ) {  
        	//FIXME the method is private
           // super.parseMappingElement(subelement, name);
        } 
        else {
            log.info("Ignoring " + name + " mapping");
        }
    }

	public void setReverseEngineeringStrategy(ReverseEngineeringStrategy reverseEngineeringStrategy) {
		this.revEngStrategy = reverseEngineeringStrategy;		
	}
	
	public ReverseEngineeringStrategy getReverseEngineeringStrategy() {
		return revEngStrategy;
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
