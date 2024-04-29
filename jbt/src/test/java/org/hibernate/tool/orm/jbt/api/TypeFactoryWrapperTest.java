package org.hibernate.tool.orm.jbt.api;

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

import org.hibernate.tool.orm.jbt.internal.factory.TypeFactoryWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.TypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeFactoryWrapperTest {
	
	private TypeFactoryWrapper typeFactoryWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		typeFactoryWrapper = TypeFactoryWrapperFactory.createTypeFactoryWrapper();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(typeFactoryWrapper);
	}

	@Test
	public void testGetBooleanType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getBooleanType();
		assertEquals("boolean", typeWrapper.getName());
	}
	
	@Test
	public void testGetByteType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getByteType();
		assertEquals("byte", typeWrapper.getName());
	}
	
	@Test
	public void testGetBigIntegerType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getBigIntegerType();
		assertEquals("big_integer", typeWrapper.getName());
	}
	
	@Test
	public void testGetShortType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getShortType();
		assertEquals("short", typeWrapper.getName());
	}
	
	@Test
	public void testGetCalendarType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getCalendarType();
		assertEquals("calendar", typeWrapper.getName());
	}
	
	@Test
	public void testGetCalendarDateType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getCalendarDateType();
		assertEquals("calendar_date", typeWrapper.getName());
	}
	
	@Test
	public void testGetIntegerType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getIntegerType();
		assertEquals("integer", typeWrapper.getName());
	}
	
	@Test
	public void testGetBigDecimalType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getBigDecimalType();
		assertEquals("big_decimal", typeWrapper.getName());
	}
	
	@Test
	public void testGetCharacterType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getCharacterType();
		assertEquals("character", typeWrapper.getName());
	}
	
	@Test
	public void testGetClassType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getClassType();
		assertEquals("class", typeWrapper.getName());
	}
	
	@Test
	public void testGetCurrencyType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getCurrencyType();
		assertEquals("currency", typeWrapper.getName());
	}
	
	@Test
	public void testGetDateType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getDateType();
		assertEquals("date", typeWrapper.getName());
	}
	
	@Test
	public void testGetDoubleType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getDoubleType();
		assertEquals("double", typeWrapper.getName());
	}
	
	@Test
	public void testGetFloatType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getFloatType();
		assertEquals("float", typeWrapper.getName());
	}
	
	@Test
	public void testGetLocaleType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getLocaleType();
		assertEquals("locale", typeWrapper.getName());
	}
	
	@Test
	public void testGetLongType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getLongType();
		assertEquals("long", typeWrapper.getName());
	}
	
	@Test
	public void testGetStringType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getStringType();
		assertEquals("string", typeWrapper.getName());
	}
	
	@Test
	public void testGetTextType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getTextType();
		assertEquals("text", typeWrapper.getName());
	}
		
	@Test
	public void testGetTimeType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getTimeType();
		assertEquals("time", typeWrapper.getName());
	}
		
	@Test
	public void testGetTimestampType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getTimestampType();
		assertEquals("timestamp", typeWrapper.getName());
	}
		
	@Test
	public void testGetTimezoneType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getTimezoneType();
		assertEquals("timezone", typeWrapper.getName());
	}
		
	@Test
	public void testGetTrueFalseType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getTrueFalseType();
		assertEquals("true_false", typeWrapper.getName());
	}
		
	@Test
	public void testGetYesNoType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getYesNoType();
		assertEquals("true_false", typeWrapper.getName());
	}
		
	@Test
	public void testGetNamedType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getNamedType(String.class.getName());
		assertEquals("string", typeWrapper.getName());
	}
		
	@Test
	public void testGetBasicType() {
		TypeWrapper typeWrapper = typeFactoryWrapper.getBasicType(String.class.getName());
		assertEquals("string", typeWrapper.getName());
	}
		
	@Test
	public void testGetTypeFormats() {
		Map<TypeWrapper, String> typeFormats = typeFactoryWrapper.getTypeFormats();
		assertEquals(23, typeFormats.size());
		assertEquals("true", typeFormats.get(TypeRegistry.getType("boolean")));
		assertEquals("42", typeFormats.get(TypeRegistry.getType("byte")));
		assertEquals("42", typeFormats.get(TypeRegistry.getType("big_integer")));
		assertEquals("42", typeFormats.get(TypeRegistry.getType("short")));
		assertEquals(
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 
				typeFormats.get(TypeRegistry.getType("calendar")));
		assertEquals(
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 
				typeFormats.get(TypeRegistry.getType("calendar_date")));
		assertEquals("42", typeFormats.get(TypeRegistry.getType("integer")));
		assertEquals("42", typeFormats.get(TypeRegistry.getType("big_decimal")));
		assertEquals("h", typeFormats.get(TypeRegistry.getType("character")));
		assertEquals(
				Class.class.getName(), 
				typeFormats.get(TypeRegistry.getType("class")));
		assertEquals(
				Currency.getInstance(Locale.getDefault()).toString(), 
				typeFormats.get(TypeRegistry.getType("currency")));
		assertEquals(
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 
				typeFormats.get(TypeRegistry.getType("date")));
		assertEquals("42.42", typeFormats.get(TypeRegistry.getType("double")));
		assertEquals("42.42", typeFormats.get(TypeRegistry.getType("float")));
		assertEquals(
				Locale.getDefault().toString(), 
				typeFormats.get(TypeRegistry.getType("locale")));
		assertEquals("42", typeFormats.get(TypeRegistry.getType("long")));
		assertEquals("a string", typeFormats.get(TypeRegistry.getType("string")));
		assertEquals("a text", typeFormats.get(TypeRegistry.getType("text")));
		assertEquals(':', typeFormats.get(TypeRegistry.getType("time")).charAt(2));
		//JdbcTimestampJavaType uses timezone UTC+0 for the string format vs the system default tz for JdbcDateJavaType.
		SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		utcDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.from( ZoneOffset.UTC )));
		assertEquals(utcDateFormat.format(new Date()).substring(0, 10),
				typeFormats.get(TypeRegistry.getType("timestamp")).substring(0, 10));
		assertEquals(
				TimeZone.getDefault().getID(), 
				typeFormats.get(TypeRegistry.getType("timezone")));
		assertEquals("true", typeFormats.get(TypeRegistry.getType("true_false")));
		assertEquals("true", typeFormats.get(TypeRegistry.getType("yes_no")));
	}
}
