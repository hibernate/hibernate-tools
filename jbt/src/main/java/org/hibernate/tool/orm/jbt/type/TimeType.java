package org.hibernate.tool.orm.jbt.type;

import java.sql.Time;
import java.util.Date;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JdbcTimeJavaType;
import org.hibernate.type.descriptor.jdbc.TimeJdbcType;

public class TimeType extends AbstractSingleColumnStandardBasicType<Date> {

	public static final TimeType INSTANCE = new TimeType();

	public TimeType() {
		super(TimeJdbcType.INSTANCE, JdbcTimeJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "time";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), Time.class.getName() };
	}

}
