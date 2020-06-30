package org.hibernate.tool.ant.JDBCConfiguration;

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
	public void testJDBCConfiguration() {

		String[] resources = new String[] {"JDBCConfiguration.xml"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "JDBCConfiguration.xml");	
		ResourceUtil.createResources(this, new String[] { "/hibernate.properties" }, resourcesDir);
		File templatesDir = new File(resourcesDir, "templates");
		templatesDir.mkdir();
		File pojoTemplateDir = new File(templatesDir, "pojo");
		pojoTemplateDir.mkdir();
		ResourceUtil.createResources(this, new String[] { "Pojo.ftl" }, pojoTemplateDir);
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		File noTemplate = new File(destinationDir, "no-template/BottomUp.java");
		File withTemplate = new File(destinationDir, "with-template/BottomUp.java");
		File cfgxml = new File(destinationDir, "cfgxml/hibernate.cfg.xml");
		
		Assert.assertFalse(noTemplate.exists());
		Assert.assertFalse(withTemplate.exists());
		Assert.assertFalse(cfgxml.exists());
		
		project.executeTarget("testJDBCConfiguration");

		String log = AntUtil.getLog(project);
		Assert.assertTrue(log, !log.contains("Exception"));
		
		Assert.assertTrue(noTemplate.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("public", noTemplate)
				.contains("BottomUp"));
		
		Assert.assertTrue(withTemplate.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("template", withTemplate)
				.contains("/** Made by a template in your neighborhood */"));

		Assert.assertTrue(cfgxml.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("mapping", cfgxml)
				.contains("BottomUp.hbm.xml"));
		
	}
	
}
