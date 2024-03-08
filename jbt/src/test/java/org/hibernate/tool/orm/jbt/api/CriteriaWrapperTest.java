package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.hibernate.tool.orm.jbt.internal.factory.CriteriaWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Query;

public class CriteriaWrapperTest {
	
	private static final List<String> RESULT_LIST = Arrays.asList("foo", "bar");
	
	private CriteriaWrapper criteriaWrapper = null; 
	private Query wrappedCriteria = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		wrappedCriteria = createQuery();
		criteriaWrapper = CriteriaWrapperFactory.createCriteriaWrapper(wrappedCriteria);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedCriteria);
		assertNotNull(criteriaWrapper);
	}
	
	@Test
	public void testSetMaxResults() {
		assertNotEquals(wrappedCriteria.getMaxResults(), Integer.MAX_VALUE);
		criteriaWrapper.setMaxResults(Integer.MAX_VALUE);
		assertEquals(wrappedCriteria.getMaxResults(), Integer.MAX_VALUE);
	}
	
	private Query createQuery() {
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
	
}
