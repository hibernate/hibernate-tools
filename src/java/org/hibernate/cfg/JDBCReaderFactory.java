package org.hibernate.cfg;

import java.util.Properties;

import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.ServiceRegistry;

final public class JDBCReaderFactory {

	
	public static JDBCReader newJDBCReader(Properties cfg, Settings settings,
			ReverseEngineeringStrategy revengStrategy, ServiceRegistry serviceRegistry) {
	
		MetaDataDialect mdd = newMetaDataDialect( serviceRegistry.getService(JdbcServices.class).getDialect(), cfg );

		return newJDBCReader( settings, revengStrategy, mdd, serviceRegistry);
	}

	public static JDBCReader newJDBCReader(Settings settings, ReverseEngineeringStrategy revengStrategy, MetaDataDialect mdd,
			ServiceRegistry serviceRegistry) {
		JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
		return new JDBCReader( mdd, jdbcServices.getConnectionProvider(), jdbcServices.getSqlExceptionHelper()
				.getSqlExceptionConverter(), settings.getDefaultCatalogName(), settings.getDefaultSchemaName(), revengStrategy );
	}

	public static MetaDataDialect newMetaDataDialect(Dialect dialect, Properties cfg) {
		return new MetaDataDialectFactory().createMetaDataDialect(dialect, cfg);		
		
	}
	
}
