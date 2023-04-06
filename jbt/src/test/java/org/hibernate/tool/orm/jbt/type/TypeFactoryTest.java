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
	public void testGetBooleanType() {
		assertSame(BooleanType.INSTANCE, TypeFactory.INSTANCE.getBooleanType());
	}
	
	@Test
	public void testGetByteType() {
		assertSame(ByteType.INSTANCE, TypeFactory.INSTANCE.getByteType());
	}
	
	@Test
	public void testGetBigIntegerType() {
		assertSame(BigIntegerType.INSTANCE, TypeFactory.INSTANCE.getBigIntegerType());
	}
	
	@Test
	public void testGetShortType() {
		assertSame(ShortType.INSTANCE, TypeFactory.INSTANCE.getShortType());
	}
	
	@Test
	public void testGetCalendarType() {
		assertSame(CalendarType.INSTANCE, TypeFactory.INSTANCE.getCalendarType());
	}
	
	@Test
	public void testGetCalendarDateType() {
		assertSame(CalendarDateType.INSTANCE, TypeFactory.INSTANCE.getCalendarDateType());
	}
	
	@Test
	public void testGetIntegerType() {
		assertSame(IntegerType.INSTANCE, TypeFactory.INSTANCE.getIntegerType());
	}
	
	@Test
	public void testGetBigDecimalType() {
		assertSame( BigDecimalType.INSTANCE, TypeFactory.INSTANCE.getBigDecimalType());
	}
	
	@Test
	public void testGetCharacterType() {
		assertSame(CharacterType.INSTANCE, TypeFactory.INSTANCE.getCharacterType());
	}
	
	@Test
	public void testGetClassType() {
		assertSame(ClassType.INSTANCE, TypeFactory.INSTANCE.getClassType());
	}
	
	@Test
	public void testGetCurrencyType() {
		assertSame(CurrencyType.INSTANCE, TypeFactory.INSTANCE.getCurrencyType());
	}
	
}
