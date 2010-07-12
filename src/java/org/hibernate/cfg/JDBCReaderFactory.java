package org.hibernate.cfg;

import java.util.Properties;

import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.dialect.Dialect;

final public class JDBCReaderFactory {

	
	public static JDBCReader newJDBCReader(Properties cfg, Settings settings,
			ReverseEngineeringStrategy revengStrategy) {

		MetaDataDialect mdd = newMetaDataDialect( settings.getDialect(), cfg );

		return newJDBCReader( settings, revengStrategy, mdd );
	}

	public static JDBCReader newJDBCReader(Settings settings, ReverseEngineeringStrategy revengStrategy, MetaDataDialect mdd) {	
		return new JDBCReader( mdd, settings.getConnectionProvider(), settings
				.getSQLExceptionConverter(), settings.getDefaultCatalogName(), settings.getDefaultSchemaName(), revengStrategy );
	}

	public static MetaDataDialect newMetaDataDialect(Dialect dialect, Properties cfg) {
		return new MetaDataDialectFactory().createMetaDataDialect(dialect, cfg);		
		
	}
	
}
