package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.type.IntegerType;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColumnWrapperTest {
	
	private ColumnWrapper columnWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		columnWrapper = new ColumnWrapper(null);
	}
	
	@Test
	public void testGetSqlType() {
		assertNull(columnWrapper.getSqlType());
		columnWrapper.setSqlType("foobar");
		assertEquals("foobar", columnWrapper.getSqlType());
		columnWrapper = new ColumnWrapper(null);
		Configuration cfg = new Configuration();
		cfg.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		cfg.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		columnWrapper.setValue(createValue());
		assertEquals("integer", columnWrapper.getSqlType(cfg));
	}
	
	@Test
	public void testGetLength() {
		assertEquals(Integer.MIN_VALUE, columnWrapper.getLength());
		columnWrapper.setLength(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnWrapper.getLength());
	}
	
	@Test
	public void testGetDefaultLength() throws Exception {
		Field defaultLengthField = ColumnWrapper.class.getDeclaredField("DEFAULT_LENGTH");
		defaultLengthField.setAccessible(true);
		assertEquals(defaultLengthField.get(null), columnWrapper.getDefaultLength());
	}
	
	@Test
	public void testGetPrecision() {
		assertEquals(Integer.MIN_VALUE, columnWrapper.getPrecision());
		columnWrapper.setPrecision(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnWrapper.getPrecision());
	}
	
	@Test
	public void testGetDefaultPrecision() throws Exception {
		Field defaultPrecisionField = ColumnWrapper.class.getDeclaredField("DEFAULT_PRECISION");
		defaultPrecisionField.setAccessible(true);
		assertEquals(defaultPrecisionField.get(null), columnWrapper.getDefaultPrecision());
	}
	
	@Test
	public void testGetScale() {
		assertEquals(Integer.MIN_VALUE, columnWrapper.getScale());
		columnWrapper.setScale(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnWrapper.getScale());
	}
	
	@Test
	public void testGetDefaultScale() throws Exception {
		Field defaultScaleField = ColumnWrapper.class.getDeclaredField("DEFAULT_SCALE");
		defaultScaleField.setAccessible(true);
		assertEquals(defaultScaleField.get(null), columnWrapper.getDefaultScale());
	}
	
	private Value createValue() {
		return (Value)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Value.class }, 
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) 
							throws Throwable {
						if (method.getName().equals("getType")) {
							return IntegerType.INSTANCE;
						}
						return null;
					}
				});
	}

}
