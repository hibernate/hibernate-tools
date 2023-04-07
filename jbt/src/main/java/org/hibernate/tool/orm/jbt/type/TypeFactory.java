package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.spi.TypeConfiguration;

public class TypeFactory {
	
	public static final TypeFactory INSTANCE = new TypeFactory();
	
	private static BasicTypeRegistry TYPE_REGISTRY = new TypeConfiguration().getBasicTypeRegistry();
	
	public static final Type BOOLEAN_TYPE = TYPE_REGISTRY.getRegisteredType("boolean");
	public static final Type BYTE_TYPE = TYPE_REGISTRY.getRegisteredType("byte");
	public static final Type BIG_INTEGER_TYPE = TYPE_REGISTRY.getRegisteredType("big_integer");
	public static final Type SHORT_TYPE = TYPE_REGISTRY.getRegisteredType("short");
	public static final Type CALENDAR_TYPE = TYPE_REGISTRY.getRegisteredType("calendar");
	public static final Type CALENDAR_DATE_TYPE = TYPE_REGISTRY.getRegisteredType("calendar_date");
	public static final Type INTEGER_TYPE = TYPE_REGISTRY.getRegisteredType("integer");
	public static final Type BIG_DECIMAL_TYPE = TYPE_REGISTRY.getRegisteredType("big_decimal");
	public static final Type CHARACTER_TYPE = TYPE_REGISTRY.getRegisteredType("character");
	public static final Type CLASS_TYPE = TYPE_REGISTRY.getRegisteredType("class");
	public static final Type CURRENCY_TYPE = TYPE_REGISTRY.getRegisteredType("currency");
	public static final Type DATE_TYPE = TYPE_REGISTRY.getRegisteredType("date");
	public static final Type DOUBLE_TYPE = TYPE_REGISTRY.getRegisteredType("double");
	public static final Type FLOAT_TYPE = TYPE_REGISTRY.getRegisteredType("float");
	public static final Type LOCALE_TYPE = TYPE_REGISTRY.getRegisteredType("locale");
	public static final Type LONG_TYPE = TYPE_REGISTRY.getRegisteredType("long");
	public static final Type STRING_TYPE = TYPE_REGISTRY.getRegisteredType("string");
	public static final Type TEXT_TYPE = TYPE_REGISTRY.getRegisteredType("text");
	public static final Type TIME_TYPE = TYPE_REGISTRY.getRegisteredType("time");
	public static final Type TIMESTAMP_TYPE = TYPE_REGISTRY.getRegisteredType("timestamp");
	public static final Type TIMEZONE_TYPE = TYPE_REGISTRY.getRegisteredType("timezone");
	public static final Type TRUE_FALSE_TYPE = TYPE_REGISTRY.getRegisteredType("true_false");
	public static final Type YES_NO_TYPE = TYPE_REGISTRY.getRegisteredType("yes_no");
	
	private TypeFactory() {}

	public Type getBooleanType() {
		return BOOLEAN_TYPE;
	}

	public Type getByteType() {
		return BYTE_TYPE;
	}

	public Type getBigIntegerType() {
		return BIG_INTEGER_TYPE;
	}

	public Type getShortType() {
		return SHORT_TYPE;
	}

	public Type getCalendarType() {
		return CALENDAR_TYPE;
	}

	public Type getCalendarDateType() {
		return CALENDAR_DATE_TYPE;
	}

	public Type getIntegerType() {
		return INTEGER_TYPE;
	}

	public Type getBigDecimalType() {
		return BIG_DECIMAL_TYPE;
	}

	public Type getCharacterType() {
		return CHARACTER_TYPE;
	}

	public Type getClassType() {
		return CLASS_TYPE;
	}

	public Type getCurrencyType() {
		return CURRENCY_TYPE;
	}

	public Type getDateType() {
		return DATE_TYPE;
	}

	public Type getDoubleType() {
		return DOUBLE_TYPE;
	}

	public Type getFloatType() {
		return FLOAT_TYPE;
	}

	public Type getLocaleType() {
		return LOCALE_TYPE;
	}

	public Type getLongType() {
		return LONG_TYPE;
	}

	public Type getStringType() {
		return STRING_TYPE;
	}

	public Type getTextType() {
		return TEXT_TYPE;
	}

	public Type getTimeType() {
		return TIME_TYPE;
	}

	public Type getTimestampType() {
		return TIMESTAMP_TYPE;
	}

	public Type getTimezoneType() {
		return TIMEZONE_TYPE;
	}

	public Type getTrueFalseType() {
		return TRUE_FALSE_TYPE;
	}

	public Type getYesNoType() {
		return YES_NO_TYPE;
	}
	
	public Type getNamedType(String name) {
		return TYPE_REGISTRY.getRegisteredType(name);
	}
	
}
