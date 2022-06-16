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

package org.hibernate.tool.hbm2x.Hbm2DaoTest;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Article.hbm.xml",
			"Author.hbm.xml"				
	};
	
	@TempDir
	public File outputFolder = new File("output");
	
	private File srcDir;
	private File resourcesDir;

	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "src");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter javaExporter = ExporterFactory.createExporter(ExporterType.JAVA);
		javaExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		javaExporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
		Exporter exporter = ExporterFactory.createExporter(ExporterType.DAO);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, srcDir);
		exporter.getProperties().setProperty("ejb3", "false");
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();
		javaExporter.start();
	}
	
	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, "org/hibernate/tool/hbm2x/ArticleHome.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, "org/hibernate/tool/hbm2x/AuthorHome.java"));
	}
	
	@Test
	public void testCompilable() throws IOException {	
		File compiled = new File(outputFolder, "compiled");
		compiled.mkdir();
		FileUtil.generateNoopComparator(srcDir);
		JavaUtil.compile(srcDir, compiled);
		assertTrue(new File(compiled, "org/hibernate/tool/hbm2x/Article.class").exists());
		assertTrue(new File(compiled, "org/hibernate/tool/hbm2x/ArticleHome.class").exists());
		assertTrue(new File(compiled, "org/hibernate/tool/hbm2x/Author.class").exists());
		assertTrue(new File(compiled, "org/hibernate/tool/hbm2x/AuthorHome.class").exists());
		assertTrue(new File(compiled, "comparator/NoopComparator.class").exists());
	}
    
	@Test
	public void testNoVelocityLeftOvers() {	
		assertNull(FileUtil
				.findFirstString(
						"$",
						new File(
								srcDir, 
								"org/hibernate/tool/hbm2x/ArticleHome.java")));
        assertNull(FileUtil
        		.findFirstString(
        				"$",
        				new File(
        						srcDir, 
        						"org/hibernate/tool/hbm2x/AuthorHome.java")));
	}

	@Test
	public void testNamedQueries() {		
		assertTrue(FileUtil
				.findFirstString(
						"findByNameAndAddress",
						new File(
								srcDir, 
								"org/hibernate/tool/hbm2x/AuthorHome.java") )
				.trim().startsWith("public List" ));
	}
	
}
