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
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.hbm.HibernateMappingGlobalSettings;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Initial implentation based on the Hbm2XTest class.
 * 
 * @author David Channon
 * @author koen
 */
public class TestCase {

	/**	@TempDir
	public File outputFolder = new File("output");
	

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
	private HbmExporter hbmexporter = null;

	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "src");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HbmExporter();
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
		hbmexporter.start();
	}
	
	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
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
	
	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testArtifactCollection() {
		assertEquals(
				5,
				hbmexporter.getArtifactCollector().getFileCount("hbm.xml"),
				"4 mappings + 1 global");
	}
	
	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGlobalSettingsGeneratedDatabase() throws Exception {
		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest");
		hgs.setSchemaName("myschema");
		hgs.setCatalogName("mycatalog");		
		Exporter gsExporter = new HbmExporter();
		gsExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		gsExporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
		( (HbmExporter)gsExporter).setGlobalSettings(hgs);
		gsExporter.start();
		File outputXml = new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		// There are 7 attributes because there are defaults defined by the DTD makes up the missing entries
		assertEquals(7, root.attributeCount(), "Unexpected number of hibernate-mapping elements " );
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.attribute("package").getStringValue(), "Unexpected package name" );
		assertEquals("myschema", root.attribute("schema").getStringValue(), "Unexpected schema name" );
		assertEquals("mycatalog", root.attribute("catalog").getStringValue(), "Unexpected mycatalog name" );
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGlobalSettingsGeneratedAccessAndCascadeNonDefault()  throws Exception {
		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest");
		hgs.setDefaultAccess("field");
		hgs.setDefaultCascade("save-update");
		Exporter gbsExporter = new HbmExporter();
		gbsExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		gbsExporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
		( (HbmExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		// There are 5 attributes because there are non-defaults not set for this test
		assertEquals(5, root.attributeCount(), "Unexpected number of hibernate-mapping elements " );
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.attribute("package").getStringValue(), "Unexpected package name" );
		assertEquals("field", root.attribute("default-access").getStringValue(), "Unexpected access setting" );
		assertEquals("save-update", root.attribute("default-cascade").getStringValue(), "Unexpected cascade setting" );
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testMetaAttributes() throws DocumentException {
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);		
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/meta");
		List<?> list = xpath.selectNodes(document);
		assertEquals(2, list.size(), "Expected to get one meta element");
		Node node = (Node) list.get(0);
		assertEquals(node.getText(),"Basic");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/meta");
		list = xpath.selectNodes(document);
		assertEquals(1, list.size(), "Expected to get one meta element");
		node = (Node) list.get(0);
		assertEquals(node.getText(),"basicId");		
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property/meta");
		list = xpath.selectNodes(document);
		assertEquals(1, list.size(), "Expected to get one meta element");
		node = (Node) list.get(0);
		assertEquals(node.getText(),"description");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set/meta");
		list = xpath.selectNodes(document);
		assertEquals(1, list.size(), "Expected to get one meta element");
		node = (Node) list.get(0);
		assertEquals(node.getText(),"anotherone");	
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testCollectionAttributes() throws DocumentException {
		File outputXml = new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);		
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/set");
		List<?> list = xpath.selectNodes(document);
		assertEquals(1, list.size(), "Expected to get one set element");
		Element node = (Element) list.get(0);
		assertEquals("delete, update", node.attributeValue("cascade"));	
	}
	
	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testComments() throws DocumentException {
		File outputXml = new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/ClassFullAttribute.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);		
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/comment");
		List<?> list = xpath.selectNodes(document);
		assertEquals(1, list.size(), "Expected to get one comment element");
		Node node = (Node) list.get(0);
		assertEquals(node.getText(),"A comment for ClassFullAttribute");
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property/column/comment");
		list = xpath.selectNodes(document);
		assertEquals(1, list.size(), "Expected to get one comment element");
		node = (Node) list.get(0);
		assertEquals(node.getText(),"columnd comment");
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testNoComments() throws DocumentException {
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/comment");
		List<?> list = xpath.selectNodes(document);
		assertEquals(list.size(), 0, "Expected to get no comment element");	
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property/column/comment");
		list = xpath.selectNodes(document);
		assertEquals(0, list.size(), "Expected to get no comment element");	
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGlobalSettingsGeneratedAccessAndCascadeDefault()  throws Exception {
		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest");
		hgs.setDefaultAccess("property");
		hgs.setDefaultCascade("none");	
		Exporter gbsExporter = new HbmExporter();
		gbsExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		gbsExporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
		( (HbmExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		// There are 5 attributes because there are non-defaults not set for this test
		assertEquals(5, root.attributeCount(), "Unexpected number of hibernate-mapping elements " );
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.attribute("package").getStringValue(), "Unexpected package name" );
		assertEquals("property", root.attribute("default-access").getStringValue(), "Unexpected access setting" );
		assertEquals("none", root.attribute("default-cascade").getStringValue(), "Unexpected cascade setting" );	
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGlobalSettingsLasyAndAutoImportNonDefault()  throws Exception {
		HibernateMappingGlobalSettings hgs = new HibernateMappingGlobalSettings();
		hgs.setDefaultPackage("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest");
		hgs.setDefaultLazy(false);
		hgs.setAutoImport(false);		
		Exporter gbsExporter = new HbmExporter();
		gbsExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		gbsExporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
		( (HbmExporter)gbsExporter).setGlobalSettings(hgs);
		gbsExporter.start();
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		Element root = document.getRootElement();
		// There are 5 attributes because there are non-defaults not set for this test
		assertEquals(5, root.attributeCount(), "Unexpected number of hibernate-mapping elements " );
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest", root.attribute("package").getStringValue(), "Unexpected package name" );
		assertEquals("false", root.attribute("default-lazy").getStringValue(), "Unexpected access setting" );
		assertEquals("false", root.attribute("auto-import").getStringValue(), "Unexpected cascade setting" );
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testIdGeneratorHasNotArgumentParameters()  throws Exception {
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/BasicGlobals.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and it has no arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator");
		List<?> list = xpath.selectNodes(document);
		assertTrue(list.size() == 1, "Expected to get one generator element");
		Attribute genAtt = ( (Element)list.get(0) ).attribute("class");
		assertEquals("assigned", genAtt.getStringValue(), "Unexpected generator class name" );
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator/param");
		list = xpath.selectNodes(document);
		assertTrue(list.size() == 0, "Expected to get no generator param elements");	
	}
    
	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
    public void testIdGeneratorHasArgumentParameters()  throws Exception {
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/Hbm2HbmXmlTest/Basic.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator");
		List<?> list = xpath.selectNodes(document);
		assertTrue(list.size() == 1, "Expected to get one generator element");
		Attribute genAtt = ( (Element)list.get(0) ).attribute("class");
		assertEquals("org.hibernate.id.TableHiLoGenerator", genAtt.getStringValue(), "Unexpected generator class name" );
		xpath = DocumentHelper.createXPath("//hibernate-mapping/class/id/generator/param");
		list = xpath.selectNodes(document);
		assertEquals(2, list.size(), "Expected to get correct number of generator param elements" );
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
		assertEquals("table", paramTableAtt.getStringValue(), "Unexpected generator param name" );
		assertEquals("column", paramColumnAtt.getStringValue(), "Unexpected generator param name" );
		assertEquals("uni_table", tableElement.getStringValue(), "Unexpected param value for table" );
		assertEquals("next_hi_value", columnElement.getStringValue(), "Unexpected param value for column" );
    }

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGeneralHbmSettingsQuery()  throws Exception {
		File outputXml = new File(
				srcDir,
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/query");
		List<?> list = xpath.selectNodes(document);
		assertEquals(2, list.size(), "Expected to get correct number of query elements" );
		Attribute genAtt = ( (Element)list.get(0) ).attribute("name");
		assertEquals("test_query_1", genAtt.getStringValue(), "Unexpected query name" );
		genAtt = ( (Element)list.get(0) ).attribute("flush-mode");
		assertNull(genAtt, "Expected flush-mode value to be null");
		genAtt = ( (Element)list.get(1) ).attribute("name");
		assertEquals("test_query_2", genAtt.getStringValue(), "Unexpected query name" );
		genAtt = ( (Element)list.get(1) ).attribute("flush-mode");
		assertEquals("auto", genAtt.getStringValue(), "Unexpected flush-mode value" );
		genAtt = ( (Element)list.get(1) ).attribute("cacheable");
		assertEquals("true", genAtt.getStringValue(), "Unexpected cacheable value" );
		genAtt = ( (Element)list.get(1) ).attribute("cache-region");
		assertEquals("myregion", genAtt.getStringValue(), "Unexpected cache-region value" );
		genAtt = ( (Element)list.get(1) ).attribute("fetch-size");
		assertEquals("10", genAtt.getStringValue(), "Unexpected fetch-size value" );
		genAtt = ( (Element)list.get(1) ).attribute("timeout");
		assertEquals("1000", genAtt.getStringValue(), "Unexpected timeout value" );
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGeneralHbmSettingsSQLQueryBasic()  throws Exception {
		File outputXml = new File(
				srcDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query");
		List<?> list = xpath.selectNodes(document);
		assertEquals(6, list.size(), "Expected to get correct number of query elements" );
		xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_1\"]");
		list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_1' not to be null");
		Attribute genAtt = node.attribute("flush-mode");
		assertNull(genAtt, "Expected flush-mode value to be null");
	}
	    
	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGeneralHbmSettingsSQLQueryAllAttributes()  throws Exception {
		File outputXml = new File(
				srcDir,
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_2\"]");
		List<?> list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_2' not to be null");
		Attribute genAtt = node.attribute("name");
		assertEquals("test_sqlquery_2", genAtt.getStringValue(), "Unexpected query name" );
		genAtt = node.attribute("flush-mode");
		assertEquals("auto", genAtt.getStringValue(), "Unexpected flush-mode value" );
		genAtt = node.attribute("cacheable");
		assertEquals("true", genAtt.getStringValue(), "Unexpected cacheable value" );
		genAtt = node.attribute("cache-region");
		assertEquals("myregion", genAtt.getStringValue(), "Unexpected cache-region value" );
		genAtt = node.attribute("fetch-size");
		assertEquals("10", genAtt.getStringValue(), "Unexpected fetch-size value" );
		genAtt = node.attribute("timeout");
		assertEquals("1000", genAtt.getStringValue(), "Unexpected timeout value" );
		Element syncTable = node.element("synchronize");
		assertNull(syncTable, "Expected synchronize element to be null");	
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGeneralHbmSettingsSQLQuerySynchronize()  throws Exception {
		File outputXml = new File(
				srcDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_3\"]");
		List<?> list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_3' not to be null");
		Attribute genAtt = node.attribute("name");
		assertEquals("test_sqlquery_3", genAtt.getStringValue(), "Unexpected query name" );
		Element syncTable = node.element("synchronize");
		assertNotNull(syncTable, "Expected synchronize element to not be null");
		genAtt = syncTable.attribute("table");
		assertEquals("mytable", genAtt.getStringValue(), "Unexpected table value for synchronize element" );
		Element returnEl = node.element("return");
		assertNull(returnEl, "Expected return element to be null");
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnRoot()  throws Exception {
		File outputXml = new File(
				srcDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_4\"]");
		List<?> list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_4' not to be null");
		Attribute genAtt = node.attribute("name");
		assertEquals("test_sqlquery_4", genAtt.getStringValue(), "Unexpected query name" );
		Element returnEl = node.element("return");
		assertNotNull(returnEl, "Expected return element to not be null");
		genAtt = returnEl.attribute("alias");
		assertEquals("e", genAtt.getStringValue(), "Unexpected alias value for return element" );
		genAtt = returnEl.attribute("class");
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.BasicGlobals", genAtt.getStringValue(), "Unexpected class value for return element");
	}

	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnRole()  throws Exception {
		File outputXml = new File(
				srcDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_5\"]");
		List<?> list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_5' not to be null");
		Attribute genAtt = node.attribute("name");
		assertEquals("test_sqlquery_5", genAtt.getStringValue(), "Unexpected query name" );
		Element returnEl = node.element("return-join");
		assertNotNull(returnEl, "Expected return element to not be null");
		genAtt = returnEl.attribute("alias");
		assertEquals("e", genAtt.getStringValue(), "Unexpected alias value for return element" );
		genAtt = returnEl.attribute("property");
		assertEquals("e.age", genAtt.getStringValue(), "Unexpected property role value for return element");
	}
	    
	// TODO HBX-2042: Reenable when implemented in ORM 6.0
	@Disabled
	@Test
	public void testGeneralHbmSettingsSQLQueryWithReturnCollection()  throws Exception {
		File outputXml = new File(
				srcDir, 
				"GeneralHbmSettings.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		Document document = xmlReader.read(outputXml);
		// Validate the Generator and that it does have arguments 
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/sql-query[@name=\"test_sqlquery_6\"]");
		List<?> list = xpath.selectNodes(document);
		Element node = (Element)list.get(0);
		assertNotNull(node, "Expected sql-query named 'test_sqlquery_6' not to be null");
		Attribute genAtt = node.attribute("name");
		assertEquals("test_sqlquery_6", genAtt.getStringValue(), "Unexpected query name");
		Element returnEl = node.element("load-collection");
		assertNotNull(returnEl, "Expected return element to not be null");
		genAtt = returnEl.attribute("alias");
		assertEquals("e", genAtt.getStringValue(), "Unexpected alias value for return element");
		genAtt = returnEl.attribute("role");
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.Hbm2HbmXmlTest.BasicGlobals.price", genAtt.getStringValue(), "Unexpected collection role value for return element");
		genAtt = returnEl.attribute("lock-mode");
		assertEquals("none", genAtt.getStringValue(), "Unexpected class lock-mode for return element");
	}
	
}
