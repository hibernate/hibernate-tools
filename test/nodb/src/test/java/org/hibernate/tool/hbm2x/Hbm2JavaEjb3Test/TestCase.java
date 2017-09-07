/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Persistence;

import org.hibernate.Version;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.hbm2x.pojo.AnnotationBuilder;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.IteratorTransformer;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
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
			"Author.hbm.xml",
			"Article.hbm.xml",
			"Train.hbm.xml",
			"Passenger.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File outputDir = null;
	private File resourcesDir = null;
	
	private Metadata metadata = null;
	
	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		metadata = 
				HibernateUtil.initializeMetadata(this, HBM_XML_FILES, resourcesDir);
		FileUtil.generateNoopComparator(outputDir);
		POJOExporter exporter = new POJOExporter();
		exporter.setMetadata(metadata);
		exporter.setOutputDirectory(outputDir);
		exporter.setTemplatePath(new String[0]);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();
	}

	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Author.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Article.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Train.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Passenger.java") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/TransportationPk.java") );
	}

	@Test
	public void testBasicComponent() {
		Assert.assertEquals( 
				"@Embeddable", 
				FileUtil.findFirstString( 
						"@Embeddable", 
						new File(
								outputDir,
								"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/TransportationPk.java")));
	}

	@Test
	public void testCompile() {
		File compiled = new File(temporaryFolder.getRoot(), "compiled");
		compiled.mkdir();
		List<String> jars = new ArrayList<String>();
		jars.add(JavaUtil.resolvePathToJarFileFor(Persistence.class)); // for jpa api
		jars.add(JavaUtil.resolvePathToJarFileFor(Version.class)); // for hibernate core
		JavaUtil.compile(outputDir, compiled, jars);
		JUnitUtil.assertIsNonEmptyFile(new File(
				compiled, 
				"comparator/NoopComparator.class") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				compiled, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Article.class") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				compiled, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Author.class") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				compiled, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Passenger.class") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				compiled, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Train.class") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				compiled, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/TransportationPk.class") );
	}

	@Test
	public void testEqualsHashCode() {
		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test.Passenger");
		POJOClass clazz = new Cfg2JavaTool().getPOJOClass(classMapping);
		Assert.assertFalse(clazz.needsEqualsHashCode());
		classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test.Article");
		clazz = new Cfg2JavaTool().getPOJOClass(classMapping);
		Assert.assertTrue(clazz.needsEqualsHashCode());
	}
	
	@Test
	public void testAnnotationColumnDefaults() {
		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test.Article");
		Cfg2JavaTool cfg2java = new Cfg2JavaTool();
		POJOClass clazz = cfg2java.getPOJOClass(classMapping);
		Property p = classMapping.getProperty("content");
		String string = clazz.generateAnnColumnAnnotation( p );
		Assert.assertNotNull(string);
		Assert.assertEquals(-1, string.indexOf("unique="));
		Assert.assertTrue(string.indexOf("nullable=")>=0);
		Assert.assertEquals(-1, string.indexOf("insertable="));
		Assert.assertEquals(-1, string.indexOf("updatable="));
		Assert.assertTrue(string.indexOf("length=10000")>0);
		p = classMapping.getProperty("name");
		string = clazz.generateAnnColumnAnnotation( p );
		Assert.assertNotNull(string);
		Assert.assertEquals(-1, string.indexOf("unique="));
		Assert.assertTrue(string.indexOf("nullable=")>=0);
		Assert.assertEquals(-1, string.indexOf("insertable="));
		Assert.assertTrue(string.indexOf("updatable=false")>0);
		Assert.assertTrue(string.indexOf("length=100")>0);
		classMapping = metadata.getEntityBinding( "org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test.Train" );
		clazz = cfg2java.getPOJOClass(classMapping);
		p = classMapping.getProperty( "name" );
		string = clazz.generateAnnColumnAnnotation( p );
		Assert.assertNotNull(string);
		Assert.assertTrue(string.indexOf("unique=true")>0);
		Assert.assertTrue(string.indexOf("nullable=")>=0);
		Assert.assertEquals(-1, string.indexOf("insertable="));
		Assert.assertEquals(-1,string.indexOf("updatable="));
		Assert.assertEquals(-1, string.indexOf("length="));
	}
	
	@Test
	public void testEmptyCascade() {
		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test.Article");
		Cfg2JavaTool cfg2java = new Cfg2JavaTool();
		EntityPOJOClass clazz = (EntityPOJOClass) cfg2java.getPOJOClass(classMapping);
		Property property = classMapping.getProperty( "author" );
		Assert.assertEquals(0, clazz.getCascadeTypes( property ).length);
		Assert.assertEquals(
				null,
				FileUtil.findFirstString(
						"cascade={}", 
						new File(
								outputDir, 
								"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Article.java") ));
	}
		
	@Test
	public void testAnnotationBuilder() {
		AnnotationBuilder builder =  AnnotationBuilder.createAnnotation("SingleCleared").resetAnnotation( "Single" );
		Assert.assertEquals("@Single", builder.getResult());
		builder = AnnotationBuilder.createAnnotation("javax.persistence.OneToMany")
				    .addAttribute("willbecleared", (String)null)
				    .resetAnnotation("javax.persistence.OneToMany")
					.addAttribute("cascade", new String[] { "val1", "val2"})
					.addAttribute("fetch", "singleValue");
		Assert.assertEquals("@javax.persistence.OneToMany(cascade={val1, val2}, fetch=singleValue)", builder.getResult());
		builder = AnnotationBuilder.createAnnotation("javax.persistence.OneToMany");
		builder.addAttribute("cascade", (String[])null);
		builder.addAttribute("fetch", (String)null);
		Assert.assertEquals("@javax.persistence.OneToMany", builder.getResult());
		builder = AnnotationBuilder.createAnnotation("abc");
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(new Integer(42));
		list.add( new String("xxx") );
		builder.addQuotedAttributes( "it", list.iterator() );
		Assert.assertEquals("@abc(it={\"42\", \"xxx\"})", builder.getResult());		
		List<String> columns = new ArrayList<String>();
		columns.add("first");
		columns.add("second");
		AnnotationBuilder constraint = AnnotationBuilder.createAnnotation( "UniqueConstraint" );
		constraint.addQuotedAttributes( "columnNames", new IteratorTransformer<String>(columns.iterator()) {
			public String transform(String object) {					
				return object.toString();
			}
		});
		constraint.addAttribute( "single", "value" );	
		String attribute = constraint.getAttributeAsString("columnNames");
		Assert.assertEquals("{\"first\", \"second\"}", attribute);
		Assert.assertEquals("value", constraint.getAttributeAsString( "single" ));
	}
	
}
