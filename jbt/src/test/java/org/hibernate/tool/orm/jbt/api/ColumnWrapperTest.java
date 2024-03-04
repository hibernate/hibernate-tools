package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.internal.factory.ColumnWrapperFactory;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.type.spi.TypeConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColumnWrapperTest {

	private ColumnWrapper columnWrapper = null; 
	private Column wrappedColumn = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		columnWrapper = ColumnWrapperFactory.createColumnWrapper(null);
		wrappedColumn = (Column)columnWrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(columnWrapper);
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
		// IColumn#getSqlType()
		assertNull(columnWrapper.getSqlType());
		wrappedColumn.setSqlType("foobar");
		assertEquals("foobar", columnWrapper.getSqlType());
		// IColumn#getSqlType(IConfiguration)
		columnWrapper = ColumnWrapperFactory.createColumnWrapper(null);
		wrappedColumn = (Column)columnWrapper.getWrappedObject();
		wrappedColumn.setValue(createValue());
		Configuration configuration = new Configuration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		assertEquals("integer", columnWrapper.getSqlType(configuration));
	}
	
	@Test
	public void testGetLength() {
		assertEquals(Integer.MIN_VALUE, columnWrapper.getLength());
		wrappedColumn.setLength(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnWrapper.getLength());
	}
	
	@Test
	public void testGetDefaultLength() throws Exception {
		assertEquals(255, columnWrapper.getDefaultLength());
	}
	
	@Test
	public void testGetPrecision() {
		assertEquals(Integer.MIN_VALUE, columnWrapper.getPrecision());
		wrappedColumn.setPrecision(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnWrapper.getPrecision());
	}
	
	@Test
	public void testGetDefaultPrecision() throws Exception {
		assertEquals(19, columnWrapper.getDefaultPrecision());
	}
	
	@Test
	public void testGetScale() {
		assertEquals(Integer.MIN_VALUE, columnWrapper.getScale());
		wrappedColumn.setScale(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnWrapper.getScale());
	}
	
	@Test
	public void testGetDefaultScale() throws Exception {
		assertEquals(2, columnWrapper.getDefaultScale());
	}
	
	@Test
	public void testIsNullable() {
		wrappedColumn.setNullable(true);
		assertTrue(columnWrapper.isNullable());
		wrappedColumn.setNullable(false);
		assertFalse(columnWrapper.isNullable());
	}
	
	@Test
	public void testGetValue() {
		Value v = createValue();
		assertNull(columnWrapper.getValue());
		wrappedColumn.setValue(v);
		Value value = columnWrapper.getValue();
		assertNotNull(value);
		assertSame(value, v);
		wrappedColumn.setValue(null);
		value = columnWrapper.getValue();
		assertNull(value);
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
							return new TypeConfiguration().getBasicTypeForJavaType(Integer.class);
						}
						return null;
					}
				});
	}

}
