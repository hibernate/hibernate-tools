package org.hibernate.tool.hbm2x;

import org.hibernate.tool.test.jdbc2cfg.identity.H2IdentityTest;
import org.hibernate.tool.test.jdbc2cfg.identity.HSQLIdentityTest;
import org.hibernate.tool.test.jdbc2cfg.identity.MySQLIdentityTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Hbm2XAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.hibernate.tool.hbm2x");
		//$JUnit-BEGIN$
		suite.addTestSuite(Hbm2JavaConstructorTest.class);
		suite.addTestSuite(Hbm2JavaInitializationTest.class);
		suite.addTestSuite(Hbm2HibernateDAOTest.class);
		suite.addTestSuite(DefaultSchemaCatalogTest.class);
		suite.addTestSuite(HashcodeEqualsTest.class);
		//suite.addTestSuite(DocExporterTest.class);
		suite.addTestSuite(Hbm2EJBDaoTest.class);
		suite
				.addTestSuite(Hbm2JavaBidirectionalIndexedCollectionMappingTest.class);
		//$JUnit-END$
		
		suite.addTestSuite(H2IdentityTest.class);
		suite.addTestSuite(MySQLIdentityTest.class);
		suite.addTestSuite(HSQLIdentityTest.class);
		return suite;
	}

}
