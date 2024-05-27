package org.hibernate.tool.orm.jbt.api;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ColumnWrapper extends Wrapper {
	
	static final int DEFAULT_LENGTH = 255;
	static final int DEFAULT_PRECISION = 19;
	static final int DEFAULT_SCALE = 2;
	
	String getName();
	Integer getSqlTypeCode();
	String getSqlType();
	String getSqlType(Configuration configuration);
	long getLength();
	int getDefaultLength();
	int getPrecision();
	int getDefaultPrecision();
	int getScale();
	int getDefaultScale();
	boolean isNullable();
	Value getValue();
	boolean isUnique();
	void setSqlType(String sqlType);
	
}
