//$Id$

/* 
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the 
 * basic structure is correct in each test.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml.OneToOneTest;

import java.io.File;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"PersonAddressOneToOnePrimaryKey.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private File resourcesDir = null;
	private Exporter hbmexporter = null;
	
	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataDescriptor(metadataDescriptor);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.start();		
	}
	
	@Test
	public void testAllFilesExistence() {
		Assert.assertFalse(new File(
				outputDir, "GeneralHbmSettings.hbm.xml")
			.exists());
		JUnitUtil.assertIsNonEmptyFile(
				new File(
						outputDir,
						"org/hibernate/tool/hbm2x/hbm2hbmxml/OneToOneTest/Person.hbm.xml"));
		JUnitUtil.assertIsNonEmptyFile(
				new File(
						outputDir, 
						"/org/hibernate/tool/hbm2x/hbm2hbmxml/OneToOneTest/Address.hbm.xml"));		
	}
	
	@Test
	public void testArtifactCollection() {
		Assert.assertEquals(
				2,
				hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
	}
	
	@Test
	public void testReadable() {
        File personHbmXml = new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/OneToOneTest/Person.hbm.xml");
        File addressHbmXml = new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/OneToOneTest/Address.hbm.xml");
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		File[] files = new File[] { personHbmXml, addressHbmXml };
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files, properties);
        Assert.assertNotNull(metadataDescriptor.createMetadata());
    }
	
	public void testOneToOne() throws Exception {
		File xmlFile = new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/OneToOneTest/Person.hbm.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(xmlFile);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/one-to-one")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one-to-one element", 1, nodeList.getLength());
		Element node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "name" ),"address");
		Assert.assertEquals(node.getAttribute( "constrained" ),"false");
		xmlFile = new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/OneToOneTest/Address.hbm.xml");
		document = db.parse(xmlFile);
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/one-to-one")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one set element", 1, nodeList.getLength());
		node = (Element)nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "name" ),"person");
		Assert.assertEquals(node.getAttribute( "constrained" ),"true");
		Assert.assertEquals(node.getAttribute( "access" ), "field");
	}

}
