package org.hibernate.tools.test.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.junit.jupiter.api.Test;

public class DummyConnectionProviderTest {
	
	private ConnectionProvider connectionProvider = new DummyConnectionProvider();
	
	@Test
	public void testGetConnection() {
		try {
			assertNotNull(connectionProvider.getConnection());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testCloseConnection() {
		try {
			connectionProvider.closeConnection(null);
		} catch (Exception e) {
			fail();
		}
	}

}
