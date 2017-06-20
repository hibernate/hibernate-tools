package org.hibernate.tool.ant.Hbm2DDLUpdateExecution;

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
	
	private static final String[] RESOURCES = {
			"build.xml",
			"TopDown.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File destinationDir = null;
	private File resourcesDir = null;
	private File buildFile = null;
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		destinationDir = new File(temporaryFolder.getRoot(), "destination");
		destinationDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		ResourceUtil.createResources(this, RESOURCES, resourcesDir);
		buildFile = new File(resourcesDir, "build.xml");
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
		
	@Test
	public void testHbm2DDLUpdateExecution() {

		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());

		File topDown = new File(destinationDir, "org/hibernate/tool/hbm2x/ant/TopDown.java");
		File onlyDrop = new File(destinationDir, "onlydrop.sql");
		File update1 = new File(destinationDir, "update1.sql");
		File update2 = new File(destinationDir, "update2.sql");
				
		Assert.assertFalse(topDown.exists());
		Assert.assertFalse(onlyDrop.exists());
		Assert.assertFalse(update1.exists());
		Assert.assertFalse(update2.exists());
		
		project.executeTarget("testantcfgUpdateExecuted");
		
		String log = AntUtil.getLog(project);
		Assert.assertTrue(log, !log.contains("Exception"));
					
		Assert.assertTrue(topDown.exists());
		Assert.assertTrue(onlyDrop.exists());
		Assert.assertTrue(update1.exists());
		Assert.assertNotNull(FileUtil.findFirstString("create", update1));
		Assert.assertTrue(update2.exists());
		Assert.assertEquals(0, update2.length());

	}
	
	

}
