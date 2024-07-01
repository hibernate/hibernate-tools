package org.hibernate.tool.orm.jbt.internal.util;

import org.hibernate.boot.internal.BootstrapContextImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.orm.jbt.util.MockDialect;

public class DummyMetadataBuildingContext {
	
	public static MetadataBuildingContext INSTANCE = createInstance();
	
	private static MetadataBuildingContext createInstance() {
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting(AvailableSettings.DIALECT, MockDialect.class.getName());
		ssrb.applySetting(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		StandardServiceRegistry serviceRegistry = ssrb.build();
		MetadataBuildingOptions metadataBuildingOptions = new MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry);
		BootstrapContext bootstrapContext = new BootstrapContextImpl(serviceRegistry, metadataBuildingOptions);
		((MetadataBuilderImpl.MetadataBuildingOptionsImpl)metadataBuildingOptions).setBootstrapContext(bootstrapContext);
		InFlightMetadataCollector inflightMetadataCollector = new InFlightMetadataCollectorImpl(bootstrapContext, metadataBuildingOptions);
		return new MetadataBuildingContextRootImpl("JBoss Tools", bootstrapContext, metadataBuildingOptions, inflightMetadataCollector);
	}

}
