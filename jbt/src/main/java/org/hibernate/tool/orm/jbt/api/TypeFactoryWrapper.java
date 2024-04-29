package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.orm.jbt.internal.util.TypeRegistry;

public interface TypeFactoryWrapper {

	default TypeWrapper getBooleanType() { return TypeRegistry.getType("boolean"); }
	default TypeWrapper getByteType() { return TypeRegistry.getType("byte"); }
	default TypeWrapper getBigIntegerType() { return TypeRegistry.getType("big_integer"); }
	default TypeWrapper getShortType() { return TypeRegistry.getType("short"); }
	default TypeWrapper getCalendarType() { return TypeRegistry.getType("calendar"); }
	default TypeWrapper getCalendarDateType() { return TypeRegistry.getType("calendar_date"); }
	default TypeWrapper getIntegerType() { return TypeRegistry.getType("integer"); }
	default TypeWrapper getBigDecimalType() { return TypeRegistry.getType("big_decimal"); }
	default TypeWrapper getCharacterType() { return TypeRegistry.getType("character"); }
	default TypeWrapper getClassType() { return TypeRegistry.getType("class"); }
	default TypeWrapper getCurrencyType() { return TypeRegistry.getType("currency"); }
	default TypeWrapper getDateType() { return TypeRegistry.getType("date"); }
	default TypeWrapper getDoubleType() { return TypeRegistry.getType("double"); }
	default TypeWrapper getFloatType() { return TypeRegistry.getType("float"); }
	default TypeWrapper getLocaleType() { return TypeRegistry.getType("locale"); }
	default TypeWrapper getLongType() { return TypeRegistry.getType("long"); }
	default TypeWrapper getStringType() { return TypeRegistry.getType("string"); }
	default TypeWrapper getTextType() { return TypeRegistry.getType("text"); }
	default TypeWrapper getTimeType() { return TypeRegistry.getType("time"); }
	default TypeWrapper getTimestampType() { return TypeRegistry.getType("timestamp"); }
	default TypeWrapper getTimezoneType() { return TypeRegistry.getType("timezone"); }
	default TypeWrapper getTrueFalseType() { return TypeRegistry.getType("true_false"); }
	default TypeWrapper getYesNoType() { return TypeRegistry.getType("true_false"); }
	default TypeWrapper getNamedType(String name) { return TypeRegistry.getType(name); }

}
