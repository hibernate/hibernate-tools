package org.hibernate.tool.ant.Query;

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
	public void testQuery() {

		String[] resources = new String[] {"build.xml", "hibernate.cfg.xml"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		ResourceUtil.createResources(this, new String[] { "/hibernate.properties" }, resourcesDir);
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		File hqlQuery = new File(destinationDir, "hqlQuery.txt");
		Assert.assertFalse(hqlQuery.exists());
		File textQuery = new File(destinationDir, "textQuery.txt");
		Assert.assertFalse(textQuery.exists());
		
		project.executeTarget("testQuery");
		
		Assert.assertTrue(hqlQuery.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("First", hqlQuery)
				.contains("SerializableResult(id:First,length:1023)"));
		Assert.assertTrue(FileUtil
				.findFirstString("Third", hqlQuery)
				.contains("ObjectResult(id:Third,length:4095)"));
		
		Assert.assertTrue(textQuery.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("First", textQuery)
				.contains("SerializableResult(id:First,length:1023)"));
		Assert.assertNull(FileUtil.findFirstString("Third", textQuery));
		
	}
	
	
}
