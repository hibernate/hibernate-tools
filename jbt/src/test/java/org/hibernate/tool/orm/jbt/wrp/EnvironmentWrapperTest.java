package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
	
}
