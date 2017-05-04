package org.hibernate.tools.test.util;

import org.hibernate.tools.test.util.DbSuite.IgnoreIfDatabaseOffline;
import org.hibernate.tools.test.util.DbSuite.SqlScriptRoot;
import org.junit.Assert;
import org.junit.Test;
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
	@IgnoreIfDatabaseOffline(true)
	public class SecondSuite {}

	@RunWith(DbSuite.class)
	@SuiteClasses(DummyTest.class)
	@IgnoreIfDatabaseOffline(false)
	public class ThirdSuite {}
	
	@RunWith(DbSuite.class)
	@SuiteClasses(DummyTest.class)
	@SqlScriptRoot("foo.bar")
	public class FourthSuite {}
	
	private class TestRunnerBuilder extends RunnerBuilder {
		@Override
		public Runner runnerForClass(Class<?> testClass) throws Throwable {
			return null;
		}		
	}
	
	private DbSuite dbSuite;
	private String sqlScriptRoot = null;
	
	@Test
	public void testDbSuiteConstruction() throws Exception {
		dbSuite = new DbSuite(FirstSuite.class, new TestRunnerBuilder());
		Assert.assertFalse(dbSuite.ignore);
		Assert.assertNull(dbSuite.sqlScriptRoot);
		dbSuite = new DbSuite(SecondSuite.class, new TestRunnerBuilder());
		Assert.assertTrue(dbSuite.ignore);
		Assert.assertNull(dbSuite.sqlScriptRoot);
		dbSuite = new DbSuite(ThirdSuite.class, new TestRunnerBuilder());
		Assert.assertFalse(dbSuite.ignore);
		Assert.assertNull(dbSuite.sqlScriptRoot);
		dbSuite = new DbSuite(FourthSuite.class, new TestRunnerBuilder());
		Assert.assertFalse(dbSuite.ignore);
		Assert.assertEquals("foo.bar", dbSuite.sqlScriptRoot);
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
	
	

}
