package org.hibernate.tool.ant.NoConnInfoExport;

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
	public void testNoConnInfoExport() {

		String[] resources = new String[] {"build.xml", "hibernate.cfg.xml", "TopDown.hbm.xml"};
		ResourceUtil.createResources(this, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		File noConnInfo = new File(destinationDir, "noConnInfo.sql");
		Assert.assertFalse(noConnInfo.exists());
		
		project.executeTarget("testNoConnInfoExport");
		
		Assert.assertTrue(noConnInfo.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("create", noConnInfo)
				.contains("create table TopDown (id bigint not null, name varchar(255), primary key (id));"));
	}
	
}
