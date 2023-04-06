package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.Type;

public class TypeFactory {
	
	public static final TypeFactory INSTANCE = new TypeFactory();
	
	private TypeFactory() {}

	public Type getBooleanType() {
		return BooleanType.INSTANCE;
	}

	public Type getByteType() {
		return ByteType.INSTANCE;
	}

	public Type getBigIntegerType() {
		return BigIntegerType.INSTANCE;
	}

	public Type getShortType() {
		return ShortType.INSTANCE;
	}

	public Type getCalendarType() {
		return CalendarType.INSTANCE;
	}

	public Type getCalendarDateType() {
		return CalendarDateType.INSTANCE;
	}

	public Object getIntegerType() {
		return IntegerType.INSTANCE;
	};
	
}
