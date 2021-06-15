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
package org.hibernate.tool.hbm2x.hbm2hbmxml.IdBagTest;

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
			"UserGroup.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private File resourcesDir = null;
	
	private Exporter hbmexporter = null;

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
		Assert.assertFalse(new File(
				outputDir,
				"/GeneralHbmSettings.hbm.xml").exists());
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/User.hbm.xml"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/Group.hbm.xml"));		
	}
	
	@Test
	public void testArtifactCollection() {
		Assert.assertEquals(
				2,
				hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
	}
	
	@Test
	public void testReadable() {
        ArrayList<File> files = new ArrayList<File>(4); 
        files.add(new File(
        		outputDir, 
        		"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/User.hbm.xml"));
        files.add(new File(
        		outputDir, 
        		"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/Group.hbm.xml"));
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files.toArray(new File[2]), properties);
        Assert.assertNotNull(metadataDescriptor.createMetadata());
    }
	
	@Test
	public void testIdBagAttributes() throws Exception {
		File outputXml = new File(
				outputDir,  
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/User.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/idbag")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one idbag element", 1, nodeList.getLength());
		Element node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "table" ),"`UserGroups`");
		Assert.assertEquals(node.getAttribute( "name" ),"groups");
		Assert.assertEquals(node.getAttribute( "lazy" ),"false");
		Assert.assertEquals(node.getAttribute( "access" ),"field");
	}
	
	@Test
	public void testCollectionId() throws Exception {
		File outputXml = new File(
				outputDir,  
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/User.hbm.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/idbag/collection-id")
				.evaluate(document, XPathConstants.NODESET);
		Assert.assertEquals("Expected to get one collection-id element", 1, nodeList.getLength());
		Element node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "column" ),"userGroupId");
		Assert.assertEquals(node.getAttribute( "type" ),"long");
		nodeList = node.getElementsByTagName("generator");
		Assert.assertEquals("Expected to get one generator element", 1, nodeList.getLength());
		node = (Element) nodeList.item(0);
		Assert.assertEquals(node.getAttribute( "class" ),"increment");
	}
	
}
