package org.hibernate.tools.test.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class JdbcUtil {
	
	static HashMap<Object, Connection> CONNECTION_TABLE = new HashMap<>();
	
	public static Properties getConnectionProperties() {
		Properties properties = new Properties();
		InputStream inputStream = Thread
				.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("hibernate.properties");
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Properties connectionProperties = new Properties();
		connectionProperties.put(
				"url", 
				properties.getProperty("hibernate.connection.url"));
		connectionProperties.put(
				"user", 
				properties.getProperty("hibernate.connection.username"));
		connectionProperties.put(
				"password", 
				properties.getProperty("hibernate.connection.password"));
		return connectionProperties;
	}
	
	public static void establishJdbcConnection(Object test) {
		try {
			CONNECTION_TABLE.put(test, createJdbcConnection());
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
	
	public static void executeSql(Object test, String[] sqls) {
		try {
			executeSql(CONNECTION_TABLE.get(test), sqls);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String toIdentifier(Object test, String string) {
		Connection connection = CONNECTION_TABLE.get(test);
		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			if (databaseMetaData.storesLowerCaseIdentifiers()) {
				return string.toLowerCase();
			} else if (databaseMetaData.storesUpperCaseIdentifiers()) {
				return string.toUpperCase();
			} else {
				return string;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean isDatabaseOnline() {
		boolean result = false;
		try {
			Connection connection = createJdbcConnection();
			result = connection.isValid(1);
			connection.commit();
			connection.close();
		} catch (SQLException e) {
			// this will happen when the connection cannot be created
		} 
		return result;
	}
	
	public static void createDatabase(Object test) {
		establishJdbcConnection(test);
		executeSql(test, getSqls(test, "create.sql"));
	}
	
	public static void populateDatabase(Object test) {
		executeSql(test, getSqls(test, "data.sql"));
	}
	
	public static void dropDatabase(Object test) {
		executeSql(test, getSqls(test, "drop.sql"));
		releaseJdbcConnection(test);
	}
	
	private static String[] getSqls(Object test, String scriptName) {
		String[] result =  new String[] {};
		InputStream inputStream = ResourceUtil.resolveResourceLocation(test.getClass(), scriptName);
		if (inputStream != null) {
			BufferedReader bufferedReader = 
					new BufferedReader(new InputStreamReader(inputStream));
			try {
				String line = null;
				ArrayList<String> lines = new ArrayList<String>();
				while ((line = bufferedReader.readLine()) != null) {
					lines.add(line);
				}
				result = lines.toArray(new String[lines.size()]);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
	private static Connection createJdbcConnection() 
			throws SQLException {
		Properties connectionProperties = getConnectionProperties();
		String connectionUrl = (String)connectionProperties.remove("url");
		return DriverManager
				.getDriver(connectionUrl)
				.connect(connectionUrl, connectionProperties);	
	}
	
	private static void executeSql(Connection connection, String[] sqls) 
			throws SQLException {
		Statement statement = connection.createStatement();
		for (int i = 0; i < sqls.length; i++) {
			statement.execute(sqls[i]);
		}
		if (!connection.getAutoCommit()) {
			connection.commit();
		}
		statement.close();		
	}

}
