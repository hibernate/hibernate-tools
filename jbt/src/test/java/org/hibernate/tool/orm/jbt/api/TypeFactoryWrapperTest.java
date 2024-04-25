package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.orm.jbt.internal.factory.TypeFactoryWrapperFactory;
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
	
}
