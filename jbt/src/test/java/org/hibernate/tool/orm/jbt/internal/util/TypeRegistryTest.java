package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.tool.orm.jbt.api.TypeWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeRegistryTest {
	
	@BeforeAll
	public static void beforeAll() {
		Locale.setDefault(new Locale("nl", "BE"));
	}
	
	@BeforeEach
	public void beforeEach() {
		TypeRegistry.TYPE_REGISTRY.clear();
		TypeRegistry.TYPE_FORMATS = null;
	}
	
	@Test
	public void testGetType() {
		assertNull(TypeRegistry.TYPE_REGISTRY.get(String.class.getName()));
		TypeWrapper stringWrapper = TypeRegistry.getType(String.class.getName());
		assertNotNull(stringWrapper);
		assertSame(TypeRegistry.TYPE_REGISTRY.get(String.class.getName()), stringWrapper);
		assertNull(TypeRegistry.TYPE_REGISTRY.get(Foo.class.getName()));
		TypeWrapper fooWrapper = TypeRegistry.getType(Foo.class.getName());
		assertNull(fooWrapper);
		assertNull(TypeRegistry.TYPE_REGISTRY.get(Foo.class.getName()));
		assertNull(TypeRegistry.getType("foo"));
		assertEquals("boolean", TypeRegistry.getType("boolean").getName());
		assertEquals("byte", TypeRegistry.getType("byte").getName());
		assertEquals("big_integer", TypeRegistry.getType("big_integer").getName());
		assertEquals("short", TypeRegistry.getType("short").getName());
		assertEquals("calendar", TypeRegistry.getType("calendar").getName());
		assertEquals("calendar_date", TypeRegistry.getType("calendar_date").getName());
		assertEquals("integer", TypeRegistry.getType("integer").getName());
		assertEquals("big_decimal", TypeRegistry.getType("big_decimal").getName());
		assertEquals("character", TypeRegistry.getType("character").getName());
		assertEquals("class", TypeRegistry.getType("class").getName());
		assertEquals("currency", TypeRegistry.getType("currency").getName());
		assertEquals("date", TypeRegistry.getType("date").getName());
		assertEquals("double", TypeRegistry.getType("double").getName());
		assertEquals("float", TypeRegistry.getType("float").getName());
		assertEquals("locale", TypeRegistry.getType("locale").getName());
		assertEquals("long", TypeRegistry.getType("long").getName());
		assertEquals("string", TypeRegistry.getType("string").getName());
		assertEquals("text", TypeRegistry.getType("text").getName());
		assertEquals("time", TypeRegistry.getType("time").getName());
		assertEquals("timestamp", TypeRegistry.getType("timestamp").getName());
		assertEquals("timezone", TypeRegistry.getType("timezone").getName());
		assertEquals("true_false", TypeRegistry.getType("true_false").getName());
		assertEquals("yes_no", TypeRegistry.getType("yes_no").getName());
	}
	
	@Test
	public void testGetTypeFormats() {
		assertTrue(true);
		Map<TypeWrapper, String> typeFormats = TypeRegistry.getTypeFormats();
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

	private class Foo {}

}
