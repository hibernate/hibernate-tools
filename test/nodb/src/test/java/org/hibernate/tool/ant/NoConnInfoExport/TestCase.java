package org.hibernate.tool.ant.NoConnInfoExport;

import java.io.File;

import org.hibernate.tools.test.util.AntUtil;
import org.hibernate.tools.test.util.ResourceUtil;
import org.junit.After;
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
//		JdbcUtil.createDatabase(this);
	}
	
	@After
	public void tearDown() {
//		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testNoConnInfoExport() {

		String resourcesLocation = ResourceUtil.getResourcesLocation(this);
		String[] resources = new String[] {"build.xml", "hibernate.cfg.xml", "TopDown.hbm.xml"};
		ResourceUtil.createResources(this, resourcesLocation, resources, resourcesDir);
		File buildFile = new File(resourcesDir, "build.xml");	
		
		AntUtil.Project project = AntUtil.createProject(buildFile);
		project.setProperty("destinationDir", destinationDir.getAbsolutePath());
		project.setProperty("resourcesDir", resourcesDir.getAbsolutePath());
		
		project.executeTarget("testNoConnInfoExport");
		
		System.out.println("blah");
		
	}
	
}
