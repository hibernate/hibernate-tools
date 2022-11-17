package org.hibernate.tool.orm.jbt.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class NativeConfigurationTest {
	
	@Test
	public void testSetEntityResolver() throws Exception {
		NativeConfiguration nativeConfiguration = new NativeConfiguration();
		Field field = NativeConfiguration.class.getDeclaredField("entityResolver");
		field.setAccessible(true);
		assertNull(field.get(nativeConfiguration));
		EntityResolver entityResolver = new DefaultHandler();
		nativeConfiguration.setEntityResolver(entityResolver);
		assertNotNull(field.get(nativeConfiguration));
		assertSame(field.get(nativeConfiguration), entityResolver);
	}

}
