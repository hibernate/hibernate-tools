package org.hibernate.tools.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT 1 AS C");
			if (resultSet.next()) {
				result = resultSet.getInt("C") == 1;
			}
			connection.commit();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// this will happen when the database is offline
		} 
		return result;
	}
	
	public static void createDatabase(Object test) {
		establishJdbcConnection(test);
		executeSql(test, getSqls(test, "create.sql", "CREATE_SQL"));
	}
	
	public static void populateDatabase(Object test) {
		executeSql(test, getSqls(test, "data.sql", "DATA_SQL"));
	}
	
	public static void dropDatabase(Object test) {
		executeSql(test, getSqls(test, "drop.sql", "DROP_SQL"));
		releaseJdbcConnection(test);
	}
	
	private static String[] getSqls(Object test, String scriptName, String fieldName) {
		File createDatabaseScript = getSqlScript(test, scriptName);
		String[] sqls = null;
		if (createDatabaseScript != null && createDatabaseScript.exists()) {
			sqls = getSqlsFromFile(createDatabaseScript);
		} else {
			sqls = getSqlsFromField(test, fieldName);
		}
		return sqls;
	}
	
	private static String[] getSqlsFromField(Object test, String fieldName) {
		String[] result = new String[] {};
		try {
			Field field = test.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			result = (String[])field.get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	private static String[] getSqlsFromFile(File file) {
		ArrayList<String> sqls = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				sqls.add(line);
			}
			bufferedReader.close();
		} catch (IOException e) {
			new RuntimeException(e);
		}
		return sqls.toArray(new String[sqls.size()]);
	}
	
	private static File getSqlScript(Object test, String name) {
		File result = null;
		String fullName = 
				getSqlScriptsLocation(test) + name;
		URL url = Thread
				.currentThread()
				.getContextClassLoader()
				.getResource(fullName);
		if (url != null) {
			result = new File(url.getFile());
		} 
		return result;
	}
	
	private static String getSqlScriptsLocation(Object test) {
		return test.getClass().getName().replace('.', '/') + '/';
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
		connection.commit();
		statement.close();		
	}

}
