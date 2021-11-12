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

package org.hibernate.tool.hbm2x.hbm2hbmxml.ListArrayTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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
	
	@TempDir
	public File outputFolder = new File("output");
	
	private File srcDir = null;
	private File resourcesDir = null;

	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "output");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataDescriptor(metadataDescriptor);
		hbmexporter.setOutputDirectory(srcDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Fee.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Glarch.hbm.xml") );
	}

	@Test
	public void testReadable() {
        ArrayList<File> files = new ArrayList<File>(4); 
        files.add(new File(
        		srcDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Fee.hbm.xml"));
        files.add(new File(
        		srcDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Glarch.hbm.xml"));
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files.toArray(new File[2]), properties);
        assertNotNull(metadataDescriptor.createMetadata());
    }

	@Test
	public void testListNode() throws Exception {
		File outputXml = new File(
				srcDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Glarch.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/list")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(2, nodeList.getLength(), "Expected to get two list element");
		Element node = (Element) nodeList.item(1); //second list
		assertEquals("fooComponents", node.getAttribute( "name" ));
		assertEquals("true", node.getAttribute( "lazy" ));
		assertEquals("all", node.getAttribute( "cascade" ));
		nodeList = node.getElementsByTagName("list-index");
		assertEquals(1, nodeList.getLength(), "Expected to get one list-index element");
		nodeList = ((Element) nodeList.item(0)).getElementsByTagName("column");
		assertEquals(1, nodeList.getLength(), "Expected to get one column element");
		node = (Element) nodeList.item(0);
		assertEquals("tha_indecks", node.getAttribute( "name" ));
		node = (Element)node.getParentNode().getParentNode();//list
		nodeList = node.getElementsByTagName("composite-element");
		assertEquals(1, nodeList.getLength(), "Expected to get one composite-element element");
		node = (Element) nodeList.item(0);
		int propertyCount = 0;
		nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if ("property".equals(nodeList.item(i).getNodeName())) propertyCount++;
		}
		assertEquals(2, propertyCount, "Expected to get two property element");
		node = (Element)node.getElementsByTagName("many-to-one").item(0);
		assertEquals("fee", node.getAttribute( "name" ));
		assertEquals("all", node.getAttribute( "cascade" ));
		//TODO :assertEquals(node.attribute( "outer-join" ).getText(),"true");
		node = (Element)node.getParentNode();//composite-element
		node = (Element)node.getElementsByTagName("nested-composite-element").item(0);
		assertEquals("subcomponent", node.getAttribute( "name" ));
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.ListArrayTest.FooComponent", node.getAttribute( "class" ));
	}

	@Test
	public void testArrayNode() throws Exception {
		File outputXml = new File(
				srcDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Glarch.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/array")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(1, nodeList.getLength(), "Expected to get one array element");
		Element node = (Element) nodeList.item(0);
		assertEquals("proxyArray", node.getAttribute( "name" ));
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.ListArrayTest.GlarchProxy", node.getAttribute( "element-class" ));
		nodeList = node.getElementsByTagName("list-index");		
		assertEquals(1, nodeList.getLength(), "Expected to get one list-index element");
		nodeList = ((Element) nodeList.item(0)).getElementsByTagName("column");
		assertEquals(1, nodeList.getLength(), "Expected to get one column element");
		node = (Element) nodeList.item(0);
		assertEquals("array_indecks", node.getAttribute( "name" ));
		node = (Element)node.getParentNode().getParentNode();//array
		nodeList = node.getElementsByTagName("one-to-many");
		assertEquals(1, nodeList.getLength(), "Expected to get one 'one-to-many' element");
	}

}
