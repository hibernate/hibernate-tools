package org.hibernate.tools.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.opentest4j.AssertionFailedError;

public class ConnectionLeakUtil {
	
	public static ConnectionLeakUtil forH2() {
		ConnectionLeakUtil result = new ConnectionLeakUtil();
		result.idleConnectionCounter = new H2IdleConnectionCounter();
		return result;
	}
	
	private IdleConnectionCounter idleConnectionCounter = null;
	
	private int connectionCount = 0;
	
	public void initialize() {
		connectionCount = idleConnectionCounter.countConnections();
	}
	
	public void assertNoLeaks() {
		int leaked = getLeakedConnectionCount();
		if (leaked != 0) {
			throw new AssertionFailedError(leaked + " connections are leaked.");
		}
	}
	
	private int getLeakedConnectionCount() {
		int previousCount = connectionCount;
		connectionCount = idleConnectionCounter.countConnections();
		return connectionCount - previousCount;
	}
	
	private static interface IdleConnectionCounter {
		int countConnections();
	}
	
	private static class H2IdleConnectionCounter implements IdleConnectionCounter {
		private Connection newConnection() {
			try {
				return DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		public int countConnections() {		
			try {
				int result = 0;
				Connection connection = newConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(
						"SELECT COUNT(*) " +
						"FROM information_schema.sessions " + 
						"WHERE executing_statement IS NULL");
				while (resultSet.next()) {
					result = resultSet.getInt(1);
				}
				statement.close();
				connection.close();
				return result;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
	}

}
