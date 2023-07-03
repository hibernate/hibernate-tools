package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Value;


public interface ColumnWrapper extends Wrapper {

	@Override Column getWrappedObject();
	String getName();
	Integer getSqlTypeCode();
	String getSqlType();
	Long getLength();
	int getDefaultLength();
	Integer getPrecision();
	int getDefaultPrecision();
	Integer getScale();
	int getDefaultScale();
	boolean isNullable();
	Value getValue();
	boolean isUnique();
	String getSqlType(Configuration configuration);
	void setSqlType(String sqlType);

}
