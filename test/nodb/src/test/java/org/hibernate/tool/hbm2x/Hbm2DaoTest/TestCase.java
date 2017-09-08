/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.Hbm2DaoTest;

import java.io.File;
import java.io.IOException;

import org.hibernate.tool.hbm2x.DAOExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.metadata.MetadataSources;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Article.hbm.xml",
			"Author.hbm.xml"				
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File outputDir;
	private File resourcesDir;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataSources metadataSources = HibernateUtil
				.initializeMetadataSources(this, HBM_XML_FILES, resourcesDir);
		POJOExporter javaExporter = new POJOExporter();
		javaExporter.setMetadataSources(metadataSources);
		javaExporter.setOutputDirectory(outputDir);
		POJOExporter exporter = new DAOExporter();
		exporter.setMetadataSources(metadataSources);
		exporter.setOutputDirectory(outputDir);
		exporter.getProperties().setProperty("ejb3", "false");
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();
		javaExporter.start();
	}
	
	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, "org/hibernate/tool/hbm2x/ArticleHome.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, "org/hibernate/tool/hbm2x/AuthorHome.java"));
	}
	
	@Test
	public void testCompilable() throws IOException {	
		File compiled = new File(temporaryFolder.getRoot(), "compiled");
		compiled.mkdir();
		FileUtil.generateNoopComparator(outputDir);
		JavaUtil.compile(outputDir, compiled);
		Assert.assertTrue(new File(compiled, "org/hibernate/tool/hbm2x/Article.class").exists());
		Assert.assertTrue(new File(compiled, "org/hibernate/tool/hbm2x/ArticleHome.class").exists());
		Assert.assertTrue(new File(compiled, "org/hibernate/tool/hbm2x/Author.class").exists());
		Assert.assertTrue(new File(compiled, "org/hibernate/tool/hbm2x/AuthorHome.class").exists());
		Assert.assertTrue(new File(compiled, "comparator/NoopComparator.class").exists());
	}
    
	@Test
	public void testNoVelocityLeftOvers() {	
		Assert.assertNull(FileUtil
				.findFirstString(
						"$",
						new File(
								outputDir, 
								"org/hibernate/tool/hbm2x/ArticleHome.java")));
        Assert.assertNull(FileUtil
        		.findFirstString(
        				"$",
        				new File(
        						outputDir, 
        						"org/hibernate/tool/hbm2x/AuthorHome.java")));
	}

	@Test
	public void testNamedQueries() {		
		Assert.assertTrue(FileUtil
				.findFirstString(
						"findByNameAndAddress",
						new File(
								outputDir, 
								"org/hibernate/tool/hbm2x/AuthorHome.java") )
				.trim().startsWith("public List" ));
	}
	
}
