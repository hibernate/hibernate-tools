/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.Hbm2CfgTest;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.internal.export.cfg.HibernateConfigurationExporter;
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

	private File outputDir = null;
	private File resourcesDir = null;
	
	private HibernateConfigurationExporter cfgexporter;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		cfgexporter = new HibernateConfigurationExporter();
		cfgexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		cfgexporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		cfgexporter.start();
	}
	
	@Test
	public void testMagicPropertyHandling() {
	   HibernateConfigurationExporter exporter = new HibernateConfigurationExporter();
	   Properties properties = exporter.getProperties();
	   properties.setProperty( "hibernate.basic", "aValue" );
	   properties.setProperty( Environment.SESSION_FACTORY_NAME, "shouldNotShowUp");
	   properties.setProperty( Environment.HBM2DDL_AUTO, "false");
	   properties.setProperty( "hibernate.temp.use_jdbc_metadata_defaults", "false");	   
	   properties.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
	   exporter.getProperties().put(
			   ExporterConstants.METADATA_DESCRIPTOR, 
			   MetadataDescriptorFactory.createNativeDescriptor(null, null, properties));
	   exporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
	   exporter.start();
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
	   exporter = new HibernateConfigurationExporter();
	   properties = exporter.getProperties();
	   properties.setProperty( Environment.HBM2DDL_AUTO, "validator");   
	   properties.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
	   exporter.getProperties().put(
			   ExporterConstants.METADATA_DESCRIPTOR, 
			   MetadataDescriptorFactory.createNativeDescriptor(null, null, properties));
	   exporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
	   exporter.start();
	   Assert.assertNotNull(
			   FileUtil.findFirstString( Environment.HBM2DDL_AUTO, file ));
	   exporter = new HibernateConfigurationExporter();
	   properties = exporter.getProperties();
	   properties.setProperty( AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "org.hibernate.console.FakeTransactionManagerLookup"); // Hack for seam-gen console configurations
	   properties.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
	   exporter.getProperties().put(
			   ExporterConstants.METADATA_DESCRIPTOR, 
			   MetadataDescriptorFactory.createNativeDescriptor(null, null, properties));
	   exporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
	   exporter.start();
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
