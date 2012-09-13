package org.hibernate.tool.hbm2x;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.tool.test.jdbc2cfg.identity.H2IdentityTest;
import org.hibernate.tool.test.jdbc2cfg.identity.HSQLIdentityTest;
import org.hibernate.tool.test.jdbc2cfg.identity.MySQLIdentityTest;

public class Hbm2XAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.hibernate.tool.hbm2x");
		//$JUnit-BEGIN$
		suite.addTestSuite(PluralizeTest.class);
		suite.addTestSuite(PropertiesTest.class);
		suite.addTestSuite(Hbm2CfgTest.class);
		suite.addTestSuite(Hbm2DaoTest.class);
		suite.addTestSuite(OtherCfg2HbmTest.class);
		suite.addTestSuite(Hbm2JavaConstructorTest.class);
		suite.addTestSuite(CachedMetaDataTest.class);
		suite.addTestSuite(GenerateFromJDBCTest.class);
		suite.addTestSuite(Hbm2JavaInitializationTest.class);
		suite.addTestSuite(Hbm2HibernateDAOTest.class);
		suite.addTestSuite(DefaultSchemaCatalogTest.class);
		suite.addTestSuite(HashcodeEqualsTest.class);
		suite.addTestSuite(JdbcHbm2JavaEjb3Test.class);
		//suite.addTestSuite(DocExporterTest.class);
		suite.addTestSuite(Hbm2EJBDaoTest.class);
		suite
				.addTestSuite(Hbm2JavaBidirectionalIndexedCollectionMappingTest.class);
		suite.addTestSuite(IncrementalSchemaReadingTest.class);
		suite.addTestSuite(Hbm2JavaEjb3Test.class);
		suite.addTestSuite(XMLPrettyPrinterTest.class);
		suite.addTestSuite(GenericExporterTest.class);
		suite.addTestSuite(Hbm2JavaTest.class);		
		//$JUnit-END$
		
		suite.addTestSuite(H2IdentityTest.class);
		suite.addTestSuite(MySQLIdentityTest.class);
		suite.addTestSuite(HSQLIdentityTest.class);
		return suite;
	}

}
