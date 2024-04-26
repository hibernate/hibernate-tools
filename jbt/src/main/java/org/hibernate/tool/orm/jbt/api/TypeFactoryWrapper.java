package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.orm.jbt.internal.util.TypeFactoryWrapperHelper;

public interface TypeFactoryWrapper {

	default TypeWrapper getBooleanType() { return TypeFactoryWrapperHelper.typeRegistry().get("boolean"); }
	default TypeWrapper getByteType() { return TypeFactoryWrapperHelper.typeRegistry().get("byte"); }
	default TypeWrapper getBigIntegerType() { return TypeFactoryWrapperHelper.typeRegistry().get("big_integer"); }
	default TypeWrapper getShortType() { return TypeFactoryWrapperHelper.typeRegistry().get("short"); }
	default TypeWrapper getCalendarType() { return TypeFactoryWrapperHelper.typeRegistry().get("calendar"); }
	default TypeWrapper getCalendarDateType() { return TypeFactoryWrapperHelper.typeRegistry().get("calendar_date"); }
	default TypeWrapper getIntegerType() { return TypeFactoryWrapperHelper.typeRegistry().get("integer"); }
	default TypeWrapper getBigDecimalType() { return TypeFactoryWrapperHelper.typeRegistry().get("big_decimal"); }
	default TypeWrapper getCharacterType() { return TypeFactoryWrapperHelper.typeRegistry().get("character"); }
	default TypeWrapper getClassType() { return TypeFactoryWrapperHelper.typeRegistry().get("class"); }
	default TypeWrapper getCurrencyType() { return TypeFactoryWrapperHelper.typeRegistry().get("currency"); }
	default TypeWrapper getDateType() { return TypeFactoryWrapperHelper.typeRegistry().get("date"); }
	default TypeWrapper getDoubleType() { return TypeFactoryWrapperHelper.typeRegistry().get("double"); }
	default TypeWrapper getFloatType() { return TypeFactoryWrapperHelper.typeRegistry().get("float"); }
	default TypeWrapper getLocaleType() { return TypeFactoryWrapperHelper.typeRegistry().get("locale"); }
	default TypeWrapper getLongType() { return TypeFactoryWrapperHelper.typeRegistry().get("long"); }
	default TypeWrapper getStringType() { return TypeFactoryWrapperHelper.typeRegistry().get("string"); }
	default TypeWrapper getTextType() { return TypeFactoryWrapperHelper.typeRegistry().get("text"); }
	default TypeWrapper getTimeType() { return TypeFactoryWrapperHelper.typeRegistry().get("time"); }
	default TypeWrapper getTimestampType() { return TypeFactoryWrapperHelper.typeRegistry().get("timestamp"); }

}
