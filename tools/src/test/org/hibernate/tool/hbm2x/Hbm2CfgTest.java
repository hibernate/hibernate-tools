/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.NonReflectiveTestCase;

/**
 * @author max
 *
 */
public class Hbm2CfgTest extends NonReflectiveTestCase {

	private HibernateConfigurationExporter cfgexporter;

	public Hbm2CfgTest(String name) {
		super( name, "cfg2cfgxmloutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		cfgexporter = new HibernateConfigurationExporter(getCfg(), getOutputDir() );
		cfgexporter.start();
	}
	

	public void testMagicPropertyHandling() {
	   Configuration srcCfg = new Configuration();
	   
	   srcCfg.setProperty( "hibernate.basic", "aValue" );
	   srcCfg.setProperty( Environment.SESSION_FACTORY_NAME, "shouldNotShowUp");
	   srcCfg.setProperty( Environment.HBM2DDL_AUTO, "false");
	   srcCfg.setProperty( "hibernate.temp.use_jdbc_metadata_defaults", "false");
	   
	   new HibernateConfigurationExporter(srcCfg, getOutputDir()).start();
	   
	   File file = new File(getOutputDir(), "hibernate.cfg.xml");
	   assertNull(findFirstString( Environment.SESSION_FACTORY_NAME, file ));
	   assertNotNull(findFirstString( "hibernate.basic\">aValue<", file ));
	   assertNull(findFirstString( Environment.HBM2DDL_AUTO, file ));
	   assertNull(findFirstString( "hibernate.temp.use_jdbc_metadata_defaults", file ));

	   srcCfg = new Configuration();
	   
	   srcCfg.setProperty( Environment.HBM2DDL_AUTO, "validator");
	   
	   new HibernateConfigurationExporter(srcCfg, getOutputDir()).start();
	   
	   assertNotNull(findFirstString( Environment.HBM2DDL_AUTO, file ));
	   

	   srcCfg = new Configuration();
	   srcCfg.setProperty( Environment.TRANSACTION_MANAGER_STRATEGY, "org.hibernate.console.FakeTransactionManagerLookup"); // Hack for seam-gen console configurations
	   HibernateConfigurationExporter exp = new HibernateConfigurationExporter(srcCfg, getOutputDir());
	   exp.start();
	   
	   assertNull(findFirstString( Environment.TRANSACTION_MANAGER_STRATEGY, file ));
	   
	 
	}
	
	public void testFileExistence() {
		
		assertFileAndExists(new File(getOutputDir(), "hibernate.cfg.xml") );
		
	}

	public void testArtifactCollection() {
		assertEquals(1,cfgexporter.getArtifactCollector().getFileCount("cfg.xml"));
	}
	
	public void testNoVelocityLeftOvers() {
		
        assertEquals(null,findFirstString("$",new File(getOutputDir(), "hibernate.cfg.xml") ) );
        
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/";
	}
	
	protected String[] getMappings() {
		return new String[] { 
				"Customer.hbm.xml",
				"Order.hbm.xml",
				"LineItem.hbm.xml",
				"Product.hbm.xml",
				"HelloWorld.hbm.xml"
		};
	}
	
}
