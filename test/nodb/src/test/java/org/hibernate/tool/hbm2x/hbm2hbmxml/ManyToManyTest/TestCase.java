//$Id$

/*
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the
 * basic structure is correct in each test.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml.ManyToManyTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.metadata.MetadataDescriptor;
import org.hibernate.tool.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"UserGroup.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private Exporter hbmexporter = null;
	private File outputDir = null;
	private File resourcesDir = null;
	
	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataSources(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataSources(metadataDescriptor);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		Assert.assertFalse(new File(
				outputDir,
				"GeneralHbmSettings.hbm.xml")
			.exists() );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/User.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/Group.hbm.xml") );
	}

	@Test
	public void testArtifactCollection() {
		Assert.assertEquals(
				2,
				hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
	}

	@Test
	public void testReadable() {
        ArrayList<File> files = new ArrayList<File>(4); 
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/User.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/Group.hbm.xml"));
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeSources(null, files.toArray(new File[2]), properties);
        Assert.assertNotNull(metadataDescriptor.buildMetadata());
    }

	@Test
	public void testManyToMany() throws DocumentException {
		File outputXml = new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/User.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader = new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/many-to-many");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one many-to-many element", 1, list.size());
		Element node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "entity-name" ).getText(),"org.hibernate.tool.hbm2x.hbm2hbmxml.ManyToManyTest.Group");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one set element", 1, list.size());
		node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "table" ).getText(),"UserGroup");
	}

	@Test
	public void testCompositeId() throws DocumentException {
		File outputXml = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/Group.hbm.xml");
		SAXReader xmlReader = new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one class element", 1, list.size());
		Element node = (Element) list.get(0);
		Assert.assertEquals(node.attribute("table").getText(), "`Group`");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/composite-id");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one composite-id element", 1, list.size());
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/composite-id/key-property");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get two key-property elements", 2, list.size());
		node = (Element) list.get(0);
		Assert.assertEquals(node.attribute("name").getText(), "name");
		node = (Element) list.get(1);
		Assert.assertEquals(node.attribute("name").getText(), "org");
	}

	@Test
	public void testSetAttributes() {
		File outputXml = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ManyToManyTest/Group.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader = new SAXReader();
		xmlReader.setValidation(true);
		Document document;
		try {
			document = xmlReader.read(outputXml);
			XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
			List<?> list = xpath.selectNodes(document);
			Assert.assertEquals("Expected to get one set element", 1, list.size());
			Element node = (Element) list.get(0);
			Assert.assertEquals(node.attribute("table").getText(), "UserGroup");
			Assert.assertEquals(node.attribute("name").getText(), "users");
			Assert.assertEquals(node.attribute("inverse").getText(), "true");
			Assert.assertEquals(node.attribute("lazy").getText(), "extra");
		} catch (DocumentException e) {
			Assert.fail("Can't parse file " + outputXml.getAbsolutePath());
		}
	}

}
