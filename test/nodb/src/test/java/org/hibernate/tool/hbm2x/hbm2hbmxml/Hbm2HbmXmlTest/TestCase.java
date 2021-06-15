//$Id$

/* 
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the 
 * basic structure is correct in each test.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Initial implentation based on the Hbm2XTest class.
 * 
 * @author David Channon
 * @author koen
 */
public class TestCase {

	/**
	 * Testing class for cfg2hbm generating hbms.
	 * Simulate a custom persister. 
	 * Note: Only needs to exist not work or be valid
	 *       in any other way.
	 * 
	 * @author David Channon
	 */
	public static class Persister {
		// Empty
	}

	private static final String[] HBM_XML_FILES = new String[] {
			"Basic.hbm.xml",
			"BasicCompositeId.hbm.xml",
			"BasicGlobals.hbm.xml",
			"ClassFullAttribute.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private MetadataDescriptor metadataDescriptor = null;
	private File outputDir = null;
	private File resourcesDir = null;
	private Exporter hbmexporter = null;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataDescriptor(metadataDescriptor);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.start();
	}
	
	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml") );
		Assert.assertFalse(new File(
				outputDir, 
				"org/hibernate/tool/cfg2hbm/GeneralHbmSettings.hbm.xml").exists() );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicCompositeId.hbm.xml") );
	}
	
	@Test
	public void testArtifactCollection() {
		Assert.assertEquals(
				"4 mappings + 1 global",
				5,
				hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
	}
	
	/**
	 * Special test for external Global settings were generated.
	 * Schema and Catalog settings should appear.
	 */
	@Test
	public void testGlobalSettingsGeneratedDatabase() throws Exception {
		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest");
		hgs.setSchemaName("myschema");
		hgs.setCatalogName("mycatalog");		
		Exporter gsExporter = new HibernateMappingExporter();
		gsExporter.setMetadataDescriptor(metadataDescriptor);
		gsExporter.setOutputDirectory(outputDir);
		( (HibernateMappingExporter)gsExporter).setGlobalSettings(hgs);
		gsExporter.start();
		File outputXml = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		Element root = document.getDocumentElement();
		// There are 7 attributes because there are defaults defined by the DTD makes up the missing entries
		Assert.assertEquals("Unexpected number of hibernate-mapping elements ", 7, root.getAttributes().getLength() );
		Assert.assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.getAttribute("package") );
		Assert.assertEquals("Unexpected schema name", "myschema", root.getAttribute("schema") );
		Assert.assertEquals("Unexpected mycatalog name", "mycatalog", root.getAttribute("catalog"));
	}

	/**
	 * Special test for external Global settings were generated.
	 * Non-defaults should appear for Cascade and Access.
	 */
	@Test
	public void testGlobalSettingsGeneratedAccessAndCascadeNonDefault()  throws Exception {
		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest");
		hgs.setDefaultAccess("field");
		hgs.setDefaultCascade("save-update");
		Exporter gbsExporter = new HibernateMappingExporter();
		gbsExporter.setMetadataDescriptor(metadataDescriptor);
		gbsExporter.setOutputDirectory(outputDir);
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		Element root = document.getDocumentElement();
		// There are 5 attributes because there are non-defaults not set for this test
		Assert.assertEquals("Unexpected number of hibernate-mapping elements ", 5, root.getAttributes().getLength());
		Assert.assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.getAttribute("package"));
		Assert.assertEquals("Unexpected access setting", "field", root.getAttribute("default-access"));
		Assert.assertEquals("Unexpected cascade setting", "save-update", root.getAttribute("default-cascade") );
	}

	@Test
	public void testMetaAttributes() throws Exception {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/meta")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one meta element", 2, nodeList.getLength());
		Node node = (Node) nodeList.item(0);
		Assert.assertEquals(node.getTextContent(),"Basic");
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/id/meta")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one meta element", 1, nodeList.getLength());
		node = (Node) nodeList.item(0);
		Assert.assertEquals(node.getTextContent(),"basicId");		
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/property/meta")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one meta element", 1, nodeList.getLength());
		node = (Node) nodeList.item(0);
		Assert.assertEquals(node.getTextContent(),"description");
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/set/meta")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one meta element", 1, nodeList.getLength());
		node = (Node) nodeList.item(0);
		Assert.assertEquals(node.getTextContent(),"anotherone");	
	}

	@Test
	public void testCollectionAttributes() throws Exception {
		File outputXml = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/set")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one set element", 1, nodeList.getLength());
		Element node = (Element) nodeList.item(0);
		Assert.assertEquals("delete, update", node.getAttribute("cascade"));	
	}
	
	@Test
	public void testComments() throws Exception {
		File outputXml = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/ClassFullAttribute.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/comment")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one comment element", 1, nodeList.getLength());
		Node node = (Node) nodeList.item(0);
		Assert.assertEquals(node.getTextContent(),"A comment for ClassFullAttribute");
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/property/column/comment")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one comment element", 1, nodeList.getLength());
		node = (Node) nodeList.item(0);
		Assert.assertEquals(node.getTextContent(),"columnd comment");
	}

	@Test
	public void testNoComments() throws Exception {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/comment")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get no comment element", nodeList.getLength(), 0);	
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/property/column/comment")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get no comment element", 0, nodeList.getLength());	
	}

	/**
	 * Special test for external Global settings were generated.
	 * Test Access and Cascade setting but they are default values
	 * so they should not appear.
	 */
	@Test
	public void testGlobalSettingsGeneratedAccessAndCascadeDefault()  throws Exception {
		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest");
		hgs.setDefaultAccess("property");
		hgs.setDefaultCascade("none");	
		Exporter gbsExporter = new HibernateMappingExporter();
		gbsExporter.setMetadataDescriptor(metadataDescriptor);
		gbsExporter.setOutputDirectory(outputDir);
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		Element root = document.getDocumentElement();
		// There are 5 attributes because there are non-defaults not set for this test
		Assert.assertEquals("Unexpected number of hibernate-mapping elements ", 5, root.getAttributes().getLength());
		Assert.assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.getAttribute("package"));
		Assert.assertEquals("Unexpected access setting", "property", root.getAttribute("default-access"));
		Assert.assertEquals("Unexpected cascade setting", "none", root.getAttribute("default-cascade"));	
	}

	/**
	 * Special test for external Global settings were generated.
	 * Non default settings for Lazy and AutoImport so they 
	 * should appear.
	 */
	@Test
	public void testGlobalSettingsLasyAndAutoImportNonDefault()  throws Exception {
		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest");
		hgs.setDefaultLazy(false);
		hgs.setAutoImport(false);		
		Exporter gbsExporter = new HibernateMappingExporter();
		gbsExporter.setMetadataDescriptor(metadataDescriptor);
		gbsExporter.setOutputDirectory(outputDir);
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		Element root = document.getDocumentElement();
		// There are 5 attributes because there are non-defaults not set for this test
		Assert.assertEquals("Unexpected number of hibernate-mapping elements ", 5, root.getAttributes().getLength());
		Assert.assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.getAttribute("package"));
		Assert.assertEquals("Unexpected access setting", "false", root.getAttribute("default-lazy"));
		Assert.assertEquals("Unexpected cascade setting", "false", root.getAttribute("auto-import"));
	}

	@Test
	public void testIdGeneratorHasNotArgumentParameters()  throws Exception {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		// Validate the Generator and it has no arguments 
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/id/generator")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertTrue("Expected to get one generator element", nodeList.getLength() == 1);
		Node genAtt = ( (Element)nodeList.item(0)).getAttributeNode("class");
		Assert.assertEquals("Unexpected generator class name", "assigned", genAtt.getTextContent() );
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/id/generator/param")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertTrue("Expected to get no generator param elements", nodeList.getLength() == 0);	
	}
    
	@Test
    public void testIdGeneratorHasArgumentParameters()  throws Exception {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		// Validate the Generator and that it does have arguments 
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/id/generator")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertTrue("Expected to get one generator element", nodeList.getLength() == 1);
		Node genAtt = ( (Element)nodeList.item(0)).getAttributeNode("class");
		Assert.assertEquals("Unexpected generator class name", "org.hibernate.id.TableHiLoGenerator", genAtt.getTextContent());
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/id/generator/param")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get correct number of generator param elements", 2, nodeList.getLength());
		Element tableElement = (Element)nodeList.item(0);
		Attr paramTableAtt = tableElement.getAttributeNode("name");
		Element columnElement = (Element)nodeList.item(1);
		Attr paramColumnAtt = columnElement.getAttributeNode("name");
		if(paramTableAtt.getTextContent().equals("column")) {
			// to make sure the order of the elements doesn't matter.
			Element tempElement = tableElement;
			Attr temp = paramTableAtt;	
			paramTableAtt = paramColumnAtt;
			tableElement = columnElement;
			paramColumnAtt = temp;
			columnElement = tempElement;
		}
		Assert.assertEquals("Unexpected generator param name", "table", paramTableAtt.getTextContent() );
		Assert.assertEquals("Unexpected generator param name", "column", paramColumnAtt.getTextContent() );
		Assert.assertEquals("Unexpected param value for table", "uni_table", tableElement.getTextContent() );
		Assert.assertEquals("Unexpected param value for column", "next_hi_value", columnElement.getTextContent() );
    }

	@Test
	public void testGeneralHbmSettingsQuery()  throws Exception {
		File outputXml = new File(
				outputDir,
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		// Validate the Generator and that it does have arguments 
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/query")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get correct number of query elements", 2, nodeList.getLength() );
		Attr genAtt = ( (Element)nodeList.item(0) ).getAttributeNode("name");
		Assert.assertEquals("Unexpected query name", "test_query_1", genAtt.getTextContent() );
		genAtt = ( (Element)nodeList.item(0) ).getAttributeNode("flush-mode");
		Assert.assertNull("Expected flush-mode value to be null", genAtt);
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("name");
		Assert.assertEquals("Unexpected query name", "test_query_2", genAtt.getTextContent() );
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("flush-mode");
		Assert.assertEquals("Unexpected flush-mode value", "auto", genAtt.getTextContent() );
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("cacheable");
		Assert.assertEquals("Unexpected cacheable value", "true", genAtt.getTextContent() );
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("cache-region");
		Assert.assertEquals("Unexpected cache-region value", "myregion", genAtt.getTextContent() );
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("fetch-size");
		Assert.assertEquals("Unexpected fetch-size value", "10", genAtt.getTextContent() );
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("timeout");
		Assert.assertEquals("Unexpected timeout value", "1000", genAtt.getTextContent() );
	}

	@Test
	public void testGeneralHbmSettingsSQLQueryBasic()  throws Exception {
		File outputXml = new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		// Validate the Generator and that it does have arguments 
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/sql-query")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get correct number of query elements", 6, nodeList.getLength() );
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/sql-query[@name=\"test_sqlquery_1\"]")
				.evaluate(document, XPathConstants.NODESET);
		Element node = (Element)nodeList.item(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_1' not to be null", node);
		Attr genAtt = node.getAttributeNode("flush-mode");
		Assert.assertNull("Expected flush-mode value to be null", genAtt);
	}
	    
	@Test
	public void testGeneralHbmSettingsSQLQueryAllAttributes()  throws Exception {
		File outputXml = new File(
				outputDir,
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		// Validate the Generator and that it does have arguments 
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/sql-query[@name=\"test_sqlquery_2\"]")
				.evaluate(document, XPathConstants.NODESET);
		Element node = (Element)nodeList.item(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_2' not to be null", node);
		Attr genAtt = node.getAttributeNode("name");
		Assert.assertEquals("Unexpected query name", "test_sqlquery_2", genAtt.getTextContent() );
		genAtt = node.getAttributeNode("flush-mode");
		Assert.assertEquals("Unexpected flush-mode value", "auto", genAtt.getTextContent() );
		genAtt = node.getAttributeNode("cacheable");
		Assert.assertEquals("Unexpected cacheable value", "true", genAtt.getTextContent() );
		genAtt = node.getAttributeNode("cache-region");
		Assert.assertEquals("Unexpected cache-region value", "myregion", genAtt.getTextContent() );
		genAtt = node.getAttributeNode("fetch-size");
		Assert.assertEquals("Unexpected fetch-size value", "10", genAtt.getTextContent() );
		genAtt = node.getAttributeNode("timeout");
		Assert.assertEquals("Unexpected timeout value", "1000", genAtt.getTextContent() );
		Element syncTable = (Element)node.getElementsByTagName("synchronize").item(0);
		Assert.assertNull("Expected synchronize element to be null", syncTable);	
	}

	@Test
	public void testGeneralHbmSettingsSQLQuerySynchronize()  throws Exception {
		File outputXml = new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		// Validate the Generator and that it does have arguments 
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/sql-query[@name=\"test_sqlquery_3\"]")
				.evaluate(document, XPathConstants.NODESET);
		Element node = (Element)nodeList.item(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_3' not to be null", node);
		Attr genAtt = node.getAttributeNode("name");
		Assert.assertEquals("Unexpected query name", "test_sqlquery_3", genAtt.getTextContent() );
		Element syncTable = (Element)node.getElementsByTagName("synchronize").item(0);
		Assert.assertNotNull("Expected synchronize element to not be null", syncTable);
		genAtt = syncTable.getAttributeNode("table");
		Assert.assertEquals("Unexpected table value for synchronize element", "mytable", genAtt.getTextContent() );
		Element returnEl = (Element)node.getElementsByTagName("return").item(0);
		Assert.assertNull("Expected return element to be null", returnEl);
	}

	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnRoot()  throws Exception {
		File outputXml = new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		// Validate the Generator and that it does have arguments 
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/sql-query[@name=\"test_sqlquery_4\"]")
				.evaluate(document, XPathConstants.NODESET);
		Element node = (Element)nodeList.item(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_4' not to be null", node);
		Attr genAtt = node.getAttributeNode("name");
		Assert.assertEquals("Unexpected query name", "test_sqlquery_4", genAtt.getTextContent() );
		Element returnEl = (Element)node.getElementsByTagName("return").item(0);
		Assert.assertNotNull("Expected return element to not be null", returnEl);
		genAtt = returnEl.getAttributeNode("alias");
		Assert.assertEquals("Unexpected alias value for return element", "e", genAtt.getTextContent() );
		genAtt = returnEl.getAttributeNode("class");
		Assert.assertEquals("Unexpected class value for return element", "org.hibernate.tool.hbm2x.hbm2hbmxml.BasicGlobals", genAtt.getTextContent());
	}

	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnRole()  throws Exception {
		File outputXml = new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		// Validate the Generator and that it does have arguments 
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/sql-query[@name=\"test_sqlquery_5\"]")
				.evaluate(document, XPathConstants.NODESET);
		Element node = (Element)nodeList.item(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_5' not to be null", node);
		Attr genAtt = node.getAttributeNode("name");
		Assert.assertEquals("Unexpected query name", "test_sqlquery_5", genAtt.getTextContent() );
		Element returnEl = (Element)node.getElementsByTagName("return-join").item(0);
		Assert.assertNotNull("Expected return element to not be null", returnEl);
		genAtt = returnEl.getAttributeNode("alias");
		Assert.assertEquals("Unexpected alias value for return element", "e", genAtt.getTextContent() );
		genAtt = returnEl.getAttributeNode("property");
		Assert.assertEquals("Unexpected property role value for return element", "e.age", genAtt.getTextContent());
	}
	    
	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnCollection()  throws Exception {
		File outputXml = new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		// Validate the Generator and that it does have arguments 
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/sql-query[@name=\"test_sqlquery_6\"]")
				.evaluate(document, XPathConstants.NODESET);
		Element node = (Element)nodeList.item(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_6' not to be null", node);
		Attr genAtt = node.getAttributeNode("name");
		Assert.assertEquals("Unexpected query name", "test_sqlquery_6", genAtt.getTextContent());
		Element returnEl = (Element)node.getElementsByTagName("load-collection").item(0);
		Assert.assertNotNull("Expected return element to not be null", returnEl);
		genAtt = returnEl.getAttributeNode("alias");
		Assert.assertEquals("Unexpected alias value for return element", "e", genAtt.getTextContent());
		genAtt = returnEl.getAttributeNode("role");
		Assert.assertEquals("Unexpected collection role value for return element", "org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest.BasicGlobals.price", genAtt.getTextContent());
		genAtt = returnEl.getAttributeNode("lock-mode");
		Assert.assertEquals("Unexpected class lock-mode for return element", "none", genAtt.getTextContent());
	}
	
}
