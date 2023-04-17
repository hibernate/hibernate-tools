package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;
import org.hibernate.type.Type;
import org.junit.jupiter.api.Test;

public class TypeFactoryWrapperTest {
	
	@Test
	public void testConstruction() {
		assertNotNull(TypeFactoryWrapper.INSTANCE);
	}
	
	@Test
	public void testGetBooleanType() {
		TypeWrapper booleanTypeWrapper = TypeFactoryWrapper.INSTANCE.getBooleanType();
		assertEquals("boolean", ((Type)booleanTypeWrapper.getWrappedObject()).getName());
	}

	@Test
	public void testGetByteType() {
		TypeWrapper byteTypeWrapper = TypeFactoryWrapper.INSTANCE.getByteType();
		assertEquals("byte", ((Type)byteTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetBigIntegerType() {
		TypeWrapper bigIntegerTypeWrapper = TypeFactoryWrapper.INSTANCE.getBigIntegerType();
		assertEquals("big_integer", ((Type)bigIntegerTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetShortType() {
		TypeWrapper shortTypeWrapper = TypeFactoryWrapper.INSTANCE.getShortType();
		assertEquals("short", ((Type)shortTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetCalendarType() {
		TypeWrapper calendarTypeWrapper = TypeFactoryWrapper.INSTANCE.getCalendarType();
		assertEquals("calendar", ((Type)calendarTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetCalendarDateType() {
		TypeWrapper calendarDateTypeWrapper = TypeFactoryWrapper.INSTANCE.getCalendarDateType();
		assertEquals("calendar_date", ((Type)calendarDateTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetIntegerType() {
		TypeWrapper integerTypeWrapper = TypeFactoryWrapper.INSTANCE.getIntegerType();
		assertEquals("integer", ((Type)integerTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetBigDecimalType() {
		TypeWrapper bigDecimalTypeWrapper = TypeFactoryWrapper.INSTANCE.getBigDecimalType();
		assertEquals("big_decimal", ((Type)bigDecimalTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetCharacterType() {
		TypeWrapper characterTypeWrapper = TypeFactoryWrapper.INSTANCE.getCharacterType();
		assertEquals("character", ((Type)characterTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetClassType() {
		TypeWrapper classTypeWrapper = TypeFactoryWrapper.INSTANCE.getClassType();
		assertEquals("class", ((Type)classTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetCurrencyType() {
		TypeWrapper currencyTypeWrapper = TypeFactoryWrapper.INSTANCE.getCurrencyType();
		assertEquals("currency", ((Type)currencyTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetDateType() {
		TypeWrapper dateTypeWrapper = TypeFactoryWrapper.INSTANCE.getDateType();
		assertEquals("date", ((Type)dateTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetDoubleType() {
		TypeWrapper doubleTypeWrapper = TypeFactoryWrapper.INSTANCE.getDoubleType();
		assertEquals("double", ((Type)doubleTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetFloatType() {
		TypeWrapper floatTypeWrapper = TypeFactoryWrapper.INSTANCE.getFloatType();
		assertEquals("float", ((Type)floatTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetLocaleType() {
		TypeWrapper localeTypeWrapper = TypeFactoryWrapper.INSTANCE.getLocaleType();
		assertEquals("locale", ((Type)localeTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetLongType() {
		TypeWrapper longTypeWrapper = TypeFactoryWrapper.INSTANCE.getLongType();
		assertEquals("long", ((Type)longTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetStringType() {
		TypeWrapper stringTypeWrapper = TypeFactoryWrapper.INSTANCE.getStringType();
		assertEquals("string", ((Type)stringTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetTextType() {
		TypeWrapper textTypeWrapper = TypeFactoryWrapper.INSTANCE.getTextType();
		assertEquals("text", ((Type)textTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetTimeType() {
		TypeWrapper timeTypeWrapper = TypeFactoryWrapper.INSTANCE.getTimeType();
		assertEquals("time", ((Type)timeTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetTimestampType() {
		TypeWrapper timestampTypeWrapper = TypeFactoryWrapper.INSTANCE.getTimestampType();
		assertEquals("timestamp", ((Type)timestampTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetTimezoneType() {
		TypeWrapper timezoneTypeWrapper = TypeFactoryWrapper.INSTANCE.getTimezoneType();
		assertEquals("timezone", ((Type)timezoneTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetTrueFalseType() {
		TypeWrapper trueFalselTypeWrapper = TypeFactoryWrapper.INSTANCE.getTrueFalseType();
		assertEquals("true_false", ((Type)trueFalselTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetYesNoType() {
		TypeWrapper yesNoTypeWrapper = TypeFactoryWrapper.INSTANCE.getYesNoType();
		assertEquals("yes_no", ((Type)yesNoTypeWrapper.getWrappedObject()).getName());
	}
	
	@Test 
	public void testGetNamedType() {
		TypeWrapper typeWrapper = TypeFactoryWrapper.INSTANCE.getNamedType(String.class.getName());
		assertEquals("string", ((Type)typeWrapper.getWrappedObject()).getName());
	}
	
	@Test
	public void testGetBasicType() {
		TypeWrapper typeWrapper = TypeFactoryWrapper.INSTANCE.getBasicType(String.class.getName());
		assertEquals("string", ((Type)typeWrapper.getWrappedObject()).getName());
	}

}
