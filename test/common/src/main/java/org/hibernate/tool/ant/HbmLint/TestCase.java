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
package org.hibernate.tool.ant.HbmLint;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.hibernate.tools.test.util.AntUtil;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.hibernate.tools.test.util.ResourceUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TestCase {
	
	@TempDir
	public File outputFolder = new File("output");
	
	private File destinationDir = null;
	private File resourcesDir = null;
	
	@BeforeEach
	public void setUp() {
		destinationDir = new File(outputFolder, "destination");
		destinationDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		JdbcUtil.createDatabase(this);
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testHbmLint() {

		String[] resources = new String[] {"build.xml", "SchemaIssues.hbm.xml", "hibernate.cfg.xml"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		ResourceUtil.createResources(this, new String[] { "/hibernate.properties" }, resourcesDir);
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		File hbmLintResult = new File(destinationDir, "hbmlint-result.txt");
		assertFalse(hbmLintResult.exists());

		project.executeTarget("testHbmLint");
		
		assertTrue(hbmLintResult.exists());
		
		assertTrue(FileUtil
				.findFirstString("BadType", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		assertTrue(FileUtil
				.findFirstString("Category", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		assertTrue(FileUtil
				.findFirstString("Column", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		assertTrue(FileUtil
				.findFirstString("does_not_exist", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		assertTrue(FileUtil
				.findFirstString("hilo_table", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		assertTrue(FileUtil
				.findFirstString("MissingTable", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		assertTrue(FileUtil
				.findFirstString("MISSING_ID_GENERATOR", hbmLintResult)
				.contains("does_not_exist"));
		
	}
	
	
}
