/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.OtherCfg2HbmTest;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.util.MetadataHelper;
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
			"Customer.hbm.xml",
			"Order.hbm.xml",
			"LineItem.hbm.xml",
			"Product.hbm.xml",
			"HelloWorld.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File exporterOutputDir;
	
	@Before
	public void setUp() throws Exception {
		Metadata metadata = 
				HibernateUtil.initializeMetadata(this, HBM_XML_FILES);
		exporterOutputDir = new File(temporaryFolder.getRoot(), "exporterOutput");
		exporterOutputDir.mkdir();		
		Exporter hbmexporter = new HibernateMappingExporter();	
		hbmexporter.setMetadata(metadata);
		hbmexporter.setOutputDirectory(exporterOutputDir);
		hbmexporter.start();		
	}
	
	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(exporterOutputDir, "org/hibernate/tool/hbm2x/Customer.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(exporterOutputDir, "org/hibernate/tool/hbm2x/LineItem.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(exporterOutputDir, "org/hibernate/tool/hbm2x/Order.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(exporterOutputDir, "org/hibernate/tool/hbm2x/Product.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(exporterOutputDir, "HelloWorld.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(exporterOutputDir, "HelloUniverse.hbm.xml") );		
	}
	
	@Test
    public void testReadable() {
        Configuration cfg = new Configuration();
		cfg.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
        cfg.addFile(new File(exporterOutputDir, "org/hibernate/tool/hbm2x/Customer.hbm.xml") );
        cfg.addFile(new File(exporterOutputDir, "org/hibernate/tool/hbm2x/LineItem.hbm.xml") );
        cfg.addFile(new File(exporterOutputDir, "org/hibernate/tool/hbm2x/Order.hbm.xml") );
        cfg.addFile(new File(exporterOutputDir, "org/hibernate/tool/hbm2x/Product.hbm.xml") );              
        Assert.assertNotNull(MetadataHelper.getMetadata(cfg));      
    }
	
	@Test
	public void testNoVelocityLeftOvers() {
		Assert.assertEquals(null, FileUtil.findFirstString("$",new File(exporterOutputDir, "org/hibernate/tool/hbm2x/Customer.hbm.xml") ) );
		Assert.assertEquals(null, FileUtil.findFirstString("$",new File(exporterOutputDir, "org/hibernate/tool/hbm2x/LineItem.hbm.xml") ) );
		Assert.assertEquals(null, FileUtil.findFirstString("$",new File(exporterOutputDir, "org/hibernate/tool/hbm2x/Order.hbm.xml") ) );
		Assert.assertEquals(null, FileUtil.findFirstString("$",new File(exporterOutputDir, "org/hibernate/tool/hbm2x/Product.hbm.xml") ) );   
	}
	
	@Test
	public void testVersioning() throws DocumentException {	
    	SAXReader xmlReader = new SAXReader();
    	xmlReader.setValidation(true);
		Document document = xmlReader.read(new File(exporterOutputDir, "org/hibernate/tool/hbm2x/Product.hbm.xml"));
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/version");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one version element", 1, list.size());			
	}
	
}
