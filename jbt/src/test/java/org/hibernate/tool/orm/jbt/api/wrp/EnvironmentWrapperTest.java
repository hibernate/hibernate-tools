/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2023-2025 Red Hat, Inc.
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.cfg.Environment;
import org.hibernate.tool.orm.jbt.internal.factory.EnvironmentWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvironmentWrapperTest {
	
	private EnvironmentWrapper environmentWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		environmentWrapper = EnvironmentWrapperFactory.createEnvironmentWrapper();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(environmentWrapper);
	}

	@Test
	public void testGetTransactionManagerStrategy() {
		assertEquals("hibernate.transaction.coordinator_class", environmentWrapper.getTransactionManagerStrategy());
	}
	
	@Test
	public void testGetDriver() {
		assertEquals("jakarta.persistence.jdbc.driver", environmentWrapper.getDriver());
	}
	
	@Test
	public void testGetHBM2DDLAuto() {
		assertEquals("hibernate.hbm2ddl.auto", environmentWrapper.getHBM2DDLAuto());
	}
	
	@Test
	public void testGetDialect() {
		assertEquals("hibernate.dialect", environmentWrapper.getDialect());
	}
	
	@Test
	public void testGetDataSource() {
		assertEquals("jakarta.persistence.jtaDataSource", environmentWrapper.getDataSource());
	}
	
	@Test
	public void testGetConnectionProvider() {
		assertEquals("hibernate.connection.provider_class", environmentWrapper.getConnectionProvider());
	}
	
	@Test
	public void testGetURL() {
		assertEquals("jakarta.persistence.jdbc.url", environmentWrapper.getURL());
	}
	
	@Test
	public void testGetUser() {
		assertEquals("jakarta.persistence.jdbc.user", environmentWrapper.getUser());
	}
	
	@Test
	public void testGetPass() {
		assertEquals("jakarta.persistence.jdbc.password", environmentWrapper.getPass());
	}
	
	@Test
	public void testGetSessionFactoryName() {
		assertEquals("hibernate.session_factory_name", environmentWrapper.getSessionFactoryName());
	}
	
	@Test
	public void testGetDefaultCatalog() {
		assertEquals("hibernate.default_catalog", environmentWrapper.getDefaultCatalog());
	}
	
	@Test
	public void testGetDefaultSchema() {
		assertEquals("hibernate.default_schema", environmentWrapper.getDefaultSchema());
	}
	
	@Test
	public void testGetWrappedClass() {
		assertSame(Environment.class, environmentWrapper.getWrappedClass());
	}
	
}
