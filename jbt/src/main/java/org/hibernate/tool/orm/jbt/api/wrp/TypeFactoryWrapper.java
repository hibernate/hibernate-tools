package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.Map;

public interface TypeFactoryWrapper {

	TypeWrapper getBooleanType();
	TypeWrapper getByteType();
	TypeWrapper getBigIntegerType();
	TypeWrapper getShortType() ;
	TypeWrapper getCalendarType();
	TypeWrapper getCalendarDateType();
	TypeWrapper getIntegerType();
	TypeWrapper getBigDecimalType();
	TypeWrapper getCharacterType();
	TypeWrapper getClassType();
	TypeWrapper getCurrencyType();
	TypeWrapper getDateType();
	TypeWrapper getDoubleType();
	TypeWrapper getFloatType();
	TypeWrapper getLocaleType();
	TypeWrapper getLongType();
	TypeWrapper getStringType();
	TypeWrapper getTextType();
	TypeWrapper getTimeType();
	TypeWrapper getTimestampType();
	TypeWrapper getTimezoneType();
	TypeWrapper getTrueFalseType();
	TypeWrapper getYesNoType();
	TypeWrapper getNamedType(String name);
	TypeWrapper getBasicType(String name);
	Map<TypeWrapper, String> getTypeFormats();

}
