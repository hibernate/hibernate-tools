package org.hibernate.tools.test.util;

import org.hibernate.tools.test.util.DbSuite.IgnoreIfDatabaseOffline;
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
	
	private static class DummyTest {
		@Test
		public void testDummy() {
			Assert.assertTrue(true);
		}
	}
	
	@RunWith(DbSuite.class)
	@SuiteClasses(DummyTest.class)
	public static class FirstSuite {}
	
	@RunWith(DbSuite.class)
	@SuiteClasses(DummyTest.class)
	@IgnoreIfDatabaseOffline(true)
	public static class SecondSuite {}

	@RunWith(DbSuite.class)
	@SuiteClasses(DummyTest.class)
	@IgnoreIfDatabaseOffline(false)
	public static class ThirdSuite {}
	
	private static class TestRunnerBuilder extends RunnerBuilder {
		@Override
		public Runner runnerForClass(Class<?> testClass) throws Throwable {			// TODO Auto-generated method stub
			return null;
		}		
	}
	
	private DbSuite dbSuite;
	
	@Test
	public void testDbSuiteConstruction() throws Exception {
		dbSuite = new DbSuite(FirstSuite.class, new TestRunnerBuilder());
		Assert.assertFalse(dbSuite.ignore);
		dbSuite = new DbSuite(SecondSuite.class, new TestRunnerBuilder());
		Assert.assertTrue(dbSuite.ignore);
		dbSuite = new DbSuite(ThirdSuite.class, new TestRunnerBuilder());
		Assert.assertFalse(dbSuite.ignore);
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
		listener.isStarted = false;
		listener.isIgnored = false;
		dbSuite.runChild(runner, notifier);
		Assert.assertTrue(listener.isStarted);
		Assert.assertFalse(listener.isIgnored);
	}
	
	

}
