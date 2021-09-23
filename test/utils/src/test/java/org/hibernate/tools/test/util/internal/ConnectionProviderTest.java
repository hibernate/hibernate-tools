package org.hibernate.tools.test.util.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.junit.jupiter.api.Test;

public class ConnectionProviderTest {
	
	private ConnectionProvider connectionProvider = new org.hibernate.tools.test.util.internal.ConnectionProvider();
	
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
