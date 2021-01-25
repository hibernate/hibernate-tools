/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2020 Red Hat, Inc.
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

package org.hibernate.tool.hbm2x.hbm2hbmxml.MapAndAnyTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
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
			"Properties.hbm.xml",
			"Person.hbm.xml"
	};
	
	@TempDir
	public File outputFolder = new File("output");
	
	private Exporter hbmexporter = null;
	private File srcDir = null;
	private File resourcesDir = null;
	private Metadata metadata = null;

	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "src");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		metadata = metadataDescriptor.createMetadata();
		hbmexporter = new HbmExporter();
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
		hbmexporter.start();
	}

	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/ComplexPropertyValue.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/IntegerPropertyValue.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/StringPropertyValue.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml") );
	}

	@Test
	public void testReadable() {
        ArrayList<File> files = new ArrayList<File>(4); 
        files.add(new File(
        		srcDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/ComplexPropertyValue.hbm.xml"));
        files.add(new File(
        		srcDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/IntegerPropertyValue.hbm.xml"));
        files.add(new File(
        		srcDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/StringPropertyValue.hbm.xml"));
        files.add(new File(
        		srcDir, 
        		"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml"));
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files.toArray(new File[4]), properties);
        assertNotNull(metadataDescriptor.createMetadata());
    }

	@Test
	public void testAnyNode() throws DocumentException {
		File outputXml = new File(
				srcDir,
				"org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/any");
		List<?> list = xpath.selectNodes(document);
		assertEquals(1, list.size(), "Expected to get one any element");
		Element node = (Element) list.get(0);
		assertEquals(node.attribute( "name" ).getText(),"someSpecificProperty");
		assertEquals(node.attribute( "id-type" ).getText(),"long");
		assertEquals(node.attribute( "meta-type" ).getText(),"string");
		assertEquals(node.attribute( "cascade" ).getText(), "all");
		assertEquals(node.attribute( "access" ).getText(), "field");
		list = node.elements("column");
		assertEquals(2, list.size(), "Expected to get two column elements");
		list = node.elements("meta-value");
		assertEquals(3, list.size(), "Expected to get three meta-value elements");
		node = (Element) list.get(0);
		String className = node.attribute( "class" ).getText();
		assertNotNull(className, "Expected class attribute in meta-value");
		if (className.indexOf("IntegerPropertyValue") > 0){
			assertEquals(node.attribute( "value" ).getText(),"I");
		} else if (className.indexOf("StringPropertyValue") > 0){
			assertEquals(node.attribute( "value" ).getText(),"S");
		} else {
			assertTrue(className.indexOf("ComplexPropertyValue") > 0);
			assertEquals(node.attribute( "value" ).getText(),"C");
		}
	}

	@Test
	public void testMetaValueRead() throws Exception{
		PersistentClass pc = metadata.getEntityBinding("org.hibernate.tool.hbm2x.hbm2hbmxml.MapAndAnyTest.Person");
		assertNotNull(pc);
		Property prop = pc.getProperty("data");
		assertNotNull(prop);
		assertTrue(prop.getValue() instanceof Any);
		Any any = (Any) prop.getValue();
		assertTrue(any.getMetaValues() != null, "Expected to get one meta-value element");
		assertEquals(1, any.getMetaValues().size(), "Expected to get one meta-value element");
	}

	@Test
	public void testMapManyToAny() throws DocumentException {
		File outputXml = new File(srcDir,  "org/hibernate/tool/hbm2x/hbm2hbmxml/MapAndAnyTest/PropertySet.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/map");
		List<?> list = xpath.selectNodes(document);
		assertEquals(1, list.size(), "Expected to get one any element");
		Element node = (Element) list.get(0);
		assertEquals(node.attribute( "name" ).getText(),"generalProperties");
		assertEquals(node.attribute( "table" ).getText(),"T_GEN_PROPS");
		assertEquals(node.attribute( "lazy" ).getText(),"true");
		assertEquals(node.attribute( "cascade" ).getText(), "all");
		assertEquals(node.attribute( "access" ).getText(), "field");
		list = node.elements("key");
		assertEquals(1, list.size(), "Expected to get one key element");
		list = node.elements("map-key");
		assertEquals(1, list.size(), "Expected to get one map-key element");
		node = (Element) list.get(0);
		assertEquals(node.attribute( "type" ).getText(),"string");
		list = node.elements("column");
		assertEquals(1, list.size(), "Expected to get one column element");
		node = node.getParent();//map
		list = node.elements("many-to-any");
		assertEquals(1, list.size(), "Expected to get one many-to-any element");
		node = (Element) list.get(0);
		list = node.elements("column");
		assertEquals(2, list.size(), "Expected to get two column elements");
		list = node.elements("meta-value");
		assertEquals(2, list.size(), "Expected to get two meta-value elements");
		node = (Element) list.get(0);
		String className = node.attribute( "class" ).getText();
		assertNotNull(className, "Expected class attribute in meta-value");
		if (className.indexOf("IntegerPropertyValue") > 0){
			assertEquals(node.attribute( "value" ).getText(),"I");
		} else {
			assertTrue(className.indexOf("StringPropertyValue") > 0);
			assertEquals(node.attribute( "value" ).getText(),"S");
		}
	}

}
