package org.hibernate.tool.ide.completion;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CompletionAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.hibernate.tool.ide.completion");
		//$JUnit-BEGIN$
		suite.addTestSuite(CompletionHelperTest.class);
		suite.addTestSuite(HqlAnalyzerTest.class);
		suite.addTestSuite(ModelCompletionTest.class);
		//$JUnit-END$
		return suite;
	}

}
