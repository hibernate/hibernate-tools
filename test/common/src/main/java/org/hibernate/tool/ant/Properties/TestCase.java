package org.hibernate.tool.ant.Properties;

import java.io.File;

import org.hibernate.tools.test.util.AntUtil;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.hibernate.tools.test.util.ResourceUtil;
import org.junit.After;
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
		JdbcUtil.createDatabase(this);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testProperties() {

		String[] resources = new String[] {"build.xml", "SomeClass.hbm.xml"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		ResourceUtil.createResources(this, new String[] { "/hibernate.properties" }, resourcesDir);
		File templatesDir = new File(resourcesDir, "templates");
		templatesDir.mkdir();
		File pojoTemplateDir = new File(templatesDir, "pojo");
		pojoTemplateDir.mkdir();
		ResourceUtil.createResources(this, new String[] { "Pojo.ftl" }, pojoTemplateDir);
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		File file = new File(destinationDir, "SomeClass.java");
		Assert.assertFalse(file.exists());
		
		project.executeTarget("testProperties");

		String log = AntUtil.getLog(project);
		Assert.assertTrue(log, !log.contains("Exception"));
		
		Assert.assertTrue(file.exists());
		
		Assert.assertTrue(FileUtil
				.findFirstString("hbm2java.weirdAl", file)
				.contains("foo3"));
		Assert.assertTrue(FileUtil
				.findFirstString("ant.project.name", file)
				.contains("PropertiesTest"));
		Assert.assertTrue(FileUtil
				.findFirstString("foo.weirdAl", file)
				.contains("does not exist"));
		Assert.assertTrue(FileUtil
				.findFirstString("bar", file)
				.contains("foo2"));
		Assert.assertTrue(FileUtil
				.findFirstString("file", file)
				.contains("some.file"));
		Assert.assertTrue(FileUtil
				.findFirstString("value", file)
				.contains("some value"));
		
	}
	
}
