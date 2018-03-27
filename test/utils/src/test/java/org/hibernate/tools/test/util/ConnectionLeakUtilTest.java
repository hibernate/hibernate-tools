package org.hibernate.tools.test.util;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConnectionLeakUtilTest {
	
	private ConnectionLeakUtil connectionLeakUtil = null;
	
	@Before
	public void before() {
		connectionLeakUtil = ConnectionLeakUtil.forH2();
		connectionLeakUtil.initialize();
	}
	
	@Test
	public void testLeaks() throws Exception {
		try {
			DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
			connectionLeakUtil.assertNoLeaks();
			throw new RuntimeException("should not happen");
		} catch (AssertionError e) {
			Assert.assertEquals("1 connections are leaked.", e.getMessage());
		}
	}
	
	@Test
	public void testNoLeaks() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
		connection.close();
		connectionLeakUtil.assertNoLeaks();
	}

}
