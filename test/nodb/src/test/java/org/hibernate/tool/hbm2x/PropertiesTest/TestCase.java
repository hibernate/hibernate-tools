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

package org.hibernate.tool.hbm2x.PropertiesTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author Josh Moore josh.moore@gmx.de
 * @author koen
 */
public class TestCase {
	
	private static final String[] HBM_XML_FILES = new String[] {
			"Properties.hbm.xml"
	};
	
	@TempDir
	public File outputFolder = new File("output");
	
	private DefaultArtifactCollector artifactCollector;
	private File outputDir = null;
	private File resourcesDir = null;
	
	@BeforeEach
	public void setUp() throws Exception {
		artifactCollector = new DefaultArtifactCollector();
		outputDir = new File(outputFolder, "src");
		outputDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter exporter = ExporterFactory.createExporter(ExporterType.JAVA);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.getProperties().put(ExporterConstants.ARTIFACT_COLLECTOR, artifactCollector);
		Exporter hbmexporter = new HbmExporter();
		hbmexporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hbmexporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		hbmexporter.getProperties().put(ExporterConstants.ARTIFACT_COLLECTOR, artifactCollector);
		exporter.start();
		hbmexporter.start();
	}	
	
	@Test
	public void testNoGenerationOfEmbeddedPropertiesComponent() {
		assertEquals(2, artifactCollector.getFileCount("java"));
		assertEquals(2, artifactCollector.getFileCount("hbm.xml"));
	}
	
	@Test
	public void testGenerationOfEmbeddedProperties() {
		File outputXml = new File(outputDir,  "properties/PPerson.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
    	SAXReader xmlReader = new SAXReader();
    	xmlReader.setValidation(true);
		Document document;
		try {
			document = xmlReader.read(outputXml);
			XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/properties");
			List<?> list = xpath.selectNodes(document);
			assertEquals(1, list.size(), "Expected to get one properties element");
			Element node = (Element) list.get(0);
			assertEquals(node.attribute( "name" ).getText(),"emergencyContact");
			assertNotNull(
					FileUtil.findFirstString(
							"name", 
							new File(outputDir, "properties/PPerson.java" )));
			assertNull(
					FileUtil.findFirstString(
							"emergencyContact", 
							new File(outputDir, "properties/PPerson.java" )),
					"Embedded component/properties should not show up in .java");		
		} catch (DocumentException e) {
			fail("Can't parse file " + outputXml.getAbsolutePath());
		}		
	}
	
	@Test
	public void testCompilable() throws Exception {
		String propertiesUsageResourcePath = "/org/hibernate/tool/hbm2x/PropertiesTest/PropertiesUsage.java_";
		File propertiesUsageOrigin = new File(getClass().getResource(propertiesUsageResourcePath).toURI());
		File propertiesUsageDestination = new File(outputDir, "properties/PropertiesUsage.java");
		File targetDir = new File(outputFolder, "target" );
		targetDir.mkdir();	
		Files.copy(propertiesUsageOrigin.toPath(), propertiesUsageDestination.toPath());
		JavaUtil.compile(outputDir, targetDir);
		assertTrue(new File(targetDir, "properties/PCompany.class").exists());
		assertTrue(new File(targetDir, "properties/PPerson.class").exists());
		assertTrue(new File(targetDir, "properties/PropertiesUsage.class").exists());
	}

}
