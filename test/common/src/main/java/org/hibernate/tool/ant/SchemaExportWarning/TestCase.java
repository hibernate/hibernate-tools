package org.hibernate.tool.ant.SchemaExportWarning;

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
	public void testSchemaExport() {

		String[] resources = new String[] {"build.xml", "TopDown.hbm.xml"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		File schemaExport = new File(destinationDir, "schemaexport.sql");
		Assert.assertFalse(schemaExport.exists());
		
		project.executeTarget("testSchemaExportWarning");
		
		Assert.assertTrue(schemaExport.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("drop", schemaExport)
				.contains("TopDown"));
		
		String log = AntUtil.getLog(project);
		Assert.assertFalse(log.contains("Exception"));

	}	
	
}
