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
package org.hibernate.tool.hbm2x.hbm2hbmxml.SetElementTest;

import java.io.File;
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
			"Search.hbm.xml",
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
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataDescriptor(metadataDescriptor);
		hbmexporter.setOutputDirectory(outputDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(
				new File(
						outputDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/SetElementTest/Search.hbm.xml"));
	}

	@Test
	public void testReadable() {
        File searchHbmXml =	new File(
						outputDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/SetElementTest/Search.hbm.xml");
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		File[] files = new File[] { searchHbmXml };
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files, properties);
        Assert.assertNotNull(metadataDescriptor.createMetadata());
    }

	@Test
	public void testKey() throws Exception {
		File outputXml = 
				new File(
						outputDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/SetElementTest/Search.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/set/key")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one key element", 1, nodeList.getLength());
		Element node = (Element)nodeList.item(0);
		if (node.getAttribute( "column" ) != null && !"".equals(node.getAttribute("column"))) {//implied attribute
			Assert.assertEquals(node.getAttribute( "column" ),"searchString");
		} else {
			node = (Element)node.getElementsByTagName("column").item(0);
			Assert.assertEquals(node.getAttribute( "name" ),"searchString");
		}
	}

	@Test
	public void testSetElement() throws Exception {
		File outputXml = 
				new File(
						outputDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/SetElementTest/Search.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/set")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one set element", 1, nodeList.getLength());
		Element node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "name" ),"searchResults");
		Assert.assertEquals(node.getAttribute( "access" ),"field");
		nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/set/element")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one element 'element'", 1, nodeList.getLength());
		node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "type" ), "string");
		nodeList = node.getElementsByTagName("column");
		Assert.assertEquals("Expected to get one element 'column'", 1, nodeList.getLength());
		node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "name" ), "text");
	}

}
