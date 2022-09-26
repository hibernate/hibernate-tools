package org.hibernate.tool.orm.jbt.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.sql.Connection;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.junit.jupiter.api.Test;

public class MockConnectionProviderTest {
	
	@Test
	public void testGetConnection() throws Exception {
		assertNotNull(MockConnectionProvider.CONNECTION);
		assertNotNull(MockConnectionProvider.DATABASE_META_DATA);
		ConnectionProvider connectionProvider = new MockConnectionProvider();
		Connection connection = connectionProvider.getConnection();
		assertSame(MockConnectionProvider.CONNECTION, connection);
		assertSame(MockConnectionProvider.DATABASE_META_DATA, connection.getMetaData());
	}

}
