package org.hibernate.tool.orm.jbt.wrp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.internal.BootstrapContextImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengDialect;
import org.hibernate.tool.api.reveng.RevengDialectFactory;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.RevengMetadataCollector;
import org.hibernate.tool.internal.reveng.reader.DatabaseReader;

public class DatabaseReaderWrapperFactory {
	
	public static DatabaseReaderWrapper createDatabaseReaderWrapper(
			Properties properties, 
			RevengStrategy revengStrategy) {
		return new DatabaseReaderWrapperImpl(properties, revengStrategy);
	}
	
	static interface DatabaseReaderWrapper extends Wrapper {
		Map<String, List<Table>> collectDatabaseTables();
	}
	
	static class DatabaseReaderWrapperImpl implements DatabaseReaderWrapper {
		
		DatabaseReader databaseReader = null;
		RevengMetadataCollector revengMetadataCollector = null;
		
		public DatabaseReaderWrapperImpl(
				Properties properties, 
				RevengStrategy revengStrategy) {
			StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(properties)
					.build();
			MetadataBuildingOptionsImpl metadataBuildingOptions = 
					new MetadataBuildingOptionsImpl(serviceRegistry);	
			BootstrapContextImpl bootstrapContext = new BootstrapContextImpl(
					serviceRegistry, 
					metadataBuildingOptions);
			metadataBuildingOptions.setBootstrapContext(bootstrapContext);
			InFlightMetadataCollectorImpl metadataCollector = new InFlightMetadataCollectorImpl(
					bootstrapContext,
					metadataBuildingOptions);
			RevengDialect mdd = RevengDialectFactory
					.createMetaDataDialect(
							serviceRegistry.getService(JdbcServices.class).getDialect(), 
							properties );
		    databaseReader = DatabaseReader.create(properties,revengStrategy,mdd, serviceRegistry);
		    MetadataBuildingContext metadataBuildingContext = new MetadataBuildingContextRootImpl("JBoss Tools", bootstrapContext, metadataBuildingOptions, metadataCollector);
		    revengMetadataCollector = new RevengMetadataCollector(metadataBuildingContext);
		}
		
		public Map<String, List<Table>> collectDatabaseTables() {
			databaseReader.readDatabaseSchema(revengMetadataCollector);
			Map<String, List<Table>> result = new HashMap<String, List<Table>>();
			for (Table table : revengMetadataCollector.getTables()) {
				String qualifier = "";
				if (table.getCatalog() != null) {
					qualifier += table.getCatalog();
				}
				if (table.getSchema() != null) {
					if (!"".equals(qualifier)) {
						qualifier += ".";
					}
					qualifier += table.getSchema();
				}
				List<Table> list = result.get(qualifier);
				if (list == null) {
					list = new ArrayList<Table>();
					result.put(qualifier, list);
				}
				list.add(table);
			}			
			return result;
		}
		
	}
	
	

}
