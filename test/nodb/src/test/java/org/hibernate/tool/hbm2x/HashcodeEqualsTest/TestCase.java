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

package org.hibernate.tool.hbm2x.HashcodeEqualsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;

import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.POJOExporter;
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
			"HashEquals.hbm.xml"				
	};
	
	@TempDir
	public File outputFolder = new File("output");

	private File srcDir = null;
	private File resourcesDir = null;
	private ArtifactCollector artifactCollector = null;
	private MetadataDescriptor metadataDescriptor = null;
	
	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "output");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		Exporter exporter = new POJOExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(srcDir);
		artifactCollector = new ArtifactCollector();
		exporter.setArtifactCollector(artifactCollector);
		exporter.start();
	}

	@Test
	public void testJDK5FailureExpectedOnJDK4() {
		POJOExporter exporter = new POJOExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(srcDir);
		exporter.getProperties().setProperty("jdk5", "true");
		artifactCollector = new ArtifactCollector();
		exporter.setArtifactCollector(artifactCollector);
		exporter.start();
		testFileExistence();
		testNoVelocityLeftOvers();
		testCompilable();
	}
	
	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, "org/hibernate/tool/hbm2x/HashEquals.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, "org/hibernate/tool/hbm2x/Address.java"));
		assertEquals(2, artifactCollector.getFileCount("java"));
	}
	
	@Test
	public void testCompilable() {
		File compiled = new File(outputFolder, "compiled");
		compiled.mkdir();
		JavaUtil.compile(srcDir, compiled);
		JUnitUtil.assertIsNonEmptyFile(new File(
				compiled, "org/hibernate/tool/hbm2x/HashEquals.class"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				compiled, "org/hibernate/tool/hbm2x/Address.class"));
	}

	@Test
	public void testNoVelocityLeftOvers() {
		assertNull(FileUtil
				.findFirstString(
						"$",
						new File(
								srcDir, 
								"org/hibernate/tool/hbm2x/HashEquals.java")));
       assertNull(FileUtil
        		.findFirstString(
        				"$",
        				new File(
        						srcDir, 
        						"org/hibernate/tool/hbm2x/Address.java")));
	}

}
