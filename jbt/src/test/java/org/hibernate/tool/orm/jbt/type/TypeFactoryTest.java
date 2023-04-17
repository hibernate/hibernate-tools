package org.hibernate.tool.orm.jbt.type;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class TypeFactoryTest {
	
	@Test
	public void testConstruction() {
		assertNotNull(TypeFactory.INSTANCE);
	}

	@Test
	public void testGetByteType() {
		assertSame(TypeFactory.BYTE_TYPE, TypeFactory.INSTANCE.getByteType());
	}
	
	@Test
	public void testGetBigIntegerType() {
		assertSame(TypeFactory.BIG_INTEGER_TYPE, TypeFactory.INSTANCE.getBigIntegerType());
	}
	
	@Test
	public void testGetShortType() {
		assertSame(TypeFactory.SHORT_TYPE, TypeFactory.INSTANCE.getShortType());
	}
	
	@Test
	public void testGetCalendarType() {
		assertSame(TypeFactory.CALENDAR_TYPE, TypeFactory.INSTANCE.getCalendarType());
	}
	
	@Test
	public void testGetCalendarDateType() {
		assertSame(TypeFactory.CALENDAR_DATE_TYPE, TypeFactory.INSTANCE.getCalendarDateType());
	}
	
	@Test
	public void testGetIntegerType() {
		assertSame(TypeFactory.INTEGER_TYPE, TypeFactory.INSTANCE.getIntegerType());
	}
	
	@Test
	public void testGetBigDecimalType() {
		assertSame(TypeFactory.BIG_DECIMAL_TYPE, TypeFactory.INSTANCE.getBigDecimalType());
	}
	
	@Test
	public void testGetCharacterType() {
		assertSame(TypeFactory.CHARACTER_TYPE, TypeFactory.INSTANCE.getCharacterType());
	}
	
	@Test
	public void testGetClassType() {
		assertSame(TypeFactory.CLASS_TYPE, TypeFactory.INSTANCE.getClassType());
	}
	
	@Test
	public void testGetCurrencyType() {
		assertSame(TypeFactory.CURRENCY_TYPE, TypeFactory.INSTANCE.getCurrencyType());
	}
	
	@Test
	public void testGetDateType() {
		assertSame(TypeFactory.DATE_TYPE, TypeFactory.INSTANCE.getDateType());
	}
	
	@Test
	public void testGetDoubleType() {
		assertSame(TypeFactory.DOUBLE_TYPE, TypeFactory.INSTANCE.getDoubleType());
	}
	
	@Test
	public void testGetFloatType() {
		assertSame(TypeFactory.FLOAT_TYPE, TypeFactory.INSTANCE.getFloatType());
	}
	
	@Test
	public void testGetLocaleType() {
		assertSame(TypeFactory.LOCALE_TYPE, TypeFactory.INSTANCE.getLocaleType());
	}
	
	@Test
	public void testGetLongType() {
		assertSame(TypeFactory.LONG_TYPE, TypeFactory.INSTANCE.getLongType());
	}
	
	@Test
	public void testGetStringType() {
		assertSame(TypeFactory.STRING_TYPE, TypeFactory.INSTANCE.getStringType());
	}
	
	@Test
	public void testGetTextType() {
		assertSame(TypeFactory.TEXT_TYPE, TypeFactory.INSTANCE.getTextType());
	}
	
	@Test
	public void testGetTimeType() {
		assertSame(TypeFactory.TIME_TYPE, TypeFactory.INSTANCE.getTimeType());
	}
	
	@Test
	public void testGetTimestampType() {
		assertSame(TypeFactory.TIMESTAMP_TYPE, TypeFactory.INSTANCE.getTimestampType());
	}
	
	@Test
	public void testGetTimezoneType() {
		assertSame(TypeFactory.TIMEZONE_TYPE, TypeFactory.INSTANCE.getTimezoneType());
	}
	
	@Test
	public void testGetTrueFalseType() {
		assertSame(TypeFactory.TRUE_FALSE_TYPE, TypeFactory.INSTANCE.getTrueFalseType());
	}
	
	@Test
	public void testGetYesNoType() {
		assertSame(TypeFactory.YES_NO_TYPE, TypeFactory.INSTANCE.getYesNoType());
	}
	
	@Test 
	public void testGetNamedType() {
		assertSame(TypeFactory.STRING_TYPE, TypeFactory.INSTANCE.getNamedType(String.class.getName()));
	}
	
	@Test
	public void testGetBasicType() {
		assertSame(TypeFactory.STRING_TYPE, TypeFactory.INSTANCE.getBasicType(String.class.getName()));
	}
}
