package org.hibernate.tool.orm.jbt.api;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ColumnWrapper extends Wrapper {
	
	static final int DEFAULT_LENGTH = 255;
	static final int DEFAULT_PRECISION = 19;
	
	default String getName() { return ((Column)getWrappedObject()).getName(); }
	default Integer getSqlTypeCode() { return ((Column)getWrappedObject()).getSqlTypeCode(); }
	default String getSqlType() { return ((Column)getWrappedObject()).getSqlType(); }
	default String getSqlType(Configuration configuration) { 
		return ((Column)getWrappedObject()).getSqlType(MetadataHelper.getMetadata(configuration)); 
		}
	default long getLength() { 
		Long length = ((Column)getWrappedObject()).getLength();
		return length == null ? Integer.MIN_VALUE : length; 
		}
	default int getDefaultLength() { return DEFAULT_LENGTH; }
	default int getPrecision() {
		Integer precision = ((Column)getWrappedObject()).getPrecision();
		return precision == null ? Integer.MIN_VALUE : precision;
	}
	default int getDefaultPrecision() { return DEFAULT_PRECISION; }
	default int getScale() {
		Integer scale = ((Column)getWrappedObject()).getScale();
		return scale == null ? Integer.MIN_VALUE : scale;
		
	}
	
}
