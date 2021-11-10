/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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
	
	@TempDir
	public File outputFolder = new File("output");
	
	private MetadataDescriptor metadataDescriptor = null;
	private File srcDir = null;
	private File resourcesDir = null;
	private Exporter hbmexporter = null;

	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "output");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataDescriptor(metadataDescriptor);
		hbmexporter.setOutputDirectory(srcDir);
		hbmexporter.start();
	}
	
	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, 
				"GeneralHbmSettings.hbm.xml") );
		assertFalse(new File(
				srcDir, 
				"org/hibernate/tool/cfg2hbm/GeneralHbmSettings.hbm.xml").exists() );
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicCompositeId.hbm.xml") );
	}
	
	@Test
	public void testArtifactCollection() {
		assertEquals(
				5,
				hbmexporter.getArtifactCollector().getFileCount("hbm.xml"),
				"4 mappings + 1 global");
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
		gsExporter.setOutputDirectory(srcDir);
		( (HibernateMappingExporter)gsExporter).setGlobalSettings(hgs);
		gsExporter.start();
		File outputXml = new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		Element root = document.getDocumentElement();
		// There are 7 attributes because there are defaults defined by the DTD makes up the missing entries
		assertEquals(7, root.getAttributes().getLength(), "Unexpected number of hibernate-mapping elements ");
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.getAttribute("package"), "Unexpected package name");
		assertEquals("myschema", root.getAttribute("schema"), "Unexpected schema name");
		assertEquals("mycatalog", root.getAttribute("catalog"), "Unexpected mycatalog name");
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
		gbsExporter.setOutputDirectory(srcDir);
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		Element root = document.getDocumentElement();
		// There are 5 attributes because there are non-defaults not set for this test
		assertEquals(5, root.getAttributes().getLength(), "Unexpected number of hibernate-mapping elements ");
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.getAttribute("package"), "Unexpected package name");
		assertEquals("field", root.getAttribute("default-access"), "Unexpected access setting");
		assertEquals("save-update", root.getAttribute("default-cascade"), "Unexpected cascade setting");
	}

	@Test
	public void testMetaAttributes() throws Exception {
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/meta")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(2, nodeList.getLength(), "Expected to get one meta element");
		Node node = (Node) nodeList.item(0);
		assertEquals(node.getTextContent(),"Basic");
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/id/meta")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(1, nodeList.getLength(), "Expected to get one meta element");
		node = (Node) nodeList.item(0);
		assertEquals(node.getTextContent(),"basicId");		
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/property/meta")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(1, nodeList.getLength(), "Expected to get one meta element");
		node = (Node) nodeList.item(0);
		assertEquals(node.getTextContent(),"description");
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/set/meta")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(1, nodeList.getLength(), "Expected to get one meta element");
		node = (Node) nodeList.item(0);
		assertEquals(node.getTextContent(),"anotherone");	
	}

	@Test
	public void testCollectionAttributes() throws Exception {
		File outputXml = new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/set")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(1, nodeList.getLength(), "Expected to get one set element");
		Element node = (Element) nodeList.item(0);
		assertEquals("delete, update", node.getAttribute("cascade"));	
	}
	
	@Test
	public void testComments() throws Exception {
		File outputXml = new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/ClassFullAttribute.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/comment")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(1, nodeList.getLength(), "Expected to get one comment element");
		Node node = (Node) nodeList.item(0);
		assertEquals(node.getTextContent(),"A comment for ClassFullAttribute");
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/property/column/comment")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(1, nodeList.getLength(), "Expected to get one comment element");
		node = (Node) nodeList.item(0);
		assertEquals(node.getTextContent(),"columnd comment");
	}

	@Test
	public void testNoComments() throws Exception {
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/comment")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(nodeList.getLength(), 0, "Expected to get no comment element");	
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/property/column/comment")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(0, nodeList.getLength(), "Expected to get no comment element");	
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
		gbsExporter.setOutputDirectory(srcDir);
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		Element root = document.getDocumentElement();
		// There are 5 attributes because there are non-defaults not set for this test
		assertEquals(5, root.getAttributes().getLength(), "Unexpected number of hibernate-mapping elements ");
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.getAttribute("package"), "Unexpected package name");
		assertEquals("property", root.getAttribute("default-access"), "Unexpected access setting");
		assertEquals("none", root.getAttribute("default-cascade"), "Unexpected cascade setting");	
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
		gbsExporter.setOutputDirectory(srcDir);
		( (HibernateMappingExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		Element root = document.getDocumentElement();
		// There are 5 attributes because there are non-defaults not set for this test
		assertEquals(5, root.getAttributes().getLength(), "Unexpected number of hibernate-mapping elements ");
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.getAttribute("package"), "Unexpected package name");
		assertEquals("false", root.getAttribute("default-lazy"), "Unexpected access setting");
		assertEquals("false", root.getAttribute("auto-import"), "Unexpected cascade setting");
	}

	@Test
	public void testIdGeneratorHasNotArgumentParameters()  throws Exception {
		File outputXml = new File(
				srcDir,
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
		assertTrue(nodeList.getLength() == 1, "Expected to get one generator element");
		Node genAtt = ( (Element)nodeList.item(0)).getAttributeNode("class");
		assertEquals("assigned", genAtt.getTextContent(), "Unexpected generator class name");
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/id/generator/param")
				.evaluate(document, XPathConstants.NODESET);
		assertTrue(nodeList.getLength() == 0, "Expected to get no generator param elements");	
	}
    
	@Test
    public void testIdGeneratorHasArgumentParameters()  throws Exception {
		File outputXml = new File(
				srcDir,
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
		assertTrue(nodeList.getLength() == 1, "Expected to get one generator element");
		Node genAtt = ( (Element)nodeList.item(0)).getAttributeNode("class");
		assertEquals("org.hibernate.id.TableHiLoGenerator", genAtt.getTextContent(), "Unexpected generator class name");
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/id/generator/param")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(2, nodeList.getLength(), "Expected to get correct number of generator param elements");
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
		assertEquals("table", paramTableAtt.getTextContent(), "Unexpected generator param name");
		assertEquals("column", paramColumnAtt.getTextContent(), "Unexpected generator param name");
		assertEquals("uni_table", tableElement.getTextContent(), "Unexpected param value for table");
		assertEquals("next_hi_value", columnElement.getTextContent(), "Unexpected param value for column");
    }

	@Test
	public void testGeneralHbmSettingsQuery()  throws Exception {
		File outputXml = new File(
				srcDir,
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
		assertEquals(2, nodeList.getLength(), "Expected to get correct number of query elements");
		Attr genAtt = ( (Element)nodeList.item(0) ).getAttributeNode("name");
		assertEquals("test_query_1", genAtt.getTextContent(), "Unexpected query name");
		genAtt = ( (Element)nodeList.item(0) ).getAttributeNode("flush-mode");
		assertNull(genAtt, "Expected flush-mode value to be null");
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("name");
		assertEquals("test_query_2", genAtt.getTextContent(), "Unexpected query name");
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("flush-mode");
		assertEquals("auto", genAtt.getTextContent(), "Unexpected flush-mode value");
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("cacheable");
		assertEquals("true", genAtt.getTextContent(), "Unexpected cacheable value");
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("cache-region");
		assertEquals("myregion", genAtt.getTextContent(), "Unexpected cache-region value");
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("fetch-size");
		assertEquals("10", genAtt.getTextContent(), "Unexpected fetch-size value");
		genAtt = ( (Element)nodeList.item(1) ).getAttributeNode("timeout");
		assertEquals("1000", genAtt.getTextContent(), "Unexpected timeout value");
	}

	@Test
	public void testGeneralHbmSettingsSQLQueryBasic()  throws Exception {
		File outputXml = new File(
				srcDir, 
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
		assertEquals(6, nodeList.getLength(), "Expected to get correct number of query elements");
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/sql-query[@name=\"test_sqlquery_1\"]")
				.evaluate(document, XPathConstants.NODESET);
		Element node = (Element)nodeList.item(0);
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_1' not to be null");
		Attr genAtt = node.getAttributeNode("flush-mode");
		assertNull(genAtt, "Expected flush-mode value to be null");
	}
	    
	@Test
	public void testGeneralHbmSettingsSQLQueryAllAttributes()  throws Exception {
		File outputXml = new File(
				srcDir,
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
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_2' not to be null");
		Attr genAtt = node.getAttributeNode("name");
		assertEquals("test_sqlquery_2", genAtt.getTextContent(), "Unexpected query name");
		genAtt = node.getAttributeNode("flush-mode");
		assertEquals("auto", genAtt.getTextContent(), "Unexpected flush-mode value");
		genAtt = node.getAttributeNode("cacheable");
		assertEquals("true", genAtt.getTextContent(), "Unexpected cacheable value");
		genAtt = node.getAttributeNode("cache-region");
		assertEquals("myregion", genAtt.getTextContent(), "Unexpected cache-region value");
		genAtt = node.getAttributeNode("fetch-size");
		assertEquals("10", genAtt.getTextContent(), "Unexpected fetch-size value");
		genAtt = node.getAttributeNode("timeout");
		assertEquals("1000", genAtt.getTextContent(), "Unexpected timeout value");
		Element syncTable = (Element)node.getElementsByTagName("synchronize").item(0);
		assertNull(syncTable, "Expected synchronize element to be null");	
	}

	@Test
	public void testGeneralHbmSettingsSQLQuerySynchronize()  throws Exception {
		File outputXml = new File(
				srcDir, 
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
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_3' not to be null");
		Attr genAtt = node.getAttributeNode("name");
		assertEquals("test_sqlquery_3", genAtt.getTextContent(), "Unexpected query name");
		Element syncTable = (Element)node.getElementsByTagName("synchronize").item(0);
		assertNotNull(syncTable, "Expected synchronize element to not be null");
		genAtt = syncTable.getAttributeNode("table");
		assertEquals("mytable", genAtt.getTextContent(), "Unexpected table value for synchronize element");
		Element returnEl = (Element)node.getElementsByTagName("return").item(0);
		assertNull(returnEl, "Expected return element to be null");
	}

	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnRoot()  throws Exception {
		File outputXml = new File(
				srcDir, 
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
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_4' not to be null");
		Attr genAtt = node.getAttributeNode("name");
		assertEquals(genAtt.getTextContent(), "test_sqlquery_4", "Unexpected query name");
		Element returnEl = (Element)node.getElementsByTagName("return").item(0);
		assertNotNull(returnEl, "Expected return element to not be null");
		genAtt = returnEl.getAttributeNode("alias");
		assertEquals("e", genAtt.getTextContent(), "Unexpected alias value for return element");
		genAtt = returnEl.getAttributeNode("class");
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.BasicGlobals", genAtt.getTextContent(), "Unexpected class value for return element");
	}

	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnRole()  throws Exception {
		File outputXml = new File(
				srcDir, 
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
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_5' not to be null");
		Attr genAtt = node.getAttributeNode("name");
		assertEquals("test_sqlquery_5", genAtt.getTextContent(), "Unexpected query name");
		Element returnEl = (Element)node.getElementsByTagName("return-join").item(0);
		assertNotNull(returnEl, "Expected return element to not be null");
		genAtt = returnEl.getAttributeNode("alias");
		assertEquals("e", genAtt.getTextContent(), "Unexpected alias value for return element");
		genAtt = returnEl.getAttributeNode("property");
		assertEquals("e.age", genAtt.getTextContent(), "Unexpected property role value for return element");
	}
	    
	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnCollection()  throws Exception {
		File outputXml = new File(
				srcDir, 
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
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_6' not to be null");
		Attr genAtt = node.getAttributeNode("name");
		assertEquals("test_sqlquery_6", genAtt.getTextContent(), "Unexpected query name");
		Element returnEl = (Element)node.getElementsByTagName("load-collection").item(0);
		assertNotNull(returnEl, "Expected return element to not be null");
		genAtt = returnEl.getAttributeNode("alias");
		assertEquals("e", genAtt.getTextContent(), "Unexpected alias value for return element");
		genAtt = returnEl.getAttributeNode("role");
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest.BasicGlobals.price", genAtt.getTextContent(), "Unexpected collection role value for return element");
		genAtt = returnEl.getAttributeNode("lock-mode");
		assertEquals("none", genAtt.getTextContent(), "Unexpected class lock-mode for return element");
	}
	
}
