/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml.MapAndAnyTest;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.util.MetadataHelper;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Dmitry Geraskov
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Properties.hbm.xml",
			"Person.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private Exporter hbmexporter = null;
	private File outputDir = null;
	private Metadata metadata = null;

	@Before
	public void setUp() throws Exception {
		metadata = 
				HibernateUtil.initializeMetadata(this, HBM_XML_FILES);
		outputDir = temporaryFolder.getRoot();
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadata(metadata);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/ComplexPropertyValue.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/IntegerPropertyValue.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/StringPropertyValue.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml") );
	}

	@Test
	public void testReadable() {
        Configuration cfg = new Configuration();
        cfg.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
        cfg.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/ComplexPropertyValue.hbm.xml"));
        cfg.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/IntegerPropertyValue.hbm.xml"));
        cfg.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/StringPropertyValue.hbm.xml"));
        cfg.addFile(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml"));
        Assert.assertNotNull(MetadataHelper.getMetadata(cfg));
    }

	@Test
	public void testAnyNode() throws DocumentException {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/any");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one any element", 1, list.size());
		Element node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "name" ).getText(),"someSpecificProperty");
		Assert.assertEquals(node.attribute( "id-type" ).getText(),"long");
		Assert.assertEquals(node.attribute( "meta-type" ).getText(),"string");
		Assert.assertEquals(node.attribute( "cascade" ).getText(), "all");
		Assert.assertEquals(node.attribute( "access" ).getText(), "field");
		list = node.elements("column");
		Assert.assertEquals("Expected to get two column elements", 2, list.size());
		list = node.elements("meta-value");
		Assert.assertEquals("Expected to get three meta-value elements", 3, list.size());
		node = (Element) list.get(0);
		String className = node.attribute( "class" ).getText();
		Assert.assertNotNull("Expected class attribute in meta-value", className);
		if (className.indexOf("IntegerPropertyValue") > 0){
			Assert.assertEquals(node.attribute( "value" ).getText(),"I");
		} else if (className.indexOf("StringPropertyValue") > 0){
			Assert.assertEquals(node.attribute( "value" ).getText(),"S");
		} else {
			Assert.assertTrue(className.indexOf("ComplexPropertyValue") > 0);
			Assert.assertEquals(node.attribute( "value" ).getText(),"C");
		}
	}

	@Test
	public void testMetaValueRead() throws Exception{
		PersistentClass pc = metadata.getEntityBinding("org.hibernate.tool.hbm2x.hbm2hbmxml.MapAndAnyTest.Person");
		Assert.assertNotNull(pc);
		Property prop = pc.getProperty("data");
		Assert.assertNotNull(prop);
		Assert.assertTrue(prop.getValue() instanceof Any);
		Any any = (Any) prop.getValue();
		Assert.assertTrue("Expected to get one meta-value element", any.getMetaValues() != null);
		Assert.assertEquals("Expected to get one meta-value element", 1, any.getMetaValues().size());
	}

	@Test
	public void testMapManyToAny() throws DocumentException {
		File outputXml = new File(outputDir,  "org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/map");
		List<?> list = xpath.selectNodes(document);
		Assert.assertEquals("Expected to get one any element", 1, list.size());
		Element node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "name" ).getText(),"generalProperties");
		Assert.assertEquals(node.attribute( "table" ).getText(),"T_GEN_PROPS");
		Assert.assertEquals(node.attribute( "lazy" ).getText(),"true");
		Assert.assertEquals(node.attribute( "cascade" ).getText(), "all");
		Assert.assertEquals(node.attribute( "access" ).getText(), "field");
		list = node.elements("key");
		Assert.assertEquals("Expected to get one key element", 1, list.size());
		list = node.elements("map-key");
		Assert.assertEquals("Expected to get one map-key element", 1, list.size());
		node = (Element) list.get(0);
		Assert.assertEquals(node.attribute( "type" ).getText(),"string");
		list = node.elements("column");
		Assert.assertEquals("Expected to get one column element", 1, list.size());
		node = node.getParent();//map
		list = node.elements("many-to-any");
		Assert.assertEquals("Expected to get one many-to-any element", 1, list.size());
		node = (Element) list.get(0);
		list = node.elements("column");
		Assert.assertEquals("Expected to get two column elements", 2, list.size());
		list = node.elements("meta-value");
		Assert.assertEquals("Expected to get two meta-value elements", 2, list.size());
		node = (Element) list.get(0);
		String className = node.attribute( "class" ).getText();
		Assert.assertNotNull("Expected class attribute in meta-value", className);
		if (className.indexOf("IntegerPropertyValue") > 0){
			Assert.assertEquals(node.attribute( "value" ).getText(),"I");
		} else {
			Assert.assertTrue(className.indexOf("StringPropertyValue") > 0);
			Assert.assertEquals(node.attribute( "value" ).getText(),"S");
		}
	}

}
