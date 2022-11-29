package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
	
	private ColumnWrapper column = null;
	
	@BeforeEach
	public void beforeEach() {
		column = new ColumnWrapper();
	}
	
	@Test
	public void testGetSqlType() {
		assertNull(column.getSqlType());
		column.setSqlType("foobar");
		assertEquals("foobar", column.getSqlType());
		column.setSqlType(null);
		Configuration cfg = new Configuration();
		cfg.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		cfg.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		column.setValue(createIntegerTypeValue());
		assertEquals("integer", column.getSqlType(cfg));
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
