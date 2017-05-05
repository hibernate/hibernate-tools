package org.hibernate.tools.test.util;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JdbcUtilTest {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		clearConnectionTable();
		createHibernateProperties("sa", "", "jdbc:h2:mem:test");
		setUpClassLoader();
	}
	
	@After
	public void tearDown() throws Exception {
		clearConnectionTable();
		restoreClassLoader();
	}
	
	@Test
	public void testGetConnectionProperties() throws Exception {
		Properties properties = JdbcUtil.getConnectionProperties();
		Assert.assertEquals("jdbc:h2:mem:test", properties.get("url"));
		Assert.assertEquals("sa", properties.get("user"));
		Assert.assertEquals("", properties.get("password"));
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
		JdbcUtil.executeSql(this, sqls);
		resultSet = connection
				.getMetaData()
				.getTables(null, null, "FOO", null);
		Assert.assertTrue(resultSet.next());
	}
	
	@Test
	public void testToIdentifier() throws Exception {
		MetaDataInvocationHandler metaDataInvocationHandler = new MetaDataInvocationHandler();
		ConnectionInvocationHandler connectionInvocationHandler = new ConnectionInvocationHandler();
		connectionInvocationHandler.metaDataInvocationHandler = metaDataInvocationHandler;
		Connection connection = (Connection)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Connection.class }, 
				connectionInvocationHandler );
		JdbcUtil.CONNECTION_TABLE.put(this, connection);
		metaDataInvocationHandler.whichCase = "MIXED";
		Assert.assertEquals("Foo", JdbcUtil.toIdentifier(this, "Foo"));
		metaDataInvocationHandler.whichCase = "UPPER";
		Assert.assertEquals("FOO", JdbcUtil.toIdentifier(this, "Foo"));
		metaDataInvocationHandler.whichCase = "LOWER";
		Assert.assertEquals("foo", JdbcUtil.toIdentifier(this, "Foo"));
	}
	
	@Test
	public void testIsDatabaseOnline() throws Exception {
			Assert.assertTrue(JdbcUtil.isDatabaseOnline());
			new File(temporaryFolder.getRoot(), "hibernate.properties").delete();
			createHibernateProperties("foo", "bar", "jdbc:sqlserver://org.foo.bar:1433");
			Assert.assertFalse(JdbcUtil.isDatabaseOnline());
	}
	
	private void clearConnectionTable() throws Exception {
		for (Connection c : JdbcUtil.CONNECTION_TABLE.values()) {
			c.close();
		}
		JdbcUtil.CONNECTION_TABLE.clear();
	}
	
	private void createHibernateProperties(
			String user, 
			String password, 
			String url) 
					throws Exception {
		Properties properties = new Properties();
		properties.put("hibernate.connection.username", user);
		properties.put("hibernate.connection.password", password);
		properties.put("hibernate.connection.url", url);
		File outputFolder = temporaryFolder.getRoot(); 
		File propertiesFile = new File(outputFolder, "hibernate.properties");
		FileWriter writer = new FileWriter(propertiesFile);
		properties.store(writer, null);
		writer.close();
	}
	
	private void setUpClassLoader() throws Exception {
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				new URLClassLoader(
					new URL[] { temporaryFolder.getRoot().toURI().toURL() }, 
					currentClassLoader));
	}
	
	private void restoreClassLoader() {
		URLClassLoader currentClassLoader = 
				(URLClassLoader)Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(currentClassLoader.getParent());
	}
	
	private static class MetaDataInvocationHandler implements InvocationHandler {
		String whichCase;
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if ("storesLowerCaseIdentifiers".equals(methodName)) {
				return "LOWER".equals(whichCase);
			} else if ("storesUpperCaseIdentifiers".equals(methodName)) {
				return "UPPER".equals(whichCase);
			} else {
				return false;
			}
		}	
	}
	
	private static class ConnectionInvocationHandler implements InvocationHandler {
		InvocationHandler metaDataInvocationHandler;
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getMetaData")) {
				return (DatabaseMetaData)Proxy.newProxyInstance(
						getClass().getClassLoader(), 
						new Class[] { DatabaseMetaData.class } , 
						metaDataInvocationHandler);
			} else {
				return null;
			}
		}	
	}
	
}
