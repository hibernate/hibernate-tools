package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.type.IntegerType;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColumnWrapperTest {
	
	private ColumnWrapper columnWrapper = null;
	private Column wrappedColumn = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		columnWrapper = new ColumnWrapper();
		Field columnField = ColumnWrapper.class.getDeclaredField("wrappedColumn");
		columnField.setAccessible(true);
		wrappedColumn = (Column)columnField.get(columnWrapper);
	}
	
	@Test
	public void testGetName() {
		assertNull(columnWrapper.getName());
		wrappedColumn.setName("foobar");
		assertEquals("foobar", columnWrapper.getName());
	}
	
	@Test
	public void testGetSqlTypeCode() {
		assertNull(columnWrapper.getSqlTypeCode());
		wrappedColumn.setSqlTypeCode(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnWrapper.getSqlTypeCode().intValue());
	}
	
	@Test
	public void testGetSqlType() {
		assertNull(columnWrapper.getSqlType());
		wrappedColumn.setSqlType("foobar");
		assertEquals("foobar", columnWrapper.getSqlType());
		wrappedColumn.setSqlType(null);
		Configuration cfg = new Configuration();
		cfg.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		cfg.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		wrappedColumn.setValue(createIntegerTypeValue());
		assertEquals("integer", columnWrapper.getSqlType(cfg));
	}
	
	@Test
	public void testGetLength() {
		assertEquals(Integer.MIN_VALUE, columnWrapper.getLength());
		wrappedColumn.setLength(Integer.MAX_VALUE);
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
		wrappedColumn.setPrecision(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnWrapper.getPrecision());
	}
	
	private Value createIntegerTypeValue() {
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
