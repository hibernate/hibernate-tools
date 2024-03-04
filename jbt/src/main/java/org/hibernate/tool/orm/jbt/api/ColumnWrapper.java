package org.hibernate.tool.orm.jbt.api;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ColumnWrapper extends Wrapper {
	
	static final int DEFAULT_LENGTH = 255;
	
	default String getName() { return ((Column)getWrappedObject()).getName(); }
	default Integer getSqlTypeCode() { return ((Column)getWrappedObject()).getSqlTypeCode(); }
	default String getSqlType() { return ((Column)getWrappedObject()).getSqlType(); }
	default String getSqlType(Configuration configuration) { 
		return ((Column)getWrappedObject()).getSqlType(MetadataHelper.getMetadata(configuration)); 
		}
	default Long getLength() { 
		Long length = ((Column)getWrappedObject()).getLength();
		return length == null ? Integer.MIN_VALUE : length.longValue(); 
		}
	default int getDefaultLength() { return DEFAULT_LENGTH; }
	
}
