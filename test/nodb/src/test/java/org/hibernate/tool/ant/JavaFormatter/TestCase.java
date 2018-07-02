package org.hibernate.tool.ant.JavaFormatter;

import java.io.File;

import org.hibernate.tool.api.java.Formatter;
import org.hibernate.tools.test.util.AntUtil;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.ResourceUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestCase {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File destinationDir = null;
	private File resourcesDir = null;
	
	@Before
	public void setUp() {
		destinationDir = new File(temporaryFolder.getRoot(), "destination");
		destinationDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
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
		Assert.assertFalse(simpleOne.exists());

		project.executeTarget("copyfiles");
		
		String log = AntUtil.getLog(project);
		Assert.assertFalse(log.contains("Exception"));
		
		Assert.assertTrue(simpleOne.exists());
		Assert.assertFalse(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		
		Formatter formatter = new Formatter(null);
		formatter.formatFile( simpleOne );
		
		Assert.assertTrue(FileUtil
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
		Assert.assertFalse(simple5One.exists());

		project.executeTarget("copyfiles");
		
		String log = AntUtil.getLog(project);
		Assert.assertFalse(log.contains("Exception"));
				
		Assert.assertTrue(simple5One.exists());
		Assert.assertFalse(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));
		
		project.executeTarget("copyfiles");
		
		Assert.assertFalse(log.contains("Exception"));
				
		Formatter formatter = new Formatter(null);
		Assert.assertTrue(
				"formatting should pass when using default settings", 
				formatter.formatFile(simple5One));
		
		Assert.assertTrue(FileUtil
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
		Assert.assertFalse(simpleOne.exists());

		project.executeTarget("copyfiles");

		String log = AntUtil.getLog(project);
		Assert.assertFalse(log.contains("Exception"));
				
		Assert.assertTrue(simpleOne.exists());
		Assert.assertFalse(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		
		project.executeTarget("formatfiles");

		Assert.assertTrue(FileUtil
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
		Assert.assertFalse(simpleOne.exists());
		File simple5One = new File(destinationDir, "formatting/Simple5One.java");
		Assert.assertFalse(simple5One.exists());

		project.executeTarget("copyfiles");

		String log = AntUtil.getLog(project);
		Assert.assertFalse(log.contains("Exception"));
				
		Assert.assertTrue(simpleOne.exists());
		Assert.assertTrue(simple5One.exists());
		
		Assert.assertFalse(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		Assert.assertFalse(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));

		project.executeTarget("configtest");
		log = AntUtil.getLog(project);
		Assert.assertFalse(log.contains("Exception"));
		
		Assert.assertTrue(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		Assert.assertTrue(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));
		
		Assert.assertTrue(simpleOne.delete());
		Assert.assertTrue(simple5One.delete());
		
		project.executeTarget("copyfiles");
		log = AntUtil.getLog(project);
		Assert.assertFalse(log.contains("Exception"));

		Assert.assertFalse(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		Assert.assertFalse(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));

		project.executeTarget("noconfigtest");
		log = AntUtil.getLog(project);
		Assert.assertFalse(log.contains("Exception"));		
		
		Assert.assertTrue(FileUtil
				.findFirstString("public", simpleOne)
				.contains("SimpleOne"));
		Assert.assertTrue(FileUtil
				.findFirstString("public", simple5One)
				.contains("Simple5One"));
		
	}

}
