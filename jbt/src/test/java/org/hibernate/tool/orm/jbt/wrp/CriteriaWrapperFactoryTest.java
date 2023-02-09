package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.hibernate.tool.orm.jbt.wrp.CriteriaWrapperFactory.CriteriaExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Query;

public class CriteriaWrapperFactoryTest {
	
	private static final List<String> RESULT_LIST = Arrays.asList("foo", "bar");
	
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
	
	@Test
	public void testList() {
		assertTrue(queryWrapper instanceof CriteriaExtension);
		assertSame(((CriteriaExtension)queryWrapper).list(), RESULT_LIST);
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
						else if (method.getName().equals("getResultList")) { return RESULT_LIST; }
						return null;
					}				
				});
	}
	
	private Query createQueryWrapper(Query target) {
		return CriteriaWrapperFactory.createCriteriaWrapper(target);
	}

}
