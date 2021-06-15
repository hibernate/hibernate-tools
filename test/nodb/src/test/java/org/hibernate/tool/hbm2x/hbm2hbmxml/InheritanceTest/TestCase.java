//$Id$

/*
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the
 * basic structure is correct in each test.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml.InheritanceTest;

import java.io.File;
import java.util.ArrayList;
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
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * this test should be fixed to have a proper model. currently a mix of subclass/joinedsubclass is in play.
 * @author max
 *
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Aliens.hbm.xml"
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
				outputDir,
				"GeneralHbmSettings.hbm.xml").exists() );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Human.hbm.xml"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Alien.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Animal.hbm.xml") );
	}

	@Test
	public void testArtifactCollection() {
		Assert.assertEquals(
				3,
				hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
	}

	public void testReadable() {
        ArrayList<File> files = new ArrayList<File>(4); 
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Alien.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Human.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Animal.hbm.xml"));
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files.toArray(new File[3]), properties);
        Assert.assertNotNull(metadataDescriptor.createMetadata());
    }

	// TODO Re-enable this test: HBX-1247
	@Ignore
	@Test
	public void testComment() throws Exception {
		File outputXml = new File(
				outputDir,
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Alien.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/joined-subclass/comment")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one comment element", 1, nodeList.getLength());
    }
	
	@Test
	public void testDiscriminator() throws Exception {
		File outputXml = new File(
				outputDir, 
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/InheritanceTest/Animal.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/discriminator")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one discriminator element", 1, nodeList.getLength());	
		Element node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "type" ), "string");
	}

}
