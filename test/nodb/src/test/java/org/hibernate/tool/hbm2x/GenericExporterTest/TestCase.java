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

package org.hibernate.tool.hbm2x.GenericExporterTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.version.Version;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	private static final String[] HBM_XML_FILES = new String[] {
			"Author.hbm.xml", 
			"Article.hbm.xml", 
			"HelloWorld.hbm.xml"
	};
	
	@TempDir
	public File outputFolder = new File("output");
	
	private MetadataDescriptor metadataDescriptor = null;
	private File outputDir = null;
	private File resourcesDir = null;
	private String resourcesLocation = null;
	
	@BeforeEach
	public void setUp() {
		outputDir = new File(outputFolder, "src");
		outputDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		resourcesLocation = '/' + getClass().getPackage().getName().replace(".", "/") + '/';
	}

	@Test
	public void testSingleFileGeneration() {
		Exporter ge = ExporterFactory.createExporter(ExporterType.GENERIC);
		ge.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		ge.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
    	ge.getProperties().put(ExporterConstants.TEMPLATE_NAME, resourcesLocation + "generic-test.ftl");
    	ge.getProperties().put(ExporterConstants.FILE_PATTERN, "generictest.txt");
		ge.start();
		JUnitUtil.assertIsNonEmptyFile(new File( outputDir,"artifacts.txt"));	
		JUnitUtil.assertIsNonEmptyFile(new File( outputDir, "templates.txt"));
		assertEquals(
				null, 
				FileUtil.findFirstString("$", new File(outputDir, "artifacts.txt")));	
		assertEquals(
				"File for artifacts in " + Version.versionString(), 
				FileUtil.findFirstString("artifacts", new File( outputDir, "artifacts.txt")));
	}

	// Not sure about this next test... There doesn't seem to be a syntax failure anywhere
	@Test
	public void testFreeMarkerSyntaxFailureExpected() {
		Exporter ge = ExporterFactory.createExporter(ExporterType.GENERIC);
		ge.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		ge.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
    	ge.getProperties().put(ExporterConstants.TEMPLATE_NAME, resourcesLocation + "freemarker.ftl");
    	ge.getProperties().put(ExporterConstants.FILE_PATTERN, "{class-name}.ftltest");
		ge.start();
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "Author.ftltest" ) );	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "Article.ftltest" ) );
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "BaseHelloWorld.ftltest" ) );	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "HelloUniverse.ftltest" ) );
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "UniversalAddress.ftltest" ) );
	}

	@Test
	public void testClassFileGeneration() {
		Exporter ge = ExporterFactory.createExporter(ExporterType.GENERIC);
		ge.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		ge.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
    	ge.getProperties().put(ExporterConstants.TEMPLATE_NAME, resourcesLocation + "generic-class.ftl");
    	ge.getProperties().put(ExporterConstants.FILE_PATTERN, "generic{class-name}.txt");
		ge.start();
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "genericAuthor.txt" ) );	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "genericArticle.txt" ) );
	}	
	
	@Test
	public void testPackageFileGeneration() {
		Exporter ge = ExporterFactory.createExporter(ExporterType.GENERIC);
		ge.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		ge.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
    	ge.getProperties().put(ExporterConstants.TEMPLATE_NAME, resourcesLocation + "generic-class.ftl");
    	ge.getProperties().put(ExporterConstants.FILE_PATTERN, "{package-name}/generic{class-name}.txt");
		ge.start();
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericAuthor.txt"));	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericArticle.txt"));
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericArticle.txt"));				
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "genericUniversalAddress.txt"));	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "genericHelloUniverse.txt"));
	}

	@Test
	public void testForEachGeneration() {
		Exporter ge = ExporterFactory.createExporter(ExporterType.GENERIC);
		ge.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		ge.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
    	ge.getProperties().put(ExporterConstants.TEMPLATE_NAME, resourcesLocation + "generic-class.ftl");
    	ge.getProperties().put(ExporterConstants.FILE_PATTERN, "{package-name}/generic{class-name}.txt");
    	ge.getProperties().put(ExporterConstants.FOR_EACH, "entity");
		ge.start();
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericAuthor.txt"));	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericArticle.txt"));
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericArticle.txt"));		
		assertFalse(
				new File(outputDir, "genericUniversalAddress.txt" ).exists(),
				"component file should not exist");
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "genericHelloUniverse.txt" ) );
		try {
	    	ge.getProperties().put(ExporterConstants.FOR_EACH, "does, not, exist");
			ge.start();
			fail();
		} catch(Exception e) {
			//e.printStackTrace();
			//expected
		}
	}

	@Test
	public void testForEachWithExceptionGeneration() {
		Exporter ge = ExporterFactory.createExporter(ExporterType.GENERIC);
		ge.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		ge.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
    	ge.getProperties().put(ExporterConstants.TEMPLATE_NAME, resourcesLocation + "generic-exception.ftl");
    	ge.getProperties().put(ExporterConstants.FILE_PATTERN, "{package-name}/generic{class-name}.txt");
		try {
	    	ge.getProperties().put(ExporterConstants.FOR_EACH, "entity");
			ge.start();
			fail();
		} catch(RuntimeException e) {
			assertTrue(e.getMessage().startsWith("Error while processing Entity:"));			
		}
		try {
	    	ge.getProperties().put(ExporterConstants.FOR_EACH, "component");
			ge.start();
			fail();
		} catch(RuntimeException e) {
			assertTrue(e.getMessage().startsWith("Error while processing Component: UniversalAddress"));
		}		
		try {
	    	ge.getProperties().put(ExporterConstants.FOR_EACH, "configuration");
			ge.start();
			fail();
		} catch(RuntimeException e) {
			assertTrue(e.getMessage().startsWith("Error while processing Configuration"));
		}
	}

	@Test
	public void testPropertySet() throws FileNotFoundException, IOException {
		Exporter ge = ExporterFactory.createExporter(ExporterType.GENERIC);
		Properties p = new Properties();
		p.setProperty("proptest", "A value");
		p.setProperty( "refproperty", "proptest=${proptest}" );
		p.setProperty("hibernatetool.booleanProperty", "true");
		p.setProperty("hibernatetool.myTool.toolclass", "org.hibernate.tool.internal.export.java.Cfg2JavaTool");
		ge.getProperties().putAll(p);
		ge.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		ge.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
    	ge.getProperties().put(ExporterConstants.TEMPLATE_NAME, resourcesLocation + "generic-class.ftl");
    	ge.getProperties().put(ExporterConstants.FILE_PATTERN, "{package-name}/generic{class-name}.txt");
		ge.start();		
		Properties generated = new Properties();
		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(outputDir, "org/hibernate/tool/hbm2x/genericArticle.txt"));
			generated.load(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}		
		assertEquals(generated.getProperty("booleanProperty"), "true");
		assertEquals(generated.getProperty("hibernatetool.booleanProperty"), "true");
		assertNull(generated.getProperty("booleanWasTrue"));
		assertEquals(generated.getProperty("myTool.value"), "value");
		assertEquals(generated.getProperty("refproperty"), "proptest=A value");	
	}
	
}
