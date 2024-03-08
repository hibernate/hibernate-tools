package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.cfg.Environment;
import org.hibernate.tool.orm.jbt.api.EnvironmentWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvironmentWrapperTest {
	
	private EnvironmentWrapper environmentWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		environmentWrapper = EnvironmentWrapper.INSTANCE;
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
		assertEquals("hibernate.connection.driver_class", environmentWrapper.getDriver());
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
		assertEquals("hibernate.connection.datasource", environmentWrapper.getDataSource());
	}
	
	@Test
	public void testGetConnectionProvider() {
		assertEquals("hibernate.connection.provider_class", environmentWrapper.getConnectionProvider());
	}
	
	@Test
	public void testGetURL() {
		assertEquals("hibernate.connection.url", environmentWrapper.getURL());
	}
	
	@Test
	public void testGetUser() {
		assertEquals("hibernate.connection.username", environmentWrapper.getUser());
	}
	
	@Test
	public void testGetPass() {
		assertEquals("hibernate.connection.password", environmentWrapper.getPass());
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
