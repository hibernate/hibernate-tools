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
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
		srcDir = new File(outputFolder, "src");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter hbmexporter = new HbmExporter();
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
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
	public void testListNode() throws DocumentException {
		File outputXml = new File(
				srcDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Glarch.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/list");
		List<?> list = xpath.selectNodes(document);
		assertEquals(2, list.size(), "Expected to get two list element");
		Element node = (Element) list.get(1); //second list
		assertEquals("fooComponents", node.attribute( "name" ).getText());
		assertEquals("true", node.attribute( "lazy" ).getText());
		assertEquals("all", node.attribute( "cascade" ).getText());
		list = node.elements("list-index");
		assertEquals(1, list.size(), "Expected to get one list-index element");
		list = ((Element) list.get(0)).elements("column");
		assertEquals(1, list.size(), "Expected to get one column element");
		node = (Element) list.get(0);
		assertEquals("tha_indecks", node.attribute( "name" ).getText());
		node = node.getParent().getParent();//list
		list = node.elements("composite-element");
		assertEquals(1, list.size(), "Expected to get one composite-element element");
		node = (Element) list.get(0);
		assertEquals(2, node.elements("property").size(), "Expected to get two property element");
		node = node.element("many-to-one");
		assertEquals("fee", node.attribute( "name" ).getText());
		assertEquals("all", node.attribute( "cascade" ).getText());
		//TODO :assertEquals(node.attribute( "outer-join" ).getText(),"true");
		node = node.getParent();//composite-element
		node = node.element("nested-composite-element");
		assertEquals("subcomponent", node.attribute( "name" ).getText());
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.ListArrayTest.FooComponent", node.attribute( "class" ).getText());
	}

	@Test
	public void testArrayNode() throws DocumentException {
		File outputXml = new File(
				srcDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/ListArrayTest/Glarch.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/array");
		List<?> list = xpath.selectNodes(document);
		assertEquals(1, list.size(), "Expected to get one array element");
		Element node = (Element) list.get(0);
		assertEquals("proxyArray", node.attribute( "name" ).getText());
		assertEquals("org.hibernate.tool.hbm2x.hbm2hbmxml.ListArrayTest.GlarchProxy", node.attribute( "element-class" ).getText());
		list = node.elements("list-index");		
		assertEquals(1, list.size(), "Expected to get one list-index element");
		list = ((Element) list.get(0)).elements("column");
		assertEquals(1, list.size(), "Expected to get one column element");
		node = (Element) list.get(0);
		assertEquals("array_indecks", node.attribute( "name" ).getText());
		node = node.getParent().getParent();//array
		list = node.elements("one-to-many");
		assertEquals(1, list.size(), "Expected to get one 'one-to-many' element");
	}

}
