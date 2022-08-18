package org.hibernate.tool.orm.jbt.type;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.CalendarJavaType;
import org.hibernate.type.descriptor.jdbc.TimestampJdbcType;

public class CalendarType extends AbstractSingleColumnStandardBasicType<Calendar> {

	public static final CalendarType INSTANCE = new CalendarType();

	public CalendarType() {
		super(TimestampJdbcType.INSTANCE, CalendarJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "calendar";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), Calendar.class.getName(), GregorianCalendar.class.getName() };
	}

}
