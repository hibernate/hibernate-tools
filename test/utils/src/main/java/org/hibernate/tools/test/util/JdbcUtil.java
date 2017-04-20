package org.hibernate.tools.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

public class JdbcUtil {
	
	static HashMap<Object, Connection> CONNECTION_TABLE = new HashMap<>();
	
	public static void establishJdbcConnection(Object test) {
		Properties properties = new Properties();
		InputStream inputStream = test
				.getClass()
				.getClassLoader()
				.getResourceAsStream("hibernate.properties");
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String connectionUrl = properties.getProperty("hibernate.connection.url");
		Properties connectionProperties = new Properties();
		connectionProperties.put(
				"user", 
				properties.getProperty("hibernate.connection.username"));
		connectionProperties.put(
				"password", 
				properties.getProperty("hibernate.connection.password"));
		try {
			Connection connection = DriverManager
					.getDriver(connectionUrl)
					.connect(connectionUrl, connectionProperties);
			CONNECTION_TABLE.put(test, connection);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void releaseJdbcConnection(Object test) {
		Connection connection = CONNECTION_TABLE.get(test);
		CONNECTION_TABLE.remove(test);
		try {
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void executeDDL(Object test, String[] sqls) {
		Connection connection = CONNECTION_TABLE.get(test);
		try {
			Statement statement = connection.createStatement();
			for (int i = 0; i < sqls.length; i++) {
				statement.execute(sqls[i]);
			}
			connection.commit();
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
