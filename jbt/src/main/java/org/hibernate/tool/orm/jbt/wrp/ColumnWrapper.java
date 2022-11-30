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
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.type.spi.TypeConfiguration;

public class ColumnWrapper {
	
	private static final int DEFAULT_LENGTH = 255;
	private static final int DEFAULT_PRECISION = 19;
	private static final int DEFAULT_SCALE = 2;
	
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
	
	public int getPrecision() {
		Integer precision = wrappedColumn.getPrecision();
		return precision == null ? Integer.MIN_VALUE : precision.intValue();
	}

	public int getDefaultPrecision() {
		return DEFAULT_PRECISION;
	}

	public Integer getScale() {
		Integer scale = wrappedColumn.getScale();
		return scale == null ? Integer.MIN_VALUE : scale.intValue();
	}

	public int getDefaultScale() {
		return DEFAULT_SCALE;
	}

	public boolean isNullable() {
		return wrappedColumn.isNullable();
	}

	public Value getValue() {
		return wrappedColumn.getValue();
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
