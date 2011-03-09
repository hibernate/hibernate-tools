package org.hibernate.tool.hbmlint;

import junit.framework.Test;
import junit.framework.TestSuite;

public class HbmLintAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.hibernate.tool.hbmlint");
		//$JUnit-BEGIN$
		suite.addTestSuite(SchemaAnalyzerTest.class);
		suite.addTestSuite(HbmLintTest.class);
		//$JUnit-END$
		return suite;
	}

}
