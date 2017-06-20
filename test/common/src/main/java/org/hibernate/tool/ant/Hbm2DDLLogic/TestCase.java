package org.hibernate.tool.ant.Hbm2DDLLogic;

import java.io.File;

import org.hibernate.tools.test.util.AntUtil;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.ResourceUtil;
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
		destinationDir = new File(temporaryFolder.getRoot(), "destination");
		destinationDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		ResourceUtil.createResources(this, RESOURCES, resourcesDir);
		buildFile = new File(resourcesDir, "build.xml");
	}
	
	@Test
	public void testHbm2DDLLogic() throws Exception {
		
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

}
