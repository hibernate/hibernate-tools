package org.hibernate.tools.test.util;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.hibernate.tools.test.util.DbSuite.SqlScriptRoot;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.RunnerBuilder;


public class DbSuiteTest {
	
	private class DummyRunner extends Runner {
		@Override
		public Description getDescription() {
			return Description.EMPTY;
		}
		@Override
		public void run(RunNotifier notifier) {
			sqlScriptRoot = System.getProperty(DbSuite.SQL_SCRIPT_ROOT);
			notifier.fireTestStarted(getDescription());;
		}		
	}
	
	private class DummyListener extends RunListener {
		boolean isStarted = false;
		boolean isIgnored = false;
	    public void testStarted(Description description) throws Exception {
	    	isStarted = true;
	    }
	    public void testIgnored(Description description) throws Exception {
	    	isIgnored = true;
	    }
	}
	
	public class DummyTest {
		@Test
		public void testDummy() {
			Assert.assertTrue(true);
		}
	}
	
	@RunWith(DbSuite.class)
	@SuiteClasses(DummyTest.class)
	public class FirstSuite {}
	
	@RunWith(DbSuite.class)
	@SuiteClasses(DummyTest.class)
	@SqlScriptRoot("foo.bar")
	public class SecondSuite {}
	
	private class TestRunnerBuilder extends RunnerBuilder {
		@Override
		public Runner runnerForClass(Class<?> testClass) throws Throwable {
			return null;
		}		
	}
	
	private DbSuite dbSuite;
	private String sqlScriptRoot = null;
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		createHibernateProperties("sa", "", "jdbc:h2:mem:test");
		setUpClassLoader();
	}
	
	@After
	public void tearDown() throws Exception {
		restoreClassLoader();
	}
	
	@Test
	public void testDbSuiteConstruction() throws Exception {
		dbSuite = new DbSuite(FirstSuite.class, new TestRunnerBuilder());
		Assert.assertNull(dbSuite.sqlScriptRoot);
		Assert.assertFalse(dbSuite.ignore);
		dbSuite = new DbSuite(SecondSuite.class, new TestRunnerBuilder());
		Assert.assertEquals("foo.bar", dbSuite.sqlScriptRoot);
		Assert.assertFalse(dbSuite.ignore);
		new File(temporaryFolder.getRoot(), "hibernate.properties").delete();
		createHibernateProperties("foo", "bar", "jdbc:sqlserver://org.foo.bar:1433");
		dbSuite = new DbSuite(FirstSuite.class, new TestRunnerBuilder());
		Assert.assertNull(dbSuite.sqlScriptRoot);
		Assert.assertTrue(dbSuite.ignore);
		dbSuite = new DbSuite(SecondSuite.class, new TestRunnerBuilder());
		Assert.assertEquals("foo.bar", dbSuite.sqlScriptRoot);
		Assert.assertTrue(dbSuite.ignore);
	}
	
	@Test
	public void testRunChild() throws Exception {
		dbSuite = new DbSuite(FirstSuite.class, new TestRunnerBuilder());
		Runner runner = new DummyRunner();
		RunNotifier notifier = new RunNotifier();
		DummyListener listener = new DummyListener();
		notifier.addListener(listener);
		dbSuite.ignore = true; 
		listener.isStarted = false;
		listener.isIgnored = false;
		dbSuite.runChild(runner, notifier);
		Assert.assertFalse(listener.isStarted);
		Assert.assertTrue(listener.isIgnored);
		dbSuite.ignore = false;
		dbSuite.sqlScriptRoot = null;
		listener.isStarted = false;
		listener.isIgnored = false;
		dbSuite.runChild(runner, notifier);
		Assert.assertTrue(listener.isStarted);
		Assert.assertFalse(listener.isIgnored);
		Assert.assertNull(sqlScriptRoot);
		dbSuite.ignore = false;
		dbSuite.sqlScriptRoot = "foo.bar";
		listener.isStarted = false;
		listener.isIgnored = false;
		dbSuite.runChild(runner, notifier);
		Assert.assertTrue(listener.isStarted);
		Assert.assertFalse(listener.isIgnored);
		Assert.assertEquals("foo.bar", sqlScriptRoot);		
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
	
	

}
