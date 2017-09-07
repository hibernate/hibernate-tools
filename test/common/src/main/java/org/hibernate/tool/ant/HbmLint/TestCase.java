package org.hibernate.tool.ant.HbmLint;

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
	public void testHbmLint() {

		String[] resources = new String[] {"build.xml", "SchemaIssues.hbm.xml", "hibernate.cfg.xml"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		ResourceUtil.createResources(this, new String[] { "/hibernate.properties" }, resourcesDir);
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		File hbmLintResult = new File(destinationDir, "hbmlint-result.txt");
		Assert.assertFalse(hbmLintResult.exists());

		project.executeTarget("testHbmLint");
		
		Assert.assertTrue(hbmLintResult.exists());
		
		Assert.assertTrue(FileUtil
				.findFirstString("BadType", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		Assert.assertTrue(FileUtil
				.findFirstString("Category", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		Assert.assertTrue(FileUtil
				.findFirstString("Column", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		Assert.assertTrue(FileUtil
				.findFirstString("does_not_exist", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		Assert.assertTrue(FileUtil
				.findFirstString("hilo_table", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		Assert.assertTrue(FileUtil
				.findFirstString("MissingTable", hbmLintResult)
				.contains("SCHEMA_TABLE_MISSING"));
		
		Assert.assertTrue(FileUtil
				.findFirstString("MISSING_ID_GENERATOR", hbmLintResult)
				.contains("does_not_exist"));
		
	}
	
	
}
