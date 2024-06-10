package org.hibernate.tool.orm.jbt.api.wrp;

public interface ColumnWrapper extends Wrapper {
	
	static final int DEFAULT_LENGTH = 255;
	static final int DEFAULT_PRECISION = 19;
	static final int DEFAULT_SCALE = 2;
	
	String getName();
	Integer getSqlTypeCode();
	String getSqlType();
	String getSqlType(ConfigurationWrapper configurationWrapper);
	long getLength();
	int getDefaultLength();
	int getPrecision();
	int getDefaultPrecision();
	int getScale();
	int getDefaultScale();
	boolean isNullable();
	ValueWrapper getValue();
	boolean isUnique();
	void setSqlType(String sqlType);
	
}
