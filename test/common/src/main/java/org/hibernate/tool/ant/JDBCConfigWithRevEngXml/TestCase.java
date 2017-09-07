package org.hibernate.tool.ant.JDBCConfigWithRevEngXml;

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
	public void testJDBCConfigWithRevEngXml() {

		String[] resources = new String[] {"build.xml", "hibernate.reveng.xml"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		ResourceUtil.createResources(this, new String[] { "/hibernate.properties" }, resourcesDir);
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		File bottomUp = new File(destinationDir, "foo/BottomUp.java");
		Assert.assertFalse(bottomUp.exists());
				
		project.executeTarget("testJDBCConfigWithRevEngXml");

		String log = AntUtil.getLog(project);
		Assert.assertTrue(log, !log.contains("Exception"));
		
		Assert.assertTrue(bottomUp.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("good", bottomUp)
				.contains("Boolean"));
		Assert.assertTrue(FileUtil
				.findFirstString("something", bottomUp)
				.contains("SomeUserType"));
		
	}
	
}
