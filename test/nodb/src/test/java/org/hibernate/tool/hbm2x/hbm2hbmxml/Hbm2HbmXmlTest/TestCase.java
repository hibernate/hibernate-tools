//$Id$

/* 
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the 
 * basic structure is correct in each test.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest;

import java.io.File;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.hbm.HibernateMappingExporter;
import org.hibernate.tool.internal.export.hbm.HibernateMappingGlobalSettings;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
	private HibernateMappingExporter hbmexporter = null;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
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
		gsExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		gsExporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		( (HibernateMappingExporter)gsExporter).setGlobalSettings(hgs);
		gsExporter.start();
		File outputXml = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		// There are 7 attributes because there are defaults defined by the DTD makes up the missing entries
		Assert.assertEquals("Unexpected number of hibernate-mapping elements ", 7, root.attributeCount() );
		Assert.assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.attribute("package").getStringValue() );
		Assert.assertEquals("Unexpected schema name", "myschema", root.attribute("schema").getStringValue() );
		Assert.assertEquals("Unexpected mycatalog name", "mycatalog", root.attribute("catalog").getStringValue() );
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
		gbsExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		gbsExporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		// There are 5 attributes because there are non-defaults not set for this test
		Assert.assertEquals("Unexpected number of hibernate-mapping elements ", 5, root.attributeCount() );
		Assert.assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.attribute("package").getStringValue() );
		Assert.assertEquals("Unexpected access setting", "field", root.attribute("default-access").getStringValue() );
		Assert.assertEquals("Unexpected cascade setting", "save-update", root.attribute("default-cascade").getStringValue() );
	}

	@Test
	public void testMetaAttributes() throws DocumentException {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);		
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/meta");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one meta element", 2, list.size());
		Node node = (Node) list.get(0);
		Assert.assertEquals(node.getText(),"Basic");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/meta");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one meta element", 1, list.size());
		node = (Node) list.get(0);
		Assert.assertEquals(node.getText(),"basicId");		
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property/meta");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one meta element", 1, list.size());
		node = (Node) list.get(0);
		Assert.assertEquals(node.getText(),"description");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/meta");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one meta element", 1, list.size());
		node = (Node) list.get(0);
		Assert.assertEquals(node.getText(),"anotherone");	
	}

	@Test
	public void testCollectionAttributes() throws DocumentException {
		File outputXml = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);		
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one set element", 1, list.size());
		Element node = (Element) list.get(0);
		Assert.assertEquals("delete, update", node.attributeValue("cascade"));	
	}
	
	@Test
	public void testComments() throws DocumentException {
		File outputXml = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/ClassFullAttribute.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);		
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/comment");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one comment element", 1, list.size());
		Node node = (Node) list.get(0);
		Assert.assertEquals(node.getText(),"A comment for ClassFullAttribute");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property/column/comment");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one comment element", 1, list.size());
		node = (Node) list.get(0);
		Assert.assertEquals(node.getText(),"columnd comment");
	}

	@Test
	public void testNoComments() throws DocumentException {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/comment");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get no comment element", list.size(), 0);	
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property/column/comment");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get no comment element", 0, list.size());	
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
		gbsExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		gbsExporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		// There are 5 attributes because there are non-defaults not set for this test
		Assert.assertEquals("Unexpected number of hibernate-mapping elements ", 5, root.attributeCount() );
		Assert.assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.attribute("package").getStringValue() );
		Assert.assertEquals("Unexpected access setting", "property", root.attribute("default-access").getStringValue() );
		Assert.assertEquals("Unexpected cascade setting", "none", root.attribute("default-cascade").getStringValue() );	
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
		gbsExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		gbsExporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		// There are 5 attributes because there are non-defaults not set for this test
		Assert.assertEquals("Unexpected number of hibernate-mapping elements ", 5, root.attributeCount() );
		Assert.assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.attribute("package").getStringValue() );
		Assert.assertEquals("Unexpected access setting", "false", root.attribute("default-lazy").getStringValue() );
		Assert.assertEquals("Unexpected cascade setting", "false", root.attribute("auto-import").getStringValue() );
	}

	@Test
	public void testIdGeneratorHasNotArgumentParameters()  throws Exception {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and it has no arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator");
		List<?> list = xpath.selectNodes(document);
		Assert.assertTrue("Expected to get one generator element", list.size() == 1);
		Attribute genAtt = ( (Element)list.get(0) ).attribute("class");
		Assert.assertEquals("Unexpected generator class name", "assigned", genAtt.getStringValue() );
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator/param");
		list = xpath.selectNodes(document);
		Assert.assertTrue("Expected to get no generator param elements", list.size() == 0);	
	}
    
	@Test
    public void testIdGeneratorHasArgumentParameters()  throws Exception {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator");
		List<?> list = xpath.selectNodes(document);
		Assert.assertTrue("Expected to get one generator element", list.size() == 1);
		Attribute genAtt = ( (Element)list.get(0) ).attribute("class");
		Assert.assertEquals("Unexpected generator class name", "org.hibernate.id.TableHiLoGenerator", genAtt.getStringValue() );
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator/param");
		list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get correct number of generator param elements", 2, list.size() );
		Element tableElement = (Element)list.get(0);
		Attribute paramTableAtt = tableElement.attribute("name");
		Element columnElement = (Element)list.get(1);
		Attribute paramColumnAtt = columnElement.attribute("name");
		if(paramTableAtt.getStringValue().equals("column")) {
			// to make sure the order of the elements doesn't matter.
			Element tempElement = tableElement;
			Attribute temp = paramTableAtt;	
			paramTableAtt = paramColumnAtt;
			tableElement = columnElement;
			paramColumnAtt = temp;
			columnElement = tempElement;
		}
		Assert.assertEquals("Unexpected generator param name", "table", paramTableAtt.getStringValue() );
		Assert.assertEquals("Unexpected generator param name", "column", paramColumnAtt.getStringValue() );
		Assert.assertEquals("Unexpected param value for table", "uni_table", tableElement.getStringValue() );
		Assert.assertEquals("Unexpected param value for column", "next_hi_value", columnElement.getStringValue() );
    }

	@Test
	public void testGeneralHbmSettingsQuery()  throws Exception {
		File outputXml = new File(
				outputDir,
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/query");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get correct number of query elements", 2, list.size() );
		Attribute genAtt = ( (Element)list.get(0) ).attribute("name");
		Assert.assertEquals("Unexpected query name", "test_query_1", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(0) ).attribute("flush-mode");
		Assert.assertNull("Expected flush-mode value to be null", genAtt);
		genAtt = ( (Element)list.get(1) ).attribute("name");
		Assert.assertEquals("Unexpected query name", "test_query_2", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(1) ).attribute("flush-mode");
		Assert.assertEquals("Unexpected flush-mode value", "auto", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(1) ).attribute("cacheable");
		Assert.assertEquals("Unexpected cacheable value", "true", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(1) ).attribute("cache-region");
		Assert.assertEquals("Unexpected cache-region value", "myregion", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(1) ).attribute("fetch-size");
		Assert.assertEquals("Unexpected fetch-size value", "10", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(1) ).attribute("timeout");
		Assert.assertEquals("Unexpected timeout value", "1000", genAtt.getStringValue() );
	}

	@Test
	public void testGeneralHbmSettingsSQLQueryBasic()  throws Exception {
		File outputXml = new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get correct number of query elements", 6, list.size() );
		xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_1\"]");
		list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_1' not to be null", node);
		Attribute genAtt = node.attribute("flush-mode");
		Assert.assertNull("Expected flush-mode value to be null", genAtt);
	}
	    
	@Test
	public void testGeneralHbmSettingsSQLQueryAllAttributes()  throws Exception {
		File outputXml = new File(
				outputDir,
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_2\"]");
		List<?> list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_2' not to be null", node);
		Attribute genAtt = node.attribute("name");
		Assert.assertEquals("Unexpected query name", "test_sqlquery_2", genAtt.getStringValue() );
		genAtt = node.attribute("flush-mode");
		Assert.assertEquals("Unexpected flush-mode value", "auto", genAtt.getStringValue() );
		genAtt = node.attribute("cacheable");
		Assert.assertEquals("Unexpected cacheable value", "true", genAtt.getStringValue() );
		genAtt = node.attribute("cache-region");
		Assert.assertEquals("Unexpected cache-region value", "myregion", genAtt.getStringValue() );
		genAtt = node.attribute("fetch-size");
		Assert.assertEquals("Unexpected fetch-size value", "10", genAtt.getStringValue() );
		genAtt = node.attribute("timeout");
		Assert.assertEquals("Unexpected timeout value", "1000", genAtt.getStringValue() );
		Element syncTable = node.element("synchronize");
		Assert.assertNull("Expected synchronize element to be null", syncTable);	
	}

	@Test
	public void testGeneralHbmSettingsSQLQuerySynchronize()  throws Exception {
		File outputXml = new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_3\"]");
		List<?> list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_3' not to be null", node);
		Attribute genAtt = node.attribute("name");
		Assert.assertEquals("Unexpected query name", "test_sqlquery_3", genAtt.getStringValue() );
		Element syncTable = node.element("synchronize");
		Assert.assertNotNull("Expected synchronize element to not be null", syncTable);
		genAtt = syncTable.attribute("table");
		Assert.assertEquals("Unexpected table value for synchronize element", "mytable", genAtt.getStringValue() );
		Element returnEl = node.element("return");
		Assert.assertNull("Expected return element to be null", returnEl);
	}

	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnRoot()  throws Exception {
		File outputXml = new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_4\"]");
		List<?> list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_4' not to be null", node);
		Attribute genAtt = node.attribute("name");
		Assert.assertEquals("Unexpected query name", "test_sqlquery_4", genAtt.getStringValue() );
		Element returnEl = node.element("return");
		Assert.assertNotNull("Expected return element to not be null", returnEl);
		genAtt = returnEl.attribute("alias");
		Assert.assertEquals("Unexpected alias value for return element", "e", genAtt.getStringValue() );
		genAtt = returnEl.attribute("class");
		Assert.assertEquals("Unexpected class value for return element", "org.hibernate.tool.hbm2x.hbm2hbmxml.BasicGlobals", genAtt.getStringValue());
	}

	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnRole()  throws Exception {
		File outputXml = new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_5\"]");
		List<?> list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_5' not to be null", node);
		Attribute genAtt = node.attribute("name");
		Assert.assertEquals("Unexpected query name", "test_sqlquery_5", genAtt.getStringValue() );
		Element returnEl = node.element("return-join");
		Assert.assertNotNull("Expected return element to not be null", returnEl);
		genAtt = returnEl.attribute("alias");
		Assert.assertEquals("Unexpected alias value for return element", "e", genAtt.getStringValue() );
		genAtt = returnEl.attribute("property");
		Assert.assertEquals("Unexpected property role value for return element", "e.age", genAtt.getStringValue());
	}
	    
	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnCollection()  throws Exception {
		File outputXml = new File(
				outputDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_6\"]");
		List<?> list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		Assert.assertNotNull("Expected sql-query named 'test_sqlquery_6' not to be null", node);
		Attribute genAtt = node.attribute("name");
		Assert.assertEquals("Unexpected query name", "test_sqlquery_6", genAtt.getStringValue());
		Element returnEl = node.element("load-collection");
		Assert.assertNotNull("Expected return element to not be null", returnEl);
		genAtt = returnEl.attribute("alias");
		Assert.assertEquals("Unexpected alias value for return element", "e", genAtt.getStringValue());
		genAtt = returnEl.attribute("role");
		Assert.assertEquals("Unexpected collection role value for return element", "org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest.BasicGlobals.price", genAtt.getStringValue());
		genAtt = returnEl.attribute("lock-mode");
		Assert.assertEquals("Unexpected class lock-mode for return element", "none", genAtt.getStringValue());
	}
	
}
