/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
	
	@Test
	public void testList() {
		assertSame(criteriaWrapper.list(), RESULT_LIST);
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
