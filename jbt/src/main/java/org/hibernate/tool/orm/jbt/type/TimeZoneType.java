package org.hibernate.tool.orm.jbt.type;

import java.util.TimeZone;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.TimeZoneJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class TimeZoneType extends AbstractSingleColumnStandardBasicType<TimeZone> {

	public static final TimeZoneType INSTANCE = new TimeZoneType();

	public TimeZoneType() {
		super(VarcharJdbcType.INSTANCE, TimeZoneJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "timezone";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

}
