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
package org.hibernate.tool.hbm2x.hbm2hbmxml.ListArrayTest;

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
			"Glarch.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private File resourcesDir = null;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataDescriptor(metadataDescriptor);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Fee.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Glarch.hbm.xml") );
	}

	@Test
	public void testReadable() {
        ArrayList<File> files = new ArrayList<File>(4); 
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Fee.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Glarch.hbm.xml"));
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files.toArray(new File[2]), properties);
        Assert.assertNotNull(metadataDescriptor.createMetadata());
    }

	@Test
	public void testListNode() throws Exception {
		File outputXml = new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Glarch.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/list")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get two list element", 2, nodeList.getLength());
		Element node = (Element) nodeList.item(1); //second list
		Assert.assertEquals("fooComponents", node.getAttribute( "name" ));
		Assert.assertEquals("true", node.getAttribute( "lazy" ));
		Assert.assertEquals("all", node.getAttribute( "cascade" ));
		nodeList = node.getElementsByTagName("list-index");
		Assert.assertEquals("Expected to get one list-index element", 1, nodeList.getLength());
		nodeList = ((Element) nodeList.item(0)).getElementsByTagName("column");
		Assert.assertEquals("Expected to get one column element", 1, nodeList.getLength());
		node = (Element) nodeList.item(0);
		Assert.assertEquals("tha_indecks", node.getAttribute( "name" ));
		node = (Element)node.getParentNode().getParentNode();//list
		nodeList = node.getElementsByTagName("composite-element");
		Assert.assertEquals("Expected to get one composite-element element", 1, nodeList.getLength());
		node = (Element) nodeList.item(0);
		int propertyCount = 0;
		nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if ("property".equals(nodeList.item(i).getNodeName())) propertyCount++;
		}
		Assert.assertEquals("Expected to get two property element", 2, propertyCount);
		node = (Element)node.getElementsByTagName("many-to-one").item(0);
		Assert.assertEquals("fee", node.getAttribute( "name" ));
		Assert.assertEquals("all", node.getAttribute( "cascade" ));
		//TODO :assertEquals(node.attribute( "outer-join" ).getText(),"true");
		node = (Element)node.getParentNode();//composite-element
		node = (Element)node.getElementsByTagName("nested-composite-element").item(0);
		Assert.assertEquals("subcomponent", node.getAttribute( "name" ));
		Assert.assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.ListArrayTest.FooComponent", node.getAttribute( "class" ));
	}

	@Test
	public void testArrayNode() throws Exception {
		File outputXml = new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Glarch.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/array")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one array element", 1, nodeList.getLength());
		Element node = (Element) nodeList.item(0);
		Assert.assertEquals("proxyArray", node.getAttribute( "name" ));
		Assert.assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.ListArrayTest.GlarchProxy", node.getAttribute( "element-class" ));
		nodeList = node.getElementsByTagName("list-index");		
		Assert.assertEquals("Expected to get one list-index element", 1, nodeList.getLength());
		nodeList = ((Element) nodeList.item(0)).getElementsByTagName("column");
		Assert.assertEquals("Expected to get one column element", 1, nodeList.getLength());
		node = (Element) nodeList.item(0);
		Assert.assertEquals("array_indecks", node.getAttribute( "name" ));
		node = (Element)node.getParentNode().getParentNode();//array
		nodeList = node.getElementsByTagName("one-to-many");
		Assert.assertEquals("Expected to get one 'one-to-many' element", 1, nodeList.getLength());
	}

}
