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
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
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
	private File resourcesDir = null;
	private Metadata metadata = null;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		metadata = metadataDescriptor.createMetadata();
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataDescriptor(metadataDescriptor);
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
        ArrayList<File> files = new ArrayList<File>(4); 
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/ComplexPropertyValue.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/IntegerPropertyValue.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/StringPropertyValue.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml"));
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		properties.setProperty(AvailableSettings.CONNECTION_PROVIDER, HibernateUtil.ConnectionProvider.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files.toArray(new File[4]), properties);
        Assert.assertNotNull(metadataDescriptor.createMetadata());
    }

	@Test
	public void testAnyNode() throws Exception {
		File outputXml = new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/any")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one any element", 1, nodeList.getLength());
		Element node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "name" ),"someSpecificProperty");
		Assert.assertEquals(node.getAttribute( "id-type" ),"long");
		Assert.assertEquals(node.getAttribute( "meta-type" ),"string");
		Assert.assertEquals(node.getAttribute( "cascade" ), "all");
		Assert.assertEquals(node.getAttribute( "access" ), "field");
		nodeList = node.getElementsByTagName("column");
		Assert.assertEquals("Expected to get two column elements", 2, nodeList.getLength());
		nodeList = node.getElementsByTagName("meta-value");
		Assert.assertEquals("Expected to get three meta-value elements", 3, nodeList.getLength());
		node = (Element) nodeList.item(0);
		String className = node.getAttribute( "class" );
		Assert.assertNotNull("Expected class attribute in meta-value", className);
		if (className.indexOf("IntegerPropertyValue") > 0){
			Assert.assertEquals(node.getAttribute( "value" ),"I");
		} else if (className.indexOf("StringPropertyValue") > 0){
			Assert.assertEquals(node.getAttribute( "value" ),"S");
		} else {
			Assert.assertTrue(className.indexOf("ComplexPropertyValue") > 0);
			Assert.assertEquals(node.getAttribute( "value" ),"C");
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
	public void testMapManyToAny() throws Exception {
		File outputXml = new File(outputDir,  "org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/map")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one any element", 1, nodeList.getLength());
		Element node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "name" ),"generalProperties");
		Assert.assertEquals(node.getAttribute( "table" ),"T_GEN_PROPS");
		Assert.assertEquals(node.getAttribute( "lazy" ),"true");
		Assert.assertEquals(node.getAttribute( "cascade" ), "all");
		Assert.assertEquals(node.getAttribute( "access" ), "field");
		nodeList = node.getElementsByTagName("key");
		Assert.assertEquals("Expected to get one key element", 1, nodeList.getLength());
		nodeList = node.getElementsByTagName("map-key");
		Assert.assertEquals("Expected to get one map-key element", 1, nodeList.getLength());
		node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "type" ),"string");
		nodeList = node.getElementsByTagName("column");
		Assert.assertEquals("Expected to get one column element", 1, nodeList.getLength());
		node = (Element)node.getParentNode();//map
		nodeList = node.getElementsByTagName("many-to-any");
		Assert.assertEquals("Expected to get one many-to-any element", 1, nodeList.getLength());
		node = (Element) nodeList.item(0);
		nodeList = node.getElementsByTagName("column");
		Assert.assertEquals("Expected to get two column elements", 2, nodeList.getLength());
		nodeList = node.getElementsByTagName("meta-value");
		Assert.assertEquals("Expected to get two meta-value elements", 2, nodeList.getLength());
		node = (Element) nodeList.item(0);
		String className = node.getAttribute( "class" );
		Assert.assertNotNull("Expected class attribute in meta-value", className);
		if (className.indexOf("IntegerPropertyValue") > 0){
			Assert.assertEquals(node.getAttribute( "value" ),"I");
		} else {
			Assert.assertTrue(className.indexOf("StringPropertyValue") > 0);
			Assert.assertEquals(node.getAttribute( "value" ),"S");
		}
	}

}
