package org.hibernate.tool.orm.jbt.type;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JdbcTimestampJavaType;
import org.hibernate.type.descriptor.jdbc.TimestampJdbcType;

public class TimestampType extends AbstractSingleColumnStandardBasicType<Date> {

	public static final TimestampType INSTANCE = new TimestampType();

	public TimestampType() {
		super(TimestampJdbcType.INSTANCE, JdbcTimestampJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "timestamp";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), Timestamp.class.getName(), Date.class.getName() };
	}

}
