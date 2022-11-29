package org.hibernate.tool.orm.jbt.wrp;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.mapping.Column;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.type.spi.TypeConfiguration;

public class ColumnWrapper extends Column {

	public String getSqlType(Configuration configuration) {
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		TypeConfiguration tc = ((MetadataImpl)metadata).getTypeConfiguration();
		return getSqlType(tc, buildDialect(configuration), metadata);
	}
	
	private Dialect buildDialect(Configuration configuration) {
		Map<String, Object> dialectPropertyMap = new HashMap<String, Object>();
		dialectPropertyMap.put(
				AvailableSettings.DIALECT, 
				configuration.getProperty(AvailableSettings.DIALECT));
		DialectFactory df = configuration
				.getStandardServiceRegistryBuilder()
				.build()
				.getService(DialectFactory.class);
		return df.buildDialect(dialectPropertyMap, null);
	}

}
