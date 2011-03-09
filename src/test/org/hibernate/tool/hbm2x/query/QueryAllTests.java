package org.hibernate.tool.hbm2x.query;

import junit.framework.Test;
import junit.framework.TestSuite;

public class QueryAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.hibernate.tool.hbm2x.query");
		//$JUnit-BEGIN$
		suite.addTestSuite(QueryExporterTest.class);
		//$JUnit-END$
		return suite;
	}

}
