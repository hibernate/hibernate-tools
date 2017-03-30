package org.hibernate.tool.ant;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AntAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.hibernate.tool.ant");
		//$JUnit-BEGIN$
		suite.addTestSuite(AntHibernateToolTest.class);		
		suite.addTestSuite(JavaFormatterTest.class);
		//$JUnit-END$
		return suite;
	}

}
