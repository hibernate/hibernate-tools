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

package org.hibernate.tool.hbm2x.hbm2hbmxml.IdBagTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
			"UserGroup.hbm.xml"
	};
	
	@TempDir
	public File outputFolder = new File("output");
	
	private File srcDir = null;
	private File resourcesDir = null;
	
	private Exporter hbmexporter = null;

	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "output");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		hbmexporter = new HibernateMappingExporter();
		hbmexporter.setMetadataDescriptor(metadataDescriptor);
		hbmexporter.setOutputDirectory(srcDir);
		hbmexporter.start();
	}
	
	@Test
	public void testAllFilesExistence() {
		assertFalse(new File(
				srcDir,
				"/GeneralHbmSettings.hbm.xml").exists());
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir,
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/User.hbm.xml"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir,
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/Group.hbm.xml"));		
	}
	
	@Test
	public void testArtifactCollection() {
		assertEquals(
				2,
				hbmexporter.getArtifactCollector().getFileCount("hbm.xml"));
	}
	
	@Test
	public void testReadable() {
        ArrayList<File> files = new ArrayList<File>(4); 
        files.add(new File(
        		srcDir, 
        		"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/User.hbm.xml"));
        files.add(new File(
        		srcDir, 
        		"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/Group.hbm.xml"));
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files.toArray(new File[2]), properties);
        assertNotNull(metadataDescriptor.createMetadata());
    }
	
	@Test
	public void testIdBagAttributes() throws Exception {
		File outputXml = new File(
				srcDir,  
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/User.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/idbag")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(1, nodeList.getLength(), "Expected to get one idbag element");
		Element node = (Element) nodeList.item(0);
		assertEquals(node.getAttribute( "table" ),"`UserGroups`");
		assertEquals(node.getAttribute( "name" ),"groups");
		assertEquals(node.getAttribute( "lazy" ),"false");
		assertEquals(node.getAttribute( "access" ),"field");
	}
	
	@Test
	public void testCollectionId() throws Exception {
		File outputXml = new File(
				srcDir,  
				"/org/hibernate/tool/hbm2x/hbm2hbmxml/IdBagTest/User.hbm.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(outputXml);
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList)xpath
				.compile("//hibernate-mapping/class/idbag/collection-id")
				.evaluate(document, XPathConstants.NODESET);
		assertEquals(1, nodeList.getLength(), "Expected to get one collection-id element");
		Element node = (Element) nodeList.item(0);
		assertEquals(node.getAttribute( "column" ),"userGroupId");
		assertEquals(node.getAttribute( "type" ),"long");
		nodeList = node.getElementsByTagName("generator");
		assertEquals(1, nodeList.getLength(), "Expected to get one generator element");
		node = (Element) nodeList.item(0);
		assertEquals(node.getAttribute( "class" ),"increment");
	}
	
}
