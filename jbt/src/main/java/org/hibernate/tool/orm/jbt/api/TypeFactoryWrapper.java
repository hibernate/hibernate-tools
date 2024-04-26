package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.orm.jbt.internal.util.TypeRegistry;

public interface TypeFactoryWrapper {

	default TypeWrapper getBooleanType() { return TypeRegistry.typeRegistry().get("boolean"); }
	default TypeWrapper getByteType() { return TypeRegistry.typeRegistry().get("byte"); }
	default TypeWrapper getBigIntegerType() { return TypeRegistry.typeRegistry().get("big_integer"); }
	default TypeWrapper getShortType() { return TypeRegistry.typeRegistry().get("short"); }
	default TypeWrapper getCalendarType() { return TypeRegistry.typeRegistry().get("calendar"); }
	default TypeWrapper getCalendarDateType() { return TypeRegistry.typeRegistry().get("calendar_date"); }
	default TypeWrapper getIntegerType() { return TypeRegistry.typeRegistry().get("integer"); }
	default TypeWrapper getBigDecimalType() { return TypeRegistry.typeRegistry().get("big_decimal"); }
	default TypeWrapper getCharacterType() { return TypeRegistry.typeRegistry().get("character"); }
	default TypeWrapper getClassType() { return TypeRegistry.typeRegistry().get("class"); }
	default TypeWrapper getCurrencyType() { return TypeRegistry.typeRegistry().get("currency"); }
	default TypeWrapper getDateType() { return TypeRegistry.typeRegistry().get("date"); }
	default TypeWrapper getDoubleType() { return TypeRegistry.typeRegistry().get("double"); }
	default TypeWrapper getFloatType() { return TypeRegistry.typeRegistry().get("float"); }
	default TypeWrapper getLocaleType() { return TypeRegistry.typeRegistry().get("locale"); }
	default TypeWrapper getLongType() { return TypeRegistry.typeRegistry().get("long"); }
	default TypeWrapper getStringType() { return TypeRegistry.typeRegistry().get("string"); }
	default TypeWrapper getTextType() { return TypeRegistry.typeRegistry().get("text"); }
	default TypeWrapper getTimeType() { return TypeRegistry.typeRegistry().get("time"); }
	default TypeWrapper getTimestampType() { return TypeRegistry.typeRegistry().get("timestamp"); }
	default TypeWrapper getTimezoneType() { return TypeRegistry.typeRegistry().get("timezone"); }
	default TypeWrapper getTrueFalseType() { return TypeRegistry.typeRegistry().get("true_false"); }
	default TypeWrapper getYesNoType() { return TypeRegistry.typeRegistry().get("true_false"); }

}
