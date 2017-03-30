package org.hibernate.cfg;

import java.util.Properties;

import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.service.ServiceRegistry;

final public class JDBCReaderFactory {

	
	public static JDBCReader newJDBCReader(
			Properties properties, 
			ReverseEngineeringStrategy revengStrategy, 
			ServiceRegistry serviceRegistry) {	
		MetaDataDialect mdd = MetaDataDialectFactory
				.createMetaDataDialect(
						serviceRegistry.getService(JdbcServices.class).getDialect(), 
						properties );
		return newJDBCReader(properties, revengStrategy, mdd, serviceRegistry);
	}

	public static JDBCReader newJDBCReader(
			Properties properties, 
			ReverseEngineeringStrategy revengStrategy, 
			MetaDataDialect mdd,
			ServiceRegistry serviceRegistry) {
		SQLExceptionConverter sqlExceptionConverter = serviceRegistry
				.getService(JdbcServices.class)
				.getSqlExceptionHelper()
				.getSqlExceptionConverter();
		ConnectionProvider connectionProvider = serviceRegistry
				.getService(ConnectionProvider.class);
		String defaultCatalogName = properties
				.getProperty(AvailableSettings.DEFAULT_CATALOG);
		String defaultSchemaName = properties
				.getProperty(AvailableSettings.DEFAULT_SCHEMA);
		return new JDBCReader(
				mdd, 
				connectionProvider, 
				sqlExceptionConverter, 
				defaultCatalogName, 
				defaultSchemaName, 
				revengStrategy );
	}

}
