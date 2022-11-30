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

public class ColumnWrapper {
	
	private static final int DEFAULT_LENGTH = 255;
	
	private Column wrappedColumn = new Column();
	
	public String getName() {
		return wrappedColumn.getName();
	}

	public Integer getSqlTypeCode() {
		return wrappedColumn.getSqlTypeCode();
	}

	public String getSqlType() {
		return wrappedColumn.getSqlType();
	}

	public String getSqlType(Configuration configuration) {
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		TypeConfiguration tc = ((MetadataImpl)metadata).getTypeConfiguration();
		return wrappedColumn.getSqlType(tc, buildDialect(configuration), metadata);
	}
	
	public int getLength() {
		Long length = wrappedColumn.getLength();
		return length == null ? Integer.MIN_VALUE : length.intValue();
	}
	
	public int getDefaultLength() {
		return DEFAULT_LENGTH;
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
