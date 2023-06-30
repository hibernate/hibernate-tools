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

public class ColumnWrapper extends Column {
	
	private static final int DEFAULT_LENGTH = 255;
	private static final int DEFAULT_PRECISION = 19;
	private static final int DEFAULT_SCALE = 2;
	
	public ColumnWrapper(String name) {
		super(name);
	}
	
	public String getSqlType(Configuration configuration) {
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		TypeConfiguration tc = ((MetadataImpl)metadata).getTypeConfiguration();
		return super.getSqlType(tc, buildDialect(configuration), metadata);
	}
	
	public Long getLength() {
		Long length = super.getLength();
		return length == null ? Integer.MIN_VALUE : length.longValue();
	}
	
	public int getDefaultLength() {
		return DEFAULT_LENGTH;
	}
	
	public Integer getPrecision() {
		Integer precision = super.getPrecision();
		return precision == null ? Integer.MIN_VALUE : precision.intValue();
	}

	public int getDefaultPrecision() {
		return DEFAULT_PRECISION;
	}

	public Integer getScale() {
		Integer scale = super.getScale();
		return scale == null ? Integer.MIN_VALUE : scale.intValue();
	}

	public int getDefaultScale() {
		return DEFAULT_SCALE;
	}
	
	@Override
	public Value getValue() {
		Value val = super.getValue();
		if (val != null) {
			val = ValueWrapperFactory.createValueWrapper(val);
		}
		return val;
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
