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

package org.hibernate.tool.hbm2x.Hbm2JavaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.hbm2x.pojo.BasicPOJOClass;
import org.hibernate.tool.hbm2x.pojo.ImportContext;
import org.hibernate.tool.hbm2x.pojo.ImportContextImpl;
import org.hibernate.tool.hbm2x.pojo.NoopImportContext;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Customer.hbm.xml", 
			"Order.hbm.xml",
			"LineItem.hbm.xml", 
			"Product.hbm.xml", 
			"HelloWorld.hbm.xml", 
			"Train.hbm.xml", 
			"Passenger.hbm.xml"
	};
	
	@TempDir
	public File outputFolder = new File("output");
	
	private Metadata metadata = null;
	private MetadataDescriptor metadataDescriptor = null;
	private File scDir = null;
	private File resourcesDir = null;
	private ArtifactCollector artifactCollector = null;
	
	@BeforeEach
	public void setUp() throws Exception {
		scDir = new File(outputFolder, "output");
		scDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		metadata = metadataDescriptor.createMetadata();
		POJOExporter exporter = new POJOExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(scDir);
		artifactCollector = new ArtifactCollector();
		exporter.setArtifactCollector(artifactCollector);
		exporter.start();
	}

	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				scDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/Customer.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				scDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/LineItem.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				scDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/Order.java"));
		JUnitUtil.assertIsNonEmptyFile(new File( 
				scDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/Train.java"));
		JUnitUtil.assertIsNonEmptyFile(new File( 
				scDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/Passenger.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				scDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/Product.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				scDir,
				"generated/BaseHelloWorld.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				scDir, 
				"HelloUniverse.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				scDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/FatherComponent.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				scDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/ChildComponent.java"));
		assertEquals(15, artifactCollector.getFileCount("java"));
	}
	
	// TODO Re-enable this test: HBX-1248
	@Disabled
	@Test
	public void testCompilable() throws Exception {
		String helloWorldResourcePath = "/org/hibernate/tool/hbm2x/Hbm2JavaTest/HelloWorld.java_";
		File helloWorldOrigin = new File(getClass().getResource(helloWorldResourcePath).toURI());
		File helloWorldDestination = new File(scDir, "HelloWorld.java");
		File targetDir = new File(outputFolder, "compilerOutput" );
		targetDir.mkdir();	
		Files.copy(helloWorldOrigin.toPath(), helloWorldDestination.toPath());
		JavaUtil.compile(scDir, targetDir);
		assertTrue(new File(targetDir, "HelloWorld.class").exists());
	}

	//  TODO Implement HBX-606 so that the following test succeeds
	@Disabled
	@Test
	public void testParentComponentFailureExpected() {		
		File file = new File(
				scDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/FatherComponent.java");		
		assertEquals(
				"test", 
				FileUtil.findFirstString(
						"testParent", 
						file));
	}
	
	@Test
	public void testNoFreeMarkerLeftOvers() {
		assertNull(FileUtil.findFirstString(
				"$", 
				new File( 
						scDir,
						"org/hibernate/tool/hbm2x/Hbm2JavaTest/Customer.java")));
		assertNull(FileUtil.findFirstString( 
				"$", 
				new File(
						scDir,
						"org/hibernate/tool/hbm2x/Hbm2JavaTest/LineItem.java")));
		assertNull(FileUtil.findFirstString(
				"$", 
				new File(
						scDir,
						"org/hibernate/tool/hbm2x/Hbm2JavaTest/Order.java")));
		assertNull(FileUtil.findFirstString(
				"$", 
				new File(
						scDir,
						"org/hibernate/tool/hbm2x/Hbm2JavaTest/Product.java")));
		assertNull(FileUtil.findFirstString(
				"$", 
				new File(
						scDir,
						"org/hibernate/tool/hbm2x/Hbm2JavaTest/Address.java")));
	}

	@Test
	public void testPackageName() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaTest.Order");
		POJOClass pc = c2j.getPOJOClass(classMapping);
		assertEquals( "org.hibernate.tool.hbm2x.Hbm2JavaTest", pc.getPackageName() );
		assertEquals( "package org.hibernate.tool.hbm2x.Hbm2JavaTest;", pc.getPackageDeclaration() );
		assertEquals( 
				"package generated;", 
				c2j.getPOJOClass(metadata.getEntityBinding("HelloWorld"))
					.getPackageDeclaration(),
				"did not honor generated-class");
	}
	
	// TODO Re-enable this test: HBX-1242
	@Disabled
	@Test
	public void testFieldNotThere() {
		assertNull(FileUtil.findFirstString(
				"notgenerated",
				new File(
						scDir,
						"HelloUniverse.java")));
	}

	@Test
	public void testJavaDoc() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		assertEquals( " * test", c2j.toJavaDoc( "test", 0 ) );
		assertEquals( "   * test", c2j.toJavaDoc( "test", 2 ) );
		assertEquals( "   * test\n   * me", c2j.toJavaDoc( "test\nme", 2 ) );
		PersistentClass local = metadata.getEntityBinding( "HelloWorld" );
		POJOClass pc = c2j.getPOJOClass(local);
		assertEquals( " * Hey there", pc.getClassJavaDoc( "fallback", 0 ) );
		assertEquals( 
				" * Test Field Description", 
				pc.getFieldJavaDoc(local.getIdentifierProperty(), 0 ) );
	}

	@Test
	public void testExtraCode() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		assertFalse(c2j.hasMetaAttribute(
				metadata.getEntityBinding("HelloWorld" ), "class-code" ) );
		PersistentClass classMapping = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		assertEquals(
				"// extra code line 1\n// extra code line 2\n{ Collator.getInstance(); }",
				c2j.getPOJOClass(classMapping).getExtraClassCode() );
	}

	@Test
	public void testScope() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		assertEquals( "public strictfp", c2j.getClassModifiers( pc ) );
		assertEquals("public", c2j.getClassModifiers(metadata.getEntityBinding( "HelloWorld" ) ) );
	}

	@Test
	public void testDeclarationType() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		assertEquals( "class", c2j.getPOJOClass(pc).getDeclarationType() );
		assertEquals( "interface", c2j.getPOJOClass(metadata.getEntityBinding( "HelloWorld" ) ).getDeclarationType() );
	}

	@Test
	public void testTypeName() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		Property property = pc.getProperty( "lineItems" );
		assertEquals( "java.util.Collection", c2j.getJavaTypeName( property, false ) );
	}

	@Test
	public void testUseRawTypeNullability() {
		Cfg2JavaTool c2j = new Cfg2JavaTool( /*true*/ );
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Product" );
		Property property = pc.getProperty( "numberAvailable" );
		assertFalse( property.getValue().isNullable() );
		assertEquals( 
				"int", 
				c2j.getJavaTypeName( property, false ),
				"typename should be used when rawtypemode");
		property = pc.getProperty( "minStock" );
		assertTrue( property.getValue().isNullable() );
		assertEquals( 
				"long", 
				c2j.getJavaTypeName( property, false ),
				"typename should be used when rawtypemode");
		property = pc.getProperty( "otherStock" );
		assertFalse( property.getValue().isNullable() );
		assertEquals(
				"java.lang.Integer", 
				c2j.getJavaTypeName( property, false ),
				"type should still be overriden by meta attribute");
		property = pc.getIdentifierProperty();
		assertFalse( property.getValue().isNullable() );
		assertEquals( 
				"long", 
				c2j.getJavaTypeName( property, false ),
				"wrappers should be used by default");
		pc = metadata.getEntityBinding( "org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer" );
		Component identifier = (Component) pc.getIdentifier();
		assertFalse(((Property) identifier.getPropertyIterator().next() )
				.getValue().isNullable() );
		assertEquals( "long", c2j.getJavaTypeName( property, false ) );
	}

	@Test
	public void testExtendsImplements() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		assertEquals( null, c2j.getPOJOClass(pc).getExtends() );
		POJOClass entityPOJOClass = c2j.getPOJOClass(metadata.getEntityBinding("HelloWorld" ));
		assertEquals( "Comparable", entityPOJOClass.getExtends() );
		assertNull(
				entityPOJOClass.getImplements(),
				"should be interface which cannot have implements");
		assertEquals(
				"",
				entityPOJOClass.getImplementsDeclaration(),
				"should be interface which cannot have implements");
		PersistentClass base = new RootClass(null);
		base.setClassName( "Base" );
		PersistentClass sub = new SingleTableSubclass( base, null );
		sub.setClassName( "Sub" );
		assertEquals( null, c2j.getPOJOClass(base).getExtends() );
		assertEquals( "Base", c2j.getPOJOClass(sub).getExtends() );
		Map<String, MetaAttribute> m = new HashMap<String, MetaAttribute>();
		MetaAttribute attribute = new MetaAttribute( "extends" );
		attribute.addValue( "x" );
		attribute.addValue( "y" );
		m.put( attribute.getName(), attribute );
		attribute = new MetaAttribute( "interface" );
		attribute.addValue( "true" );
		m.put( attribute.getName(), attribute );
		sub.setMetaAttributes( m );
		assertEquals( "Base,x,y", c2j.getPOJOClass(sub).getExtends() );
		m = new HashMap<String, MetaAttribute>();
		attribute = new MetaAttribute( "implements" );
		attribute.addValue( "intf" );
		m.put( attribute.getName(), attribute );
		base.setMetaAttributes( m );
		assertEquals( "intf,java.io.Serializable", c2j.getPOJOClass(base).getImplements() );
	}

	@Test
	public void testDeclarationName() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		PersistentClass hw = metadata.getEntityBinding( "HelloWorld" );
		POJOClass epc = c2j.getPOJOClass(pc);
		assertEquals( "Order", epc.getDeclarationName() );	
		epc = c2j.getPOJOClass(hw);
		assertEquals( "BaseHelloWorld", epc.getDeclarationName() );
	}

	@Test
	public void testAsArguments() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		assertEquals(
				"java.util.Calendar orderDate, java.math.BigDecimal total, org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer customer, java.util.Collection lineItems",
				c2j.asParameterList( 
						pc.getPropertyIterator(), false, new NoopImportContext() ));
		assertEquals( 
				"orderDate, total, customer, lineItems", 
				c2j.asArgumentList( pc.getPropertyIterator() ) );
	}

	@Test
	public void testPropertiesForFullConstructor() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding( "HelloWorld" );
		POJOClass pjc = c2j.getPOJOClass(pc);
		List<Property> wl = pjc.getPropertiesForFullConstructor();
		assertEquals( 3, wl.size() );
		PersistentClass uni = metadata.getEntityBinding( "HelloUniverse" );
		pjc = c2j.getPOJOClass(uni);
		List<Property> local = pjc.getPropertyClosureForFullConstructor();
		assertEquals( 6, local.size() );
		for(int i=0;i<wl.size();i++) {
			assertEquals(local.get( i ), wl.get( i ),  i + " position should be the same" );
		}
	}

	@Test
	public void testToString() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding( "HelloWorld" );
		POJOClass pjc = c2j.getPOJOClass(pc);
		assertTrue( pjc.needsToString() );
		Iterator<Property> iter = pjc.getToStringPropertiesIterator();
		// in HelloWorld.hbm.xml there're 2 Properties for toString
		assertEquals( "id", (iter.next() ).getName() );
		assertEquals( "hello", (iter.next() ).getName() );
		assertFalse( iter.hasNext() );
		pc = metadata.getEntityBinding( "org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		pjc = c2j.getPOJOClass(pc);
		assertFalse( pjc.needsToString() );
		pc = metadata.getEntityBinding( "org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer" );
		Component c = (Component) pc.getProperty( "addressComponent" )
				.getValue();		
		POJOClass cc = c2j.getPOJOClass(c);
		assertTrue( cc.needsToString() );
		iter = cc.getToStringPropertiesIterator();	
		// in Customer.hbm.xml there's 1 Property for toString
		assertEquals( "city", (iter.next() ).getName() );
		assertFalse( iter.hasNext() );
	}

	@Test
	public void testImportOfSameName() {
		ImportContext ic = new ImportContextImpl("foobar");
		assertEquals("CascadeType", ic.importType("javax.persistence.CascadeType"));
		assertEquals("org.hibernate.annotations.CascadeType", ic.importType("org.hibernate.annotations.CascadeType"));
		assertTrue(ic.generateImports().indexOf("hibernate")<0, "The hibernate annotation should not be imported to avoid name clashes");	
	}
	
	@Test
	public void testImporter() {
		ImportContext context = new ImportContextImpl( "org.hibernate" );
		assertEquals("byte", context.importType("byte"));
		assertEquals("Session", context.importType("org.hibernate.Session"));
		assertEquals("Long", context.importType("java.lang.Long"));
		assertEquals("org.test.Session", context.importType("org.test.Session"));	
		assertEquals("Entity", context.importType("org.test.Entity"));
		assertEquals("org.other.test.Entity", context.importType("org.other.test.Entity"));		
		assertEquals("Collection<org.marvel.Hulk>", context.importType("java.util.Collection<org.marvel.Hulk>"));
		assertEquals("Map<java.lang.String, org.marvel.Hulk>", context.importType("java.util.Map<java.lang.String, org.marvel.Hulk>"));
		assertEquals("Collection<org.marvel.Hulk>[]", context.importType("java.util.Collection<org.marvel.Hulk>[]"));
		assertEquals("Map<java.lang.String, org.marvel.Hulk>", context.importType("java.util.Map<java.lang.String, org.marvel.Hulk>"));		
		String string = context.generateImports();
		assertTrue(string.indexOf("import org.hibernate.Session;")<0);
		assertTrue(string.indexOf("import org.test.Entity;")>0);
		assertTrue(string.indexOf("import org.other.test.Entity;")<0, "Entity can only be imported once");		
		assertFalse(string.indexOf("<")>=0);
		assertEquals("Outer.Entity", context.importType("org.test.Outer$Entity"));
		assertEquals("org.other.test.Outer.Entity", context.importType("org.other.test.Outer$Entity"));
		assertEquals("Collection<org.marvel.Outer.Hulk>", context.importType("java.util.Collection<org.marvel.Outer$Hulk>"));
		assertEquals("Map<java.lang.String, org.marvel.Outer.Hulk>", context.importType("java.util.Map<java.lang.String, org.marvel.Outer$Hulk>"));
		assertEquals("Collection<org.marvel.Outer.Hulk>[]", context.importType("java.util.Collection<org.marvel.Outer$Hulk>[]"));
		assertEquals("Map<java.lang.String, org.marvel.Outer.Hulk>", context.importType("java.util.Map<java.lang.String, org.marvel.Outer$Hulk>"));
		//assertEquals("Test.Entry", context.importType("org.hibernate.Test.Entry")); what should be the behavior for this ?
		assertEquals("Test.Entry", context.importType("org.hibernate.Test$Entry"));
		assertEquals("Map.Entry", context.importType("java.util.Map$Entry"));
		assertEquals("Entry", context.importType("java.util.Map.Entry")); // we can't detect that it is the same class here unless we try an load all strings so we fall back to default class name.
		assertEquals("List<java.util.Map.Entry>", context.importType( "java.util.List<java.util.Map$Entry>" ));
		assertEquals("List<org.hibernate.Test.Entry>", context.importType( "java.util.List<org.hibernate.Test$Entry>" ));
		string = context.generateImports();
		assertTrue(string.indexOf("import java.util.Map")>=0);
		assertTrue(string.indexOf("import java.utilMap$")<0);
		assertTrue(string.indexOf("$")<0);
	}
	
	@Test
	public void testEqualsHashCode() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding( "org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer" );
		POJOClass pjc = c2j.getPOJOClass((Component) pc.getProperty("addressComponent").getValue());
		assertTrue( pjc.needsEqualsHashCode() );
		Iterator<Property> iter = pjc.getEqualsHashCodePropertiesIterator();
		// in HelloWorld.hbm.xml there're 2 Properties for toString
		assertEquals( "streetAddress1",  iter.next().getName() );
		assertEquals( "city", iter.next().getName() );
		assertEquals( "verified", iter.next().getName() );
		assertFalse( iter.hasNext() );
	}
	
	// TODO Re-enable this test: HBX-1249
	@Disabled
	@Test
	public void testGenerics() throws Exception {
		File genericsSource = new File(outputFolder, "genericssource");
		POJOExporter exporter = new POJOExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(genericsSource);
		artifactCollector = new ArtifactCollector();
		exporter.setArtifactCollector(artifactCollector);
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();
		File genericsTarget = new File(outputFolder, "genericstarget" );
		genericsTarget.mkdir();
		String helloWorldResourcePath = "/org/hibernate/tool/hbm2x/Hbm2JavaTest/HelloWorld.java_";
		File helloWorldOrigin = new File(getClass().getResource(helloWorldResourcePath).toURI());
		File helloWorldDestination = new File(scDir, "HelloWorld.java");
		Files.copy(helloWorldOrigin.toPath(), helloWorldDestination.toPath());
		JavaUtil.compile(genericsSource, genericsTarget);
		assertTrue(new File(genericsTarget, "HelloWorld.class").exists());
	}
	
	@Disabled
	@Test
	public void testDynamicComponent() {
		PersistentClass classMapping = 
				metadata.getEntityBinding(
						"org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer");
		assertEquals(
				"java.util.Map", 
				new Cfg2JavaTool().getJavaTypeName(
						classMapping.getProperty("dynaMap"), false));
	}
	
	@Test
	public void testCapitializaiton() {
		assertEquals("Mail", BasicPOJOClass.beanCapitalize("Mail"));
		assertEquals("Mail", BasicPOJOClass.beanCapitalize("mail"));
		assertEquals("eMail", BasicPOJOClass.beanCapitalize("eMail"));
		assertEquals("EMail", BasicPOJOClass.beanCapitalize("EMail"));
	}
	
	@Test
	public void testUserTypes() {
		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer");
		Property property = classMapping.getProperty("customDate");
		assertEquals("java.sql.Date", new Cfg2JavaTool().getJavaTypeName(property, false));	
	}
	
}
