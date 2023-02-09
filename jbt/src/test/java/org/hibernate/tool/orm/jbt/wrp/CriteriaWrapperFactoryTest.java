package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Query;

public class CriteriaWrapperFactoryTest {
	
	private Query queryTarget, queryWrapper;
	
	@BeforeEach
	public void beforeEach() {
		queryTarget = createQueryTarget();
		queryWrapper = createQueryWrapper(queryTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(queryTarget);
		assertNotNull(queryWrapper);
	}
	
	@Test
	public void testSetMaxResults() {
		assertNotEquals(queryTarget.getMaxResults(), Integer.MAX_VALUE);
		queryWrapper.setMaxResults(Integer.MAX_VALUE);
		assertEquals(queryTarget.getMaxResults(), Integer.MAX_VALUE);
	}
	
	private Query createQueryTarget() {
		return (Query)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Query.class }, 
				new InvocationHandler () {
					private int maxResults = 0;
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (method.getName().equals("getMaxResults")) { return maxResults; }
						else if (method.getName().equals("setMaxResults")) { maxResults = (int)args[0]; }
						return null;
					}				
				});
	}
	
	private Query createQueryWrapper(Query target) {
		return CriteriaWrapperFactory.createCriteriaWrapper(target);
	}

}
