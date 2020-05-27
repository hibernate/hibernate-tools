/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2020 Red Hat, Inc.
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
package org.hibernate.tools.test.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Echo;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AntUtilTest {
	
	private static String BUILD_XML = 
			"<?xml version='1.0' encoding='UTF-8'?> \n" + 
			"<!DOCTYPE project>                     \n" + 
			"<project name='AntUtilTest'/>          \n";
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Test
	public void testCreateProject() throws Exception {
		File buildFile = new File(temporaryFolder.getRoot(), "build.xml");
		Files.copy(new ByteArrayInputStream(BUILD_XML.getBytes()), buildFile.toPath());
		AntUtil.Project project = AntUtil.createProject(buildFile);
		Assert.assertEquals("AntUtilTest", project.getName());
	}
	
	@Test
	public void testGetLog() {
		AntUtil.Project project = new AntUtil.Project();
		Assert.assertNull(project.logBuffer);
		Target target = new Target();
		target.setName("foobar");
		Echo echo = new Echo();
		echo.setProject(project);
		echo.setMessage("Executing foobar");
		target.addTask(echo);
		project.addTarget(target);
		project.executeTarget("foobar");
		Assert.assertTrue(AntUtil.getLog(project).contains("Executing foobar"));
	}

}
