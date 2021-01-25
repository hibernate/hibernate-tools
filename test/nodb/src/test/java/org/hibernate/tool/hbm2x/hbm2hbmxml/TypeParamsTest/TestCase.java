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

package org.hibernate.tool.hbm2x.hbm2hbmxml.TypeParamsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author Dmitry Geraskov
 * @author koen
 */
public class TestCase {
	
	private static final String[] HBM_XML_FILES = new String[] {
			"Order.hbm.xml",
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
		Exporter hbmexporter = new HbmExporter();
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
		hbmexporter.start();
	}

	// TODO HBX-2062: Investigate and reenable
	@Disabled
	@Test
	public void testAllFilesExistence() {
		JUnitUtil.assertIsNonEmptyFile(
				new File(
						srcDir,  
						"org/hibernate/tool/hbm2x/hbm2hbmxml/TypeParamsTest/Order.hbm.xml"));
	}

	// TODO HBX-2062: Investigate and reenable
	@Disabled
	@Test
	public void testReadable() {
		File orderHbmXml =
        		new File(
        				srcDir, 
        				"org/hibernate/tool/hbm2x/hbm2hbmxml/TypeParamsTest/Order.hbm.xml");
		Properties properties = new Properties();
		properties.setProperty(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		File[] files = new File[] { orderHbmXml };
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, files, properties);
        assertNotNull(metadataDescriptor.createMetadata());
    }

	// TODO HBX-2062: Investigate and reenable
	@Disabled
	@Test
	public void testTypeParamsElements() throws DocumentException {
		File outputXml = new File(
				srcDir,  
				"org/hibernate/tool/hbm2x/hbm2hbmxml/TypeParamsTest/Order.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
		SAXReader xmlReader =  new SAXReader();
		xmlReader.setValidation(true);
		Document document = xmlReader.read(outputXml);
		XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/property");
		List<?> list = xpath.selectNodes(document);
		assertEquals(2, list.size(), "Expected to get one property element");
		Element statusElement = (Element) list.get(0);
		Element nameElement = (Element) list.get(1);
		if(!statusElement.attribute( "name" ).getText().equals("status")) {
			Element temp = nameElement;
			nameElement = statusElement;
			statusElement = temp;
		}
		assertEquals(statusElement.attribute( "name" ).getText(),"status");
		list = statusElement.elements("type");
		assertEquals(1, list.size(), "Expected to get one type element");
		list =  ((Element) list.get(0)).elements("param");		
		assertEquals(list.size(), 5, "Expected to get 5 params elements");
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < list.size(); i++) {
			Element param = (Element) list.get(i);
			params.put(param.attribute( "name" ).getText(), param.getText());
		}
		Set<String> set = params.keySet();
		assertEquals(params.size(), 5, "Expected to get 5 different params elements");
		assertTrue(
				set.contains("catalog"),
				"Can't find 'catalog' param");
		assertEquals("", params.get("catalog"));
		assertTrue(
				set.contains("column"),
				"Can't find 'column' param");
		assertEquals("STATUS", params.get("column"));
		assertTrue(
				set.contains("table"),
				"Can't find 'table' param");
		assertEquals("ORDERS", params.get("table"));
		assertTrue(
				set.contains("schema"),
				"Can't find 'schema' param");
		assertEquals("", params.get("schema"));
		assertTrue(
				set.contains("enumClass"),
				"Can't find 'enumClass' param");
		assertEquals(
				"org.hibernate.tool.hbm2x.hbm2hbmxml.Order$Status", 
				params.get("enumClass"));
		assertTrue(nameElement.elements("type").isEmpty(), "property name should not have any type element");
		assertEquals(nameElement.attribute("type").getText(), "string");
	}

}
