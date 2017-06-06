/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.Hbm2CfgTest;

import java.io.File;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"HelloWorld.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File outputDir;
	
	private HibernateConfigurationExporter cfgexporter;

	@Before
	public void setUp() throws Exception {
		Configuration configuration = 
				HibernateUtil.initializeConfiguration(this, HBM_XML_FILES);
		outputDir = temporaryFolder.getRoot();
		cfgexporter = new HibernateConfigurationExporter(
				configuration, outputDir);
		cfgexporter.start();
	}
	
	@Test
	public void testMagicPropertyHandling() {
	   Configuration srcCfg = new Configuration();	   
	   srcCfg.setProperty( "hibernate.basic", "aValue" );
	   srcCfg.setProperty( Environment.SESSION_FACTORY_NAME, "shouldNotShowUp");
	   srcCfg.setProperty( Environment.HBM2DDL_AUTO, "false");
	   srcCfg.setProperty( "hibernate.temp.use_jdbc_metadata_defaults", "false");	   
	   srcCfg.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
	   new HibernateConfigurationExporter(srcCfg, outputDir).start();   
	   File file = new File(outputDir, "hibernate.cfg.xml");
	   Assert.assertNull(
			   FileUtil.findFirstString(
					   Environment.SESSION_FACTORY_NAME, file ));
	   Assert.assertNotNull(
			   FileUtil.findFirstString( "hibernate.basic\">aValue<", file ));
	   Assert.assertNull(
			   FileUtil.findFirstString( Environment.HBM2DDL_AUTO, file ));
	   Assert.assertNull(
			   FileUtil.findFirstString("hibernate.temp.use_jdbc_metadata_defaults", file ));
	   srcCfg = new Configuration(); 
	   srcCfg.setProperty( Environment.HBM2DDL_AUTO, "validator");   
	   srcCfg.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
	   new HibernateConfigurationExporter(srcCfg, outputDir).start();
	   Assert.assertNotNull(
			   FileUtil.findFirstString( Environment.HBM2DDL_AUTO, file ));
	   srcCfg = new Configuration();
	   srcCfg.setProperty( AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "org.hibernate.console.FakeTransactionManagerLookup"); // Hack for seam-gen console configurations
	   srcCfg.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
	   HibernateConfigurationExporter exp = new HibernateConfigurationExporter(srcCfg, outputDir);
	   exp.start();
	   Assert.assertNull(
			   FileUtil.findFirstString( AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, file ));
	}
	
	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "hibernate.cfg.xml") );		
	}

	@Test
	public void testArtifactCollection() {
		Assert.assertEquals(1, cfgexporter.getArtifactCollector().getFileCount("cfg.xml"));
	}
	
	@Test
	public void testNoVelocityLeftOvers() {
        Assert.assertNull(FileUtil.findFirstString("${",new File(outputDir, "hibernate.cfg.xml")));
	}

}
