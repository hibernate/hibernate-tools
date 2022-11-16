package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NamingStrategyWrapperFactoryTest {
	
	private Object namingStrategyWrapperFactory = null;
	
	@BeforeEach
	public void beforeEach() {
		namingStrategyWrapperFactory = NamingStrategyWrapperFactory.create(DefaultNamingStrategy.class.getName());
	}
	
	@Test
	public void testGetClassName() throws Exception {
		Method getClassNameMethod = namingStrategyWrapperFactory
				.getClass()
				.getDeclaredMethod("getStrategyClassName", new Class[] {});
		assertEquals(
				DefaultNamingStrategy.class.getName(),
				getClassNameMethod.invoke(namingStrategyWrapperFactory, new Object[] {}));
	}

}
