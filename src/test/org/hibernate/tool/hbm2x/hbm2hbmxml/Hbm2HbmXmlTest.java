//$Id$

/* 
 * Tests for generating the HBM documents from the Configuration data structure.
 * The generated XML document will be validated and queried to make sure the 
 * basic structure is correct in each test.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.io.File;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.hibernate.util.DTDEntityResolver;

/**
 * Initial implentation based on the Hbm2XTest class.
 * 
 * @author David Channon
 */
public class Hbm2HbmXmlTest extends NonReflectiveTestCase {

	private Exporter hbmexporter;

	public Hbm2HbmXmlTest(String name) {
		super( name, "cfg2hbmoutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		
		hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		hbmexporter.start();		
	}
	
	public void testAllFilesExistence() {

		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml") );
		assertFalse(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/cfg2hbm/GeneralHbmSettings.hbm.xml").exists() );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Basic.hbm.xml") );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/BasicGlobals.hbm.xml") );
		assertFileAndExists(new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/BasicCompositeId.hbm.xml") );
	}
	
	public void testArtifactCollection() {
		
		assertEquals("4 mappings + 1 global",5,hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
		
	}
	
	/**
	 * Special test for external Global settings were generated.
	 * Schema and Catalog settings should appear.
	 */
	public void testGlobalSettingsGeneratedDatabase() throws Exception {

		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml");
		hgs.setSchemaName("myschema");
		hgs.setCatalogName("mycatalog");
		
		Exporter gsExporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		( (HibernateMappingExporter)gsExporter).setGlobalSettings(hgs);
	
		gsExporter.start();

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/BasicGlobals.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		
		// There are 7 attributes because there are defaults defined by the DTD makes up the missing entries
		assertEquals("Unexpected number of hibernate-mapping elements ", 7, root.attributeCount() );
		assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml", root.attribute("package").getStringValue() );
		assertEquals("Unexpected schema name", "myschema", root.attribute("schema").getStringValue() );
		assertEquals("Unexpected mycatalog name", "mycatalog", root.attribute("catalog").getStringValue() );
		
	}

	/**
	 * Special test for external Global settings were generated.
	 * Non-defaults should appear for Cascade and Access.
	 */
	public void testGlobalSettingsGeneratedAccessAndCascadeNonDefault()  throws Exception {

		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml");
		hgs.setDefaultAccess("field");
		hgs.setDefaultCascade("save-update");
		
		Exporter gbsExporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
	
		gbsExporter.start();

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/BasicGlobals.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		
		// There are 5 attributes because there are non-defaults not set for this test
		assertEquals("Unexpected number of hibernate-mapping elements ", 5, root.attributeCount() );
		assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml", root.attribute("package").getStringValue() );
		assertEquals("Unexpected access setting", "field", root.attribute("default-access").getStringValue() );
		assertEquals("Unexpected cascade setting", "save-update", root.attribute("default-cascade").getStringValue() );
	
	}

	public void testMetaAttributes() throws DocumentException {
		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Basic.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);		
	
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/meta");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one meta element", 2, list.size());
		Node node = (Node) list.get(0);
		assertEquals(node.getText(),"Basic");
		
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/meta");
		list = xpath.selectNodes(document);
		assertEquals("Expected to get one meta element", 1, list.size());
		node = (Node) list.get(0);
		assertEquals(node.getText(),"basicId");	
		
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property/meta");
		list = xpath.selectNodes(document);
		assertEquals("Expected to get one meta element", 1, list.size());
		node = (Node) list.get(0);
		assertEquals(node.getText(),"description");
		
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/meta");
		list = xpath.selectNodes(document);
		assertEquals("Expected to get one meta element", 1, list.size());
		node = (Node) list.get(0);
		assertEquals(node.getText(),"anotherone");
		
	}

