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

package org.hibernate.tool.ant.JavaFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.hibernate.tool.ide.formatting.DefaultJavaPrettyPrinterStrategy;
import org.hibernate.tools.test.util.AntUtil;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.ResourceUtil;
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
	}
	
	@Test
	public void testJavaFormatFile() {
		
		String[] resources = new String[] {"build.xml", "formatting/SimpleOne.java"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());

		File simpleOne = new File(destinationDir, "formatting/SimpleOne.java");
		assertFalse(simpleOne.exists());

		project.executeTarget("copyfiles");
		
		String log = AntUtil.getLog(project);
		assertFalse(log.contains("Exception"));
		
		assertTrue(simpleOne.exists());
		assertFalse(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		
		DefaultJavaPrettyPrinterStrategy formatter = new DefaultJavaPrettyPrinterStrategy(null);
		formatter.formatFile( simpleOne );
		
		assertTrue(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
				
	}
	
	@Test
	public void testJavaJdk5FormatFile() {
		
		String[] resources = new String[] {"build.xml", "formatting/Simple5One.java"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());

		File simple5One = new File(destinationDir, "formatting/Simple5One.java");
		assertFalse(simple5One.exists());

		project.executeTarget("copyfiles");
		
		String log = AntUtil.getLog(project);
		assertFalse(log.contains("Exception"));
				
		assertTrue(simple5One.exists());
		assertFalse(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));
		
		project.executeTarget("copyfiles");
		
		assertFalse(log.contains("Exception"));
				
		DefaultJavaPrettyPrinterStrategy formatter = new DefaultJavaPrettyPrinterStrategy(null);
		assertTrue(
				formatter.formatFile(simple5One),
				"formatting should pass when using default settings");
		
		assertTrue(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));
	}
	
	@Test
	public void testAntFormatTask() {
		
		String[] resources = new String[] {"build.xml", "formatting/SimpleOne.java"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());

		File simpleOne = new File(destinationDir, "formatting/SimpleOne.java");
		assertFalse(simpleOne.exists());

		project.executeTarget("copyfiles");

		String log = AntUtil.getLog(project);
		assertFalse(log.contains("Exception"));
				
		assertTrue(simpleOne.exists());
		assertFalse(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		
		project.executeTarget("formatfiles");

		assertTrue(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));

	}
	
	@Test
	public void testConfig() {
		
		String[] resources = new String[] {"build.xml", "formatting/SimpleOne.java", "formatting/Simple5One.java", "emptyconfig.properties"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());

		File simpleOne = new File(destinationDir, "formatting/SimpleOne.java");
		assertFalse(simpleOne.exists());
		File simple5One = new File(destinationDir, "formatting/Simple5One.java");
		assertFalse(simple5One.exists());

		project.executeTarget("copyfiles");

		String log = AntUtil.getLog(project);
		assertFalse(log.contains("Exception"));
				
		assertTrue(simpleOne.exists());
		assertTrue(simple5One.exists());
		
		assertFalse(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		assertFalse(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));

		project.executeTarget("configtest");
		log = AntUtil.getLog(project);
		assertFalse(log.contains("Exception"));
		
		assertTrue(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		assertTrue(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));
		
		assertTrue(simpleOne.delete());
		assertTrue(simple5One.delete());
		
		project.executeTarget("copyfiles");
		log = AntUtil.getLog(project);
		assertFalse(log.contains("Exception"));

		assertFalse(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		assertFalse(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));

		project.executeTarget("noconfigtest");
		log = AntUtil.getLog(project);
		assertFalse(log.contains("Exception"));		
		
		assertTrue(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		assertTrue(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));
		
	}

}
