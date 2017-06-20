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
