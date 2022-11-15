package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NamingStrategyWrapperTest {
	
	private Object namingStrategyWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		namingStrategyWrapper = NamingStrategyWrapper.create(DefaultNamingStrategy.class.getName());
	}
	
	@Test
	public void testGetClassName() throws Exception {
		Method getClassNameMethod = namingStrategyWrapper
				.getClass()
				.getDeclaredMethod("getStrategyClassName", new Class[] {});
		assertEquals(
				DefaultNamingStrategy.class.getName(),
				getClassNameMethod.invoke(namingStrategyWrapper, new Object[] {}));
	}

}
