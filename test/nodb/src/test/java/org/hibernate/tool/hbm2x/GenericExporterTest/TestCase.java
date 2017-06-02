/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.GenericExporterTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.Version;
import org.hibernate.tool.hbm2x.ExporterException;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
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
			"Author.hbm.xml", 
			"Article.hbm.xml", 
			"HelloWorld.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Configuration configuration = null;
	private File outputDir = null;
	private String resourcesLocation = null;
	
	@Before
	public void setUp() {
		outputDir = temporaryFolder.getRoot();
		resourcesLocation = '/' + getClass().getPackage().getName().replace(".", "/") + '/';
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.loadProperties(resourcesLocation + "hibernate.properties");
		MetadataSources metadataSources = new MetadataSources(ssrb.build());
		for (int i = 0; i < HBM_XML_FILES.length; i++) {
			metadataSources.addResource(resourcesLocation + HBM_XML_FILES[i]);
		}
		configuration = new Configuration(metadataSources);
		configuration.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
		metadataSources.buildMetadata();
	}

	@Test
	public void testSingleFileGeneration() {
		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(configuration);
		ge.setOutputDirectory(outputDir);
		ge.setTemplateName(resourcesLocation + "generic-test.ftl"); 
		ge.setFilePattern("generictest.txt");
		ge.start();
		JUnitUtil.assertIsNonEmptyFile(new File( outputDir,"artifacts.txt"));	
		JUnitUtil.assertIsNonEmptyFile(new File( outputDir, "templates.txt"));
		Assert.assertEquals(
				null, 
				FileUtil.findFirstString("$", new File(outputDir, "artifacts.txt")));	
		Assert.assertEquals(
				"File for artifacts in " + Version.getDefault().getVersion(), 
				FileUtil.findFirstString("artifacts", new File( outputDir, "artifacts.txt")));
	}

	// Not sure about this next test... There doesn't seem to be a syntax failure anywhere
	@Test
	public void testFreeMarkerSyntaxFailureExpected() {
		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(configuration);
		ge.setOutputDirectory(outputDir);
		ge.setTemplateName(resourcesLocation + "freemarker.ftl");
		ge.setFilePattern("{class-name}.ftltest");
		ge.start();
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "Author.ftltest" ) );	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "Article.ftlTest" ) );
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "BaseHelloWorld.ftlTest" ) );	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "HelloUniverse.ftlTest" ) );
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "UniversalAddress.ftlTest" ) );
	}

	@Test
	public void testClassFileGeneration() {
		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(configuration);
		ge.setOutputDirectory(outputDir);
		ge.setTemplateName(resourcesLocation + "generic-class.ftl");
		ge.setFilePattern("generic{class-name}.txt");
		ge.start();
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "genericAuthor.txt" ) );	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "genericArticle.txt" ) );
	}	
	
	@Test
	public void testPackageFileGeneration() {
		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(configuration);
		ge.setOutputDirectory(outputDir);
		ge.setTemplateName(resourcesLocation + "generic-class.ftl");
		ge.setFilePattern("{package-name}/generic{class-name}.txt");
		ge.start();
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericAuthor.txt"));	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericArticle.txt"));
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericArticle.txt"));				
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "genericUniversalAddress.txt"));	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "genericHelloUniverse.txt"));
	}

	@Test
	public void testForEachGeneration() {
		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(configuration);
		ge.setOutputDirectory(outputDir);
		ge.setTemplateName(resourcesLocation + "generic-class.ftl");
		ge.setFilePattern("{package-name}/generic{class-name}.txt");
		ge.setForEach("entity");
		ge.start();
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericAuthor.txt"));	
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericArticle.txt"));
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "org/hibernate/tool/hbm2x/genericArticle.txt"));		
		Assert.assertFalse(
				"component file should not exist", 
				new File(outputDir, "genericUniversalAddress.txt" ).exists());
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "genericHelloUniverse.txt" ) );
		try {
			ge.setForEach( "does, not, exist" );
			ge.start();
			Assert.fail();
		} catch(Exception e) {
			//e.printStackTrace();
			//expected
		}
	}

	@Test
	public void testForEachWithExceptionGeneration() {
		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(configuration);
		ge.setOutputDirectory(outputDir);
		ge.setTemplateName(resourcesLocation + "generic-exception.ftl");
		ge.setFilePattern("{package-name}/generic{class-name}.txt");
		try {
			ge.setForEach("entity");
			ge.start();
			Assert.fail();
		} catch(ExporterException e) {
			Assert.assertTrue(e.getMessage().startsWith("Error while processing Entity:"));			
		}
		try {
			ge.setForEach("component");
			ge.start();
			Assert.fail();
		} catch(ExporterException e) {
			Assert.assertTrue(e.getMessage().startsWith("Error while processing Component: UniversalAddress"));
		}		
		try {
			ge.setForEach("configuration");
			ge.start();
			Assert.fail();
		} catch(ExporterException e) {
			Assert.assertTrue(e.getMessage().startsWith("Error while processing Configuration"));
		}
	}

	@Test
	public void testPropertySet() throws FileNotFoundException, IOException {
		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(configuration);
		ge.setOutputDirectory(outputDir);
		Properties p = new Properties();
		p.setProperty("proptest", "A value");
		p.setProperty( "refproperty", "proptest=${proptest}" );
		p.setProperty("hibernatetool.booleanProperty", "true");
		p.setProperty("hibernatetool.myTool.toolclass", "org.hibernate.tool.hbm2x.Cfg2JavaTool");
		ge.setProperties(p);
		ge.setTemplateName(resourcesLocation + "generic-class.ftl");
		ge.setFilePattern("{package-name}/generic{class-name}.txt");
		ge.start();		
		Properties generated = new Properties();
		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(outputDir, "org/hibernate/tool/hbm2x/genericArticle.txt"));
			generated.load(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}		
		Assert.assertEquals(generated.getProperty("booleanProperty"), "true");
		Assert.assertEquals(generated.getProperty("hibernatetool.booleanProperty"), "true");
		Assert.assertNull(generated.getProperty("booleanWasTrue"));
		Assert.assertEquals(generated.getProperty("myTool.value"), "value");
		Assert.assertEquals(generated.getProperty("refproperty"), "proptest=A value");	
	}
	
}
