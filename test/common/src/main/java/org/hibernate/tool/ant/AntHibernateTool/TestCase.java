package org.hibernate.tool.ant.AntHibernateTool;

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
	public void testHbm2DDLLogic() throws Exception {

		String[] resources = { "Hbm2DDLLogic.xml", "TopDown.hbm.xml" };		
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "Hbm2DDLLogic.xml");		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());

		File topDown = new File(destinationDir, "org/hibernate/tool/hbm2x/ant/TopDown.java");
		File onlyDrop = new File(destinationDir, "onlydrop.sql");
		File onlyCreate = new File(destinationDir, "onlycreate.sql");
		File dropAndCreate = new File(destinationDir, "dropandcreate.sql");
		File update = new File(destinationDir, "update.sql");
		
		Assert.assertFalse(topDown.exists());
		Assert.assertFalse(onlyDrop.exists());	
		Assert.assertFalse(onlyCreate.exists());
		Assert.assertFalse(dropAndCreate.exists());
		Assert.assertFalse(update.exists());
		
		project.executeTarget("testHbm2DDLLogic");
		
		String log = AntUtil.getLog(project);
		Assert.assertTrue(log, !log.contains("Exception"));
		
		Assert.assertTrue(topDown.exists());	
		
		Assert.assertTrue(onlyDrop.exists());		
		Assert.assertNull(FileUtil.findFirstString("create", onlyDrop));
		Assert.assertNotNull(FileUtil.findFirstString("drop", onlyDrop));	
		
		Assert.assertTrue(onlyCreate.exists());
		Assert.assertNull(FileUtil.findFirstString("drop", onlyCreate));
		Assert.assertNotNull(FileUtil.findFirstString("create", onlyCreate));
		Assert.assertNotNull(FileUtil.findFirstString("---", onlyCreate));
		
		Assert.assertTrue(dropAndCreate.exists());
		Assert.assertNotNull(FileUtil.findFirstString("drop", dropAndCreate));
		Assert.assertNotNull(FileUtil.findFirstString("create", dropAndCreate));
		Assert.assertNotNull(FileUtil.findFirstString("---", dropAndCreate));

		Assert.assertTrue(update.exists());
		Assert.assertNotNull(FileUtil.findFirstString("create", update));
		Assert.assertNotNull(FileUtil.findFirstString("---", update));

	}

	@Test
	public void testHbm2DDLUpdateExecution() {

		String[] resources = { "Hbm2DDLUpdateExecution.xml", "TopDown.hbm.xml" };
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "Hbm2DDLUpdateExecution.xml");
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

	@Test
	public void testHbm2DDLExportExecution() throws Exception {

		String[] resources = { "Hbm2DDLExportExecution.xml", "TopDown.hbm.xml" };	
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "Hbm2DDLExportExecution.xml");		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());

		File export = new File(destinationDir, "export.sql");
		File update = new File(destinationDir, "update.sql");
		File onlydrop = new File(destinationDir, "onlydrop.sql");
		
		Assert.assertFalse(export.exists());
		Assert.assertFalse(update.exists());
		Assert.assertFalse(onlydrop.exists());

		project.executeTarget("testantcfgExportExecuted");
		
		String log = AntUtil.getLog(project);
		Assert.assertTrue(log, !log.contains("Exception"));
		
		Assert.assertTrue(export.exists());
		Assert.assertTrue(update.exists());
		Assert.assertNotNull(FileUtil.findFirstString("create", export));
		// if export is executed, update should be empty
		Assert.assertEquals(0, update.length());
		
	}

}
