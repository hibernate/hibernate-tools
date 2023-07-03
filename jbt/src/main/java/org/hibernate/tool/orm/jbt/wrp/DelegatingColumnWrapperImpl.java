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

public class DelegatingColumnWrapperImpl extends Column implements ColumnWrapper {
	
	private static final int DEFAULT_LENGTH = 255;
	private static final int DEFAULT_PRECISION = 19;
	private static final int DEFAULT_SCALE = 2;
	
	private Column delegate = null;
	
	public DelegatingColumnWrapperImpl(Column c) {
		delegate = c;
	}
	
	@Override
	public Column getWrappedObject() {
		return delegate;
	}
	
	@Override
	public String getSqlType(Configuration configuration) {
		Metadata metadata = MetadataHelper.getMetadata(configuration);
		TypeConfiguration tc = ((MetadataImpl)metadata).getTypeConfiguration();
		return delegate.getSqlType(tc, buildDialect(configuration), metadata);
	}
	
	@Override
	public Long getLength() {
		Long length = delegate.getLength();
		return length == null ? Integer.MIN_VALUE : length.longValue();
	}
	
	@Override
	public int getDefaultLength() {
		return DEFAULT_LENGTH;
	}
	
	@Override
	public Integer getPrecision() {
		Integer precision = delegate.getPrecision();
		return precision == null ? Integer.MIN_VALUE : precision.intValue();
	}

	@Override
	public int getDefaultPrecision() {
		return DEFAULT_PRECISION;
	}

	@Override
	public Integer getScale() {
		Integer scale = delegate.getScale();
		return scale == null ? Integer.MIN_VALUE : scale.intValue();
	}

	@Override
	public int getDefaultScale() {
		return DEFAULT_SCALE;
	}
	
	@Override
	public Value getValue() {
		Value val = delegate.getValue();
		if (val != null) {
			val = ValueWrapperFactory.createValueWrapper(val);
		}
		return val;
	}

	@Override
	public String getName() {
		return delegate.getName();
	}
	
	@Override
	public Integer getSqlTypeCode() {
		return delegate.getSqlTypeCode();
	}
	
	@Override
	public String getSqlType() {
		return delegate.getSqlType();
	}
	
	@Override
	public boolean isNullable() {
		return delegate.isNullable();
	}
	
	@Override
	public boolean isUnique() {
		return delegate.isUnique();
	}
	
	@Override
	public void setSqlType(String sqlType) {
		delegate.setSqlType(sqlType);
	}
	
	@Override 
	public boolean equals(Object o) {
		if (!(o instanceof DelegatingColumnWrapperImpl)) {
			return false;
		} else {
			return ((DelegatingColumnWrapperImpl)o).getWrappedObject().equals(delegate);
		}
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
