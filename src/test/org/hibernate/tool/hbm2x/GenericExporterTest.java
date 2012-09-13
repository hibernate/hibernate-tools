/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.Version;

/**
 * @author max
 * 
 */
public class GenericExporterTest extends NonReflectiveTestCase {

	public GenericExporterTest(String name) {
		super( name, "genericexport" );
	}

	protected void setUp() throws Exception {
		super.setUp();

	}

	public void testSingleFileGeneration() {

		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(getCfg());
		ge.setOutputDirectory(getOutputDir());
		ge.setTemplateName("generictemplates/pojo/generic-test.ftl");
		ge.setFilePattern("generictest.txt");
		ge.start();

		assertFileAndExists( new File( getOutputDir(),"artifacts.txt" ) );
		
		assertFileAndExists( new File( getOutputDir(), "templates.txt" ) );

		assertEquals( null, findFirstString( "$", new File( getOutputDir(),
		"artifacts.txt" ) ) );
		
		assertEquals( "File for artifacts in " + Version.getDefault().getVersion(), findFirstString( "artifacts", new File( getOutputDir(),
		"artifacts.txt" ) ) );
		
	}

	/*public void testFreeMarkerSyntaxFailureExpected() {

		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(getCfg());
		ge.setOutputDirectory(getOutputDir());
		ge.setTemplateName("generictemplates/freemarker.ftl");
		ge.setFilePattern("{class-name}.ftltest");
		ge.start();

	}*/

	public void testClassFileGeneration() {

		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(getCfg());
		ge.setOutputDirectory(getOutputDir());
		ge.setTemplateName("generictemplates/pojo/generic-class.ftl");
		ge.setFilePattern("generic{class-name}.txt");
		ge.start();

		assertFileAndExists( new File( getOutputDir(),
				"genericAuthor.txt" ) );
		
		assertFileAndExists( new File( getOutputDir(),
		"genericArticle.txt" ) );
	}	
	
	public void testPackageFileGeneration() {

		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(getCfg());
		ge.setOutputDirectory(getOutputDir());
		ge.setTemplateName("generictemplates/pojo/generic-class.ftl");
		ge.setFilePattern("{package-name}/generic{class-name}.txt");
		ge.start();

		assertFileAndExists( new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/genericAuthor.txt" ) );
		
		assertFileAndExists( new File( getOutputDir(),
		"org/hibernate/tool/hbm2x/genericArticle.txt" ) );

		assertFileAndExists( new File( getOutputDir(),
		"org/hibernate/tool/hbm2x/genericArticle.txt" ) );		
		
		assertFileAndExists( new File( getOutputDir(),
		"genericUniversalAddress.txt" ) );
		
		assertFileAndExists( new File( getOutputDir(),
		"genericHelloUniverse.txt" ) );
	}

	public void testForEachGeneration() {

		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(getCfg());
		ge.setOutputDirectory(getOutputDir());
		ge.setTemplateName("generictemplates/pojo/generic-class.ftl");
		ge.setFilePattern("{package-name}/generic{class-name}.txt");
		ge.setForEach("entity");
		ge.start();

		assertFileAndExists( new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/genericAuthor.txt" ) );
		
		assertFileAndExists( new File( getOutputDir(),
		"org/hibernate/tool/hbm2x/genericArticle.txt" ) );

		assertFileAndExists( new File( getOutputDir(),
		"org/hibernate/tool/hbm2x/genericArticle.txt" ) );		
		
		assertFalse("component file should not exist", new File( getOutputDir(), "genericUniversalAddress.txt" ).exists());
		
		
		assertFileAndExists( new File( getOutputDir(),
		"genericHelloUniverse.txt" ) );
		
		
		try {
			ge.setForEach( "does, not, exist" );
			ge.start();
			fail();
		} catch(Exception e) {
			//e.printStackTrace();
			//expected
		}
	}

	public void testForEachWithExceptionGeneration() {

		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(getCfg());
		ge.setOutputDirectory(getOutputDir());
		ge.setTemplateName("generictemplates/generic-exception.ftl");
		ge.setFilePattern("{package-name}/generic{class-name}.txt");
		
		try {
			ge.setForEach("entity");
			ge.start();
			fail();
		} catch(ExporterException e) {
			assertTrue(e.getMessage().startsWith("Error while processing Entity:"));			
		}


		try {
			ge.setForEach("component");
			ge.start();
			fail();
		} catch(ExporterException e) {
			assertTrue(e.getMessage().startsWith("Error while processing Component: UniversalAddress"));
		}
		
		try {
			ge.setForEach("configuration");
			ge.start();
			fail();
		} catch(ExporterException e) {
			assertTrue(e.getMessage().startsWith("Error while processing Configuration"));
		}


	}

	public void testPropertySet() throws FileNotFoundException, IOException {
		GenericExporter ge = new GenericExporter();
		ge.setConfiguration(getCfg());
		ge.setOutputDirectory(getOutputDir());
		Properties p = new Properties();
		p.setProperty("proptest", "A value");
		p.setProperty( "refproperty", "proptest=${proptest}" );
		p.setProperty("hibernatetool.booleanProperty", "true");
		p.setProperty("hibernatetool.myTool.toolclass", "org.hibernate.tool.hbm2x.Cfg2JavaTool");
		
		ge.setProperties(p);
		ge.setTemplateName("generictemplates/pojo/generic-class.ftl");
		ge.setFilePattern("{package-name}/generic{class-name}.txt");
		ge.start();		
		
		Properties generated = new Properties();
		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(getOutputDir(), "org/hibernate/tool/hbm2x/genericArticle.txt"));
			generated.load(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		
		assertEquals(generated.getProperty("booleanProperty"), "true");
		assertEquals(generated.getProperty("hibernatetool.booleanProperty"), "true");
		assertNull(generated.getProperty("booleanWasTrue"));
		assertEquals(generated.getProperty("myTool.value"), "value");
		assertEquals(generated.getProperty("refproperty"), "proptest=A value");
		
	}
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/";
	}
	
	
	protected String[] getMappings() {
		return new String[] { "Author.hbm.xml", "Article.hbm.xml", "HelloWorld.hbm.xml"};
	}
}
