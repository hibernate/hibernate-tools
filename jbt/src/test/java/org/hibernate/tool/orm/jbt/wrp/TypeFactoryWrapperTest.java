package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;
import org.hibernate.type.Type;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TypeFactoryWrapperTest {
	
	private static Locale SAVED_LOCALE = null;
	
	@BeforeAll
	public static void beforeAll() {
		SAVED_LOCALE = Locale.getDefault();
		Locale.setDefault(new Locale("nl", "BE"));
	}
	
	@AfterAll
	public static void afterAll() {
		Locale.setDefault(SAVED_LOCALE);
	}
	
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

	@Test
	public void testGetTypeFormats() {
		Map<TypeWrapper, String> typeFormats = TypeFactoryWrapper.INSTANCE.getTypeFormats();
		assertEquals(23, typeFormats.size());
		assertEquals("true", typeFormats.get(TypeFactoryWrapper.INSTANCE.getBooleanType()));
		assertEquals("42", typeFormats.get(TypeFactoryWrapper.INSTANCE.getByteType()));
		assertEquals("42", typeFormats.get(TypeFactoryWrapper.INSTANCE.getBigIntegerType()));
		assertEquals("42", typeFormats.get(TypeFactoryWrapper.INSTANCE.getShortType()));
		assertEquals(
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 
				typeFormats.get(TypeFactoryWrapper.INSTANCE.getCalendarType()));
		assertEquals(
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 
				typeFormats.get(TypeFactoryWrapper.INSTANCE.getCalendarDateType()));
		assertEquals("42", typeFormats.get(TypeFactoryWrapper.INSTANCE.getIntegerType()));
		assertEquals("42", typeFormats.get(TypeFactoryWrapper.INSTANCE.getBigDecimalType()));
		assertEquals("h", typeFormats.get(TypeFactoryWrapper.INSTANCE.getCharacterType()));
		assertEquals(
				Class.class.getName(), 
				typeFormats.get(TypeFactoryWrapper.INSTANCE.getClassType()));
		assertEquals(
				Currency.getInstance(Locale.getDefault()).toString(), 
				typeFormats.get(TypeFactoryWrapper.INSTANCE.getCurrencyType()));
		assertEquals(
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 
				typeFormats.get(TypeFactoryWrapper.INSTANCE.getDateType()));
		assertEquals("42.42", typeFormats.get(TypeFactoryWrapper.INSTANCE.getDoubleType()));
		assertEquals("42.42", typeFormats.get(TypeFactoryWrapper.INSTANCE.getFloatType()));
		assertEquals(
				Locale.getDefault().toString(), 
				typeFormats.get(TypeFactoryWrapper.INSTANCE.getLocaleType()));
		assertEquals("42", typeFormats.get(TypeFactoryWrapper.INSTANCE.getLongType()));
		assertEquals("a string", typeFormats.get(TypeFactoryWrapper.INSTANCE.getStringType()));
		assertEquals("a text", typeFormats.get(TypeFactoryWrapper.INSTANCE.getTextType()));
		assertEquals(':', typeFormats.get(TypeFactoryWrapper.INSTANCE.getTimeType()).charAt(2));
    //JdbcTimestampJavaType uses timezone UTC+0 for the string format vs the system default tz for JdbcDateJavaType.
    SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    utcDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.from( ZoneOffset.UTC )));
		assertEquals(utcDateFormat.format(new Date()).substring(0, 10),
				typeFormats.get(TypeFactoryWrapper.INSTANCE.getTimestampType()).substring(0, 10));
		assertEquals(
				TimeZone.getDefault().getID(), 
				typeFormats.get(TypeFactoryWrapper.INSTANCE.getTimezoneType()));
		assertEquals("true", typeFormats.get(TypeFactoryWrapper.INSTANCE.getTrueFalseType()));
		assertEquals("true", typeFormats.get(TypeFactoryWrapper.INSTANCE.getYesNoType()));
	}
}
