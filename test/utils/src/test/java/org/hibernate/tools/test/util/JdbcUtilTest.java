package org.hibernate.tools.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JdbcUtilTest {
	
	@Before
	public void setUp() throws Exception {
		clearConnectionTable();
	}
	
	@After
	public void tearDown() throws Exception {
		clearConnectionTable();
	}
	
	@Test
	public void testEstablishJdbcConnection() throws Exception {
		Assert.assertNull(JdbcUtil.CONNECTION_TABLE.get(this));
		JdbcUtil.establishJdbcConnection(this);
		Connection connection = JdbcUtil.CONNECTION_TABLE.get(this);
		Assert.assertFalse(connection.isClosed());
	}
	
	@Test
	public void testReleaseJdbcConnection() throws Exception {
		Connection connection = 
				DriverManager.getConnection("jdbc:h2:mem:test;USER=sa");
		JdbcUtil.CONNECTION_TABLE.put(this, connection);
		Assert.assertNotNull(JdbcUtil.CONNECTION_TABLE.get(this));
		Assert.assertFalse(connection.isClosed());
		JdbcUtil.releaseJdbcConnection(this);
		Assert.assertNull(JdbcUtil.CONNECTION_TABLE.get(this));
		Assert.assertTrue(connection.isClosed());
	}
	
	@Test
	public void testExecuteDDL() throws Exception {
		Connection connection = DriverManager
				.getConnection("jdbc:h2:mem:test;USER=sa");
		JdbcUtil.CONNECTION_TABLE.put(this, connection);
		ResultSet resultSet = connection
				.getMetaData()
				.getTables(null, null, "FOO", null);
		Assert.assertFalse(resultSet.next());
		String[] sqls = new String[] {
				"CREATE TABLE FOO (BAR INT)"
		};
		JdbcUtil.executeDDL(this, sqls);
		resultSet = connection
				.getMetaData()
				.getTables(null, null, "FOO", null);
		Assert.assertTrue(resultSet.next());
	}
	
	private void clearConnectionTable() throws Exception {
		for (Connection c : JdbcUtil.CONNECTION_TABLE.values()) {
			c.close();
		}
		JdbcUtil.CONNECTION_TABLE.clear();
	}
	
}
