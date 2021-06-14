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
package org.hibernate.tool.hbm2x.hbm2hbmxml.TypeParamsTest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
			"Order.hbm.xml",
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
		JUnitUtil.assertIsNonEmptyFile(
				new File(
						outputDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/TypeParamsTest/Order.hbm.xml"));
	}

	@Test
	public void testReadable() {
		File orderHbmXml =
        		new File(
        				outputDir, 
        				"org/hibernate/tool/hbm2x/hbm2hbmxml/TypeParamsTest/Order.hbm.xml");
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		File[] files = new File[] { orderHbmXml };
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files, properties);
        Assert.assertNotNull(metadataDescriptor.createMetadata());
    }

	@Test
	public void testTypeParamsElements() throws Exception {
		File outputXml = new File(
				outputDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/TypeParamsTest/Order.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/property")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one property element", 2, nodeList.getLength());
		Element statusElement = (Element) nodeList.item(0);
		Element nameElement = (Element) nodeList.item(1);
		if(!statusElement.getAttribute( "name" ).equals("status")) {
			Element temp = nameElement;
			nameElement = statusElement;
			statusElement = temp;
		}
		Assert.assertEquals(statusElement.getAttribute( "name" ),"status");
		nodeList = statusElement.getElementsByTagName("type");
		Assert.assertEquals("Expected to get one type element", 1, nodeList.getLength());
		nodeList =  ((Element) nodeList.item(0)).getElementsByTagName("param");		
		Assert.assertEquals("Expected to get 5 params elements", nodeList.getLength(), 5);
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element param = (Element) nodeList.item(i);
			params.put(param.getAttribute( "name" ), param.getTextContent());
		}
		Set<String> set = params.keySet();
		Assert.assertEquals("Expected to get 5 different params elements", params.size(), 5);
		Assert.assertTrue(
				"Can't find 'catalog' param", 
				set.contains("catalog"));
		Assert.assertEquals("", params.get("catalog"));
		Assert.assertTrue(
				"Can't find 'column' param", 
				set.contains("column"));
		Assert.assertEquals("STATUS", params.get("column"));
		Assert.assertTrue(
				"Can't find 'table' param", 
				set.contains("table"));
		Assert.assertEquals("ORDERS", params.get("table"));
		Assert.assertTrue(
				"Can't find 'schema' param", 
				set.contains("schema"));
		Assert.assertEquals("", params.get("schema"));
		Assert.assertTrue(
				"Can't find 'enumClass' param", 
				set.contains("enumClass"));
		Assert.assertEquals(
				"org.hibernate.tool.hbm2x.hbm2hbmxml.Order$Status", 
				params.get("enumClass"));
		Assert.assertTrue("property name should not have any type element",nameElement.getElementsByTagName("type").getLength() == 0);
		Assert.assertEquals(nameElement.getAttribute("type"), "string");
	}

}
