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
package org.hibernate.tool.hbm2x.JdbcHbm2JavaEjb3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Persistence;

import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 *
 */
public class TestCase {
	
	@TempDir
	public File outputDir = new File("output");
	
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		POJOExporter exporter = new POJOExporter();
		exporter.setMetadataDescriptor(MetadataDescriptorFactory
				.createJdbcDescriptor(null, null, true));
		exporter.setOutputDirectory(outputDir);
		exporter.setTemplatePath(new String[0]);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile( new File(outputDir.getAbsolutePath() + "/Master.java") );
	}

	@Test
	public void testUniqueConstraints() {
		assertEquals(null, FileUtil.findFirstString( "uniqueConstraints", new File(outputDir,"Master.java") ));
		assertNotNull(FileUtil.findFirstString( "uniqueConstraints", new File(outputDir,"Uniquemaster.java") ));
	}
	
	@Test
	public void testCompile() {
		File destination = new File(outputDir, "destination");
		destination.mkdir();
		List<String> jars = new ArrayList<String>();
		jars.add(JavaUtil.resolvePathToJarFileFor(Persistence.class)); // for jpa api
		JavaUtil.compile(outputDir, destination, jars);
		JUnitUtil.assertIsNonEmptyFile(new File(destination, "Master.class"));
		JUnitUtil.assertIsNonEmptyFile(new File(destination, "Uniquemaster.class"));
	}
	
}
