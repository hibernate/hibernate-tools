/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Persistence;

import org.hibernate.Version;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
	
	@TempDir
	public File outputFolder = new File("output");
	
	private File srcDir = null;
	private File resourcesDir = null;
	
	private Metadata metadata = null;
	
	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "output");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		metadata = metadataDescriptor.createMetadata();
		FileUtil.generateNoopComparator(srcDir);
		POJOExporter exporter = new POJOExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(srcDir);
		exporter.setTemplatePath(new String[0]);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();
	}

	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Author.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Article.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Train.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Passenger.java") );
		JUnitUtil.assertIsNonEmptyFile(new File(
				srcDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/TransportationPk.java") );
	}

	@Test
	public void testBasicComponent() {
		assertEquals( 
				"@Embeddable", 
				FileUtil.findFirstString( 
						"@Embeddable", 
						new File(
								srcDir,
								"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/TransportationPk.java")));
	}

	@Test
	public void testCompile() {
		File compiled = new File(outputFolder, "compiled");
		compiled.mkdir();
		List<String> jars = new ArrayList<String>();
		jars.add(JavaUtil.resolvePathToJarFileFor(Persistence.class)); // for jpa api
		jars.add(JavaUtil.resolvePathToJarFileFor(Version.class)); // for hibernate core
		JavaUtil.compile(srcDir, compiled, jars);
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
		assertFalse(clazz.needsEqualsHashCode());
		classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test.Article");
		clazz = new Cfg2JavaTool().getPOJOClass(classMapping);
		assertTrue(clazz.needsEqualsHashCode());
	}
	
	@Test
	public void testAnnotationColumnDefaults() {
		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test.Article");
		Cfg2JavaTool cfg2java = new Cfg2JavaTool();
		POJOClass clazz = cfg2java.getPOJOClass(classMapping);
		Property p = classMapping.getProperty("content");
		String string = clazz.generateAnnColumnAnnotation( p );
		assertNotNull(string);
		assertEquals(-1, string.indexOf("unique="));
		assertTrue(string.indexOf("nullable=")>=0);
		assertEquals(-1, string.indexOf("insertable="));
		assertEquals(-1, string.indexOf("updatable="));
		assertTrue(string.indexOf("length=10000")>0);
		p = classMapping.getProperty("name");
		string = clazz.generateAnnColumnAnnotation( p );
		assertNotNull(string);
		assertEquals(-1, string.indexOf("unique="));
		assertTrue(string.indexOf("nullable=")>=0);
		assertEquals(-1, string.indexOf("insertable="));
		assertTrue(string.indexOf("updatable=false")>0);
		assertTrue(string.indexOf("length=100")>0);
		classMapping = metadata.getEntityBinding( "org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test.Train" );
		clazz = cfg2java.getPOJOClass(classMapping);
		p = classMapping.getProperty( "name" );
		string = clazz.generateAnnColumnAnnotation( p );
		assertNotNull(string);
		assertTrue(string.indexOf("unique=true")>0);
		assertTrue(string.indexOf("nullable=")>=0);
		assertEquals(-1, string.indexOf("insertable="));
		assertEquals(-1,string.indexOf("updatable="));
		assertEquals(-1, string.indexOf("length="));
	}
	
	@Test
	public void testEmptyCascade() {
		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaEjb3Test.Article");
		Cfg2JavaTool cfg2java = new Cfg2JavaTool();
		EntityPOJOClass clazz = (EntityPOJOClass) cfg2java.getPOJOClass(classMapping);
		Property property = classMapping.getProperty( "author" );
		assertEquals(0, clazz.getCascadeTypes( property ).length);
		assertEquals(
				null,
				FileUtil.findFirstString(
						"cascade={}", 
						new File(
								srcDir, 
								"org/hibernate/tool/hbm2x/Hbm2JavaEjb3Test/Article.java") ));
	}
		
	@Test
	public void testAnnotationBuilder() {
		AnnotationBuilder builder =  AnnotationBuilder.createAnnotation("SingleCleared").resetAnnotation( "Single" );
		assertEquals("@Single", builder.getResult());
		builder = AnnotationBuilder.createAnnotation("javax.persistence.OneToMany")
				    .addAttribute("willbecleared", (String)null)
				    .resetAnnotation("javax.persistence.OneToMany")
					.addAttribute("cascade", new String[] { "val1", "val2"})
					.addAttribute("fetch", "singleValue");
		assertEquals("@javax.persistence.OneToMany(cascade={val1, val2}, fetch=singleValue)", builder.getResult());
		builder = AnnotationBuilder.createAnnotation("javax.persistence.OneToMany");
		builder.addAttribute("cascade", (String[])null);
		builder.addAttribute("fetch", (String)null);
		assertEquals("@javax.persistence.OneToMany", builder.getResult());
		builder = AnnotationBuilder.createAnnotation("abc");
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(new Integer(42));
		list.add( new String("xxx") );
		builder.addQuotedAttributes( "it", list.iterator() );
		assertEquals("@abc(it={\"42\", \"xxx\"})", builder.getResult());		
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
		assertEquals("{\"first\", \"second\"}", attribute);
		assertEquals("value", constraint.getAttributeAsString( "single" ));
	}
	
}