	public void testCollectionAttributes() throws DocumentException {
		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Basic.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);		
	
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one set element", 1, list.size());
		Element node = (Element) list.get(0);
		assertEquals("delete, update", node.attributeValue("cascade"));
		
	}
	
	public void testComments() throws DocumentException {
		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/ClassFullAttribute.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);		
	
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/comment");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get one comment element", 1, list.size());
		Node node = (Node) list.get(0);
		assertEquals(node.getText(),"A comment for ClassFullAttribute");
		
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property/column/comment");
		list = xpath.selectNodes(document);
		assertEquals("Expected to get one comment element", 1, list.size());
		node = (Node) list.get(0);
		assertEquals(node.getText(),"columnd comment");
	}

	public void testNoComments() throws DocumentException {
		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Basic.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		
	
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/comment");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get no comment element", list.size(), 0);
		
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property/column/comment");
		list = xpath.selectNodes(document);
		assertEquals("Expected to get no comment element", 0, list.size());
		
		
	}

	/**
	 * Special test for external Global settings were generated.
	 * Test Access and Cascade setting but they are default values
	 * so they should not appear.
	 */
	public void testGlobalSettingsGeneratedAccessAndCascadeDefault()  throws Exception {

		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml");
		hgs.setDefaultAccess("property");
		hgs.setDefaultCascade("none");
		
		Exporter gbsExporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
	
		gbsExporter.start();

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/BasicGlobals.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		
		// There are 5 attributes because there are non-defaults not set for this test
		assertEquals("Unexpected number of hibernate-mapping elements ", 5, root.attributeCount() );
		assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml", root.attribute("package").getStringValue() );
		assertEquals("Unexpected access setting", "property", root.attribute("default-access").getStringValue() );
		assertEquals("Unexpected cascade setting", "none", root.attribute("default-cascade").getStringValue() );
		
	}

	/**
	 * Special test for external Global settings were generated.
	 * Non default settings for Lazy and AutoImport so they 
	 * should appear.
	 */
	public void testGlobalSettingsLasyAndAutoImportNonDefault()  throws Exception {

		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml");
		hgs.setDefaultLazy(false);
		hgs.setAutoImport(false);		
		
		Exporter gbsExporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/BasicGlobals.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		
		// There are 5 attributes because there are non-defaults not set for this test
		assertEquals("Unexpected number of hibernate-mapping elements ", 5, root.attributeCount() );
		assertEquals("Unexpected package name", "org.hibernate.tool.hbm2x.hbm2hbmxml", root.attribute("package").getStringValue() );
		assertEquals("Unexpected access setting", "false", root.attribute("default-lazy").getStringValue() );
		assertEquals("Unexpected cascade setting", "false", root.attribute("auto-import").getStringValue() );
		
	}

	public void testIdGeneratorHasNotArgumentParameters()  throws Exception {

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/BasicGlobals.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		
		// Validate the Generator and it has no arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator");
		List list = xpath.selectNodes(document);
		assertTrue("Expected to get one generator element", list.size() == 1);
		Attribute genAtt = ( (Element)list.get(0) ).attribute("class");
		assertEquals("Unexpected generator class name", "assigned", genAtt.getStringValue() );

		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator/param");
		list = xpath.selectNodes(document);
		assertTrue("Expected to get no generator param elements", list.size() == 0);
		
	}
    
	
    public void testIdGeneratorHasArgumentParameters()  throws Exception {

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/org/hibernate/tool/hbm2x/hbm2hbmxml/Basic.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator");
		List list = xpath.selectNodes(document);
		assertTrue("Expected to get one generator element", list.size() == 1);
		Attribute genAtt = ( (Element)list.get(0) ).attribute("class");
		assertEquals("Unexpected generator class name", "org.hibernate.id.TableHiLoGenerator", genAtt.getStringValue() );

		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator/param");
		list = xpath.selectNodes(document);
		assertEquals("Expected to get correct number of generator param elements", 2, list.size() );
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
		
		assertEquals("Unexpected generator param name", "table", paramTableAtt.getStringValue() );
		assertEquals("Unexpected generator param name", "column", paramColumnAtt.getStringValue() );
		assertEquals("Unexpected param value for table", "uni_table", tableElement.getStringValue() );
		assertEquals("Unexpected param value for column", "next_hi_value", columnElement.getStringValue() );
    }

	public void testGeneralHbmSettingsQuery()  throws Exception {

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/query");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get correct number of query elements", 2, list.size() );
		
		Attribute genAtt = ( (Element)list.get(0) ).attribute("name");
		assertEquals("Unexpected query name", "test_query_1", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(0) ).attribute("flush-mode");
		assertNull("Expected flush-mode value to be null", genAtt);
		
		genAtt = ( (Element)list.get(1) ).attribute("name");
		assertEquals("Unexpected query name", "test_query_2", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(1) ).attribute("flush-mode");
		assertEquals("Unexpected flush-mode value", "auto", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(1) ).attribute("cacheable");
		assertEquals("Unexpected cacheable value", "true", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(1) ).attribute("cache-region");
		assertEquals("Unexpected cache-region value", "myregion", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(1) ).attribute("fetch-size");
		assertEquals("Unexpected fetch-size value", "10", genAtt.getStringValue() );
		genAtt = ( (Element)list.get(1) ).attribute("timeout");
		assertEquals("Unexpected timeout value", "1000", genAtt.getStringValue() );

	}

	public void testGeneralHbmSettingsSQLQueryBasic()  throws Exception {

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query");
		List list = xpath.selectNodes(document);
		assertEquals("Expected to get correct number of query elements", 6, list.size() );

		xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_1\"]");
		list = xpath.selectNodes(document);
		
		Element node = (Element)list.get(0);
		assertNotNull("Expected sql-query named 'test_sqlquery_1' not to be null", node);

		Attribute genAtt = node.attribute("flush-mode");
		assertNull("Expected flush-mode value to be null", genAtt);
	}
	    
	public void testGeneralHbmSettingsSQLQueryAllAttributes()  throws Exception {

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_2\"]");
		List list = xpath.selectNodes(document);
		
		Element node = (Element)list.get(0);
		assertNotNull("Expected sql-query named 'test_sqlquery_2' not to be null", node);

		Attribute genAtt = node.attribute("name");
		assertEquals("Unexpected query name", "test_sqlquery_2", genAtt.getStringValue() );
		genAtt = node.attribute("flush-mode");
		assertEquals("Unexpected flush-mode value", "auto", genAtt.getStringValue() );
		genAtt = node.attribute("cacheable");
		assertEquals("Unexpected cacheable value", "true", genAtt.getStringValue() );
		genAtt = node.attribute("cache-region");
		assertEquals("Unexpected cache-region value", "myregion", genAtt.getStringValue() );
		genAtt = node.attribute("fetch-size");
		assertEquals("Unexpected fetch-size value", "10", genAtt.getStringValue() );
		genAtt = node.attribute("timeout");
		assertEquals("Unexpected timeout value", "1000", genAtt.getStringValue() );

		Element syncTable = node.element("synchronize");
		assertNull("Expected synchronize element to be null", syncTable);
		
	}

	public void testGeneralHbmSettingsSQLQuerySynchronize()  throws Exception {

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_3\"]");
		List list = xpath.selectNodes(document);
		
		Element node = (Element)list.get(0);
		assertNotNull("Expected sql-query named 'test_sqlquery_3' not to be null", node);

		Attribute genAtt = node.attribute("name");
		assertEquals("Unexpected query name", "test_sqlquery_3", genAtt.getStringValue() );

		Element syncTable = node.element("synchronize");
		assertNotNull("Expected synchronize element to not be null", syncTable);
		genAtt = syncTable.attribute("table");
		assertEquals("Unexpected table value for synchronize element", "mytable", genAtt.getStringValue() );

		Element returnEl = node.element("return");
		assertNull("Expected return element to be null", returnEl);

	}

	public void testGeneralHbmSettingsSQLQueryWithReturnRoot()  throws Exception {

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_4\"]");
		List list = xpath.selectNodes(document);
		
		Element node = (Element)list.get(0);
		assertNotNull("Expected sql-query named 'test_sqlquery_4' not to be null", node);

		Attribute genAtt = node.attribute("name");
		assertEquals("Unexpected query name", "test_sqlquery_4", genAtt.getStringValue() );

		Element returnEl = node.element("return");
		assertNotNull("Expected return element to not be null", returnEl);
		genAtt = returnEl.attribute("alias");
		assertEquals("Unexpected alias value for return element", "e", genAtt.getStringValue() );
		genAtt = returnEl.attribute("class");
		assertEquals("Unexpected class value for return element", "org.hibernate.tool.hbm2x.hbm2hbmxml.elephant", genAtt.getStringValue() );

	}

	public void testGeneralHbmSettingsSQLQueryWithReturnRole()  throws Exception {

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_5\"]");
		List list = xpath.selectNodes(document);
		
		Element node = (Element)list.get(0);
		assertNotNull("Expected sql-query named 'test_sqlquery_5' not to be null", node);

		Attribute genAtt = node.attribute("name");
		assertEquals("Unexpected query name", "test_sqlquery_5", genAtt.getStringValue() );

		Element returnEl = node.element("return-join");
		assertNotNull("Expected return element to not be null", returnEl);
		genAtt = returnEl.attribute("alias");
		assertEquals("Unexpected alias value for return element", "e", genAtt.getStringValue() );
		genAtt = returnEl.attribute("property");
		assertEquals("Unexpected property role value for return element", "e.age", genAtt.getStringValue() );

	}
	    
	public void testGeneralHbmSettingsSQLQueryWithReturnCollection()  throws Exception {

		File outputXml = new File(getOutputDir().getAbsolutePath() + "/GeneralHbmSettings.hbm.xml");
		assertFileAndExists(outputXml);

		SAXReader xmlReader =  this.getSAXReader();
		
		Document document = xmlReader.read(outputXml);
		
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_6\"]");
		List list = xpath.selectNodes(document);
		
		Element node = (Element)list.get(0);
		assertNotNull("Expected sql-query named 'test_sqlquery_6' not to be null", node);

		Attribute genAtt = node.attribute("name");
		assertEquals("Unexpected query name", "test_sqlquery_6", genAtt.getStringValue() );

		Element returnEl = node.element("load-collection");
		assertNotNull("Expected return element to not be null", returnEl);
		genAtt = returnEl.attribute("alias");
		assertEquals("Unexpected alias value for return element", "e", genAtt.getStringValue() );
		genAtt = returnEl.attribute("role");
		assertEquals("Unexpected collection role value for return element", "org.hibernate.tool.hbm2x.hbm2hbmxml.elephant.age", genAtt.getStringValue() );
		genAtt = returnEl.attribute("lock-mode");
		assertEquals("Unexpected class lock-mode for return element", "none", genAtt.getStringValue() );

	}
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/hbm2hbmxml/";
	}
	
	protected String[] getMappings() {
		return new String[] { 
				"Basic.hbm.xml",
				"BasicGlobals.hbm.xml",
				"ClassFullAttribute.hbm.xml",
				"BasicCompositeId.hbm.xml"
		};
	}
	    
	public static Test suite() {
		return new TestSuite(Hbm2HbmXmlTest.class);
	}

}
