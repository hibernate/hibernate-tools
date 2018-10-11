/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.Hbm2JavaTest;

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
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.pojo.BasicPOJOClass;
import org.hibernate.tool.internal.export.pojo.Cfg2JavaTool;
import org.hibernate.tool.internal.export.pojo.ImportContext;
import org.hibernate.tool.internal.export.pojo.ImportContextImpl;
import org.hibernate.tool.internal.export.pojo.NoopImportContext;
import org.hibernate.tool.internal.export.pojo.POJOClass;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Metadata metadata = null;
	private MetadataDescriptor metadataDescriptor = null;
	private File outputDir = null;
	private File resourcesDir = null;
	private DefaultArtifactCollector artifactCollector = null;
	
	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		metadata = metadataDescriptor.createMetadata();
		Exporter exporter = ExporterFactory.createExporter(ExporterType.POJO);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		artifactCollector = new DefaultArtifactCollector();
		exporter.getProperties().put(ExporterConstants.ARTIFACT_COLLECTOR, artifactCollector);
		exporter.start();
	}

	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/Customer.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/LineItem.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/Order.java"));
		JUnitUtil.assertIsNonEmptyFile(new File( 
				outputDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/Train.java"));
		JUnitUtil.assertIsNonEmptyFile(new File( 
				outputDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/Passenger.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/Product.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir,
				"generated/BaseHelloWorld.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"HelloUniverse.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/FatherComponent.java"));
		JUnitUtil.assertIsNonEmptyFile(new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/ChildComponent.java"));
		Assert.assertEquals(15, artifactCollector.getFileCount("java"));
	}
	
	// TODO Re-enable this test: HBX-1248
	@Ignore
	@Test
	public void testCompilable() throws Exception {
		String helloWorldResourcePath = "/org/hibernate/tool/hbm2x/Hbm2JavaTest/HelloWorld.java_";
		File helloWorldOrigin = new File(getClass().getResource(helloWorldResourcePath).toURI());
		File helloWorldDestination = new File(outputDir, "HelloWorld.java");
		File targetDir = new File(temporaryFolder.getRoot(), "compilerOutput" );
		targetDir.mkdir();	
		Files.copy(helloWorldOrigin.toPath(), helloWorldDestination.toPath());
		JavaUtil.compile(outputDir, targetDir);
		Assert.assertTrue(new File(targetDir, "HelloWorld.class").exists());
	}

	//  TODO Implement HBX-606 so that the following test succeeds
	@Ignore
	@Test
	public void testParentComponentFailureExpected() {		
		File file = new File(
				outputDir, 
				"org/hibernate/tool/hbm2x/Hbm2JavaTest/FatherComponent.java");		
		Assert.assertEquals(
				"test", 
				FileUtil.findFirstString(
						"testParent", 
						file));
	}
	
	@Test
	public void testNoFreeMarkerLeftOvers() {
		Assert.assertNull(FileUtil.findFirstString(
				"$", 
				new File( 
						outputDir,
						"org/hibernate/tool/hbm2x/Hbm2JavaTest/Customer.java")));
		Assert.assertNull(FileUtil.findFirstString( 
				"$", 
				new File(
						outputDir,
						"org/hibernate/tool/hbm2x/Hbm2JavaTest/LineItem.java")));
		Assert.assertNull(FileUtil.findFirstString(
				"$", 
				new File(
						outputDir,
						"org/hibernate/tool/hbm2x/Hbm2JavaTest/Order.java")));
		Assert.assertNull(FileUtil.findFirstString(
				"$", 
				new File(
						outputDir,
						"org/hibernate/tool/hbm2x/Hbm2JavaTest/Product.java")));
		Assert.assertNull(FileUtil.findFirstString(
				"$", 
				new File(
						outputDir,
						"org/hibernate/tool/hbm2x/Hbm2JavaTest/Address.java")));
	}

	@Test
	public void testPackageName() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaTest.Order");
		POJOClass pc = c2j.getPOJOClass(classMapping);
		Assert.assertEquals( "org.hibernate.tool.hbm2x.Hbm2JavaTest", pc.getPackageName() );
		Assert.assertEquals( "package org.hibernate.tool.hbm2x.Hbm2JavaTest;", pc.getPackageDeclaration() );
		Assert.assertEquals( 
				"did not honor generated-class", 
				"package generated;", 
				c2j.getPOJOClass(metadata.getEntityBinding("HelloWorld"))
					.getPackageDeclaration());
	}
	
	// TODO Re-enable this test: HBX-1242
	@Ignore
	@Test
	public void testFieldNotThere() {
		Assert.assertNull(FileUtil.findFirstString(
				"notgenerated", 
				new File(
						outputDir,
						"HelloUniverse.java")));
	}

	@Test
	public void testJavaDoc() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		Assert.assertEquals( " * test", c2j.toJavaDoc( "test", 0 ) );
		Assert.assertEquals( "   * test", c2j.toJavaDoc( "test", 2 ) );
		Assert.assertEquals( "   * test\n   * me", c2j.toJavaDoc( "test\nme", 2 ) );
		PersistentClass local = metadata.getEntityBinding( "HelloWorld" );
		POJOClass pc = c2j.getPOJOClass(local);
		Assert.assertEquals( " * Hey there", pc.getClassJavaDoc( "fallback", 0 ) );
		Assert.assertEquals( 
				" * Test Field Description", 
				pc.getFieldJavaDoc(local.getIdentifierProperty(), 0 ) );
	}

	@Test
	public void testExtraCode() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		Assert.assertFalse(c2j.hasMetaAttribute(
				metadata.getEntityBinding("HelloWorld" ), "class-code" ) );
		PersistentClass classMapping = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		Assert.assertEquals(
				"// extra code line 1\n// extra code line 2\n{ Collator.getInstance(); }",
				c2j.getPOJOClass(classMapping).getExtraClassCode() );
	}

	@Test
	public void testScope() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		Assert.assertEquals( "public strictfp", c2j.getClassModifiers( pc ) );
		Assert.assertEquals("public", c2j.getClassModifiers(metadata.getEntityBinding( "HelloWorld" ) ) );
	}

	@Test
	public void testDeclarationType() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		Assert.assertEquals( "class", c2j.getPOJOClass(pc).getDeclarationType() );
		Assert.assertEquals( "interface", c2j.getPOJOClass(metadata.getEntityBinding( "HelloWorld" ) ).getDeclarationType() );
	}

	@Test
	public void testTypeName() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		Property property = pc.getProperty( "lineItems" );
		Assert.assertEquals( "java.util.Collection", c2j.getJavaTypeName( property, false ) );
	}

	@Test
	public void testUseRawTypeNullability() {
		Cfg2JavaTool c2j = new Cfg2JavaTool( /*true*/ );
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Product" );
		Property property = pc.getProperty( "numberAvailable" );
		Assert.assertFalse( property.getValue().isNullable() );
		Assert.assertEquals( 
				"typename should be used when rawtypemode", 
				"int", 
				c2j.getJavaTypeName( property, false ) );
		property = pc.getProperty( "minStock" );
		Assert.assertTrue( property.getValue().isNullable() );
		Assert.assertEquals( 
				"typename should be used when rawtypemode", 
				"long", 
				c2j.getJavaTypeName( property, false ) );
		property = pc.getProperty( "otherStock" );
		Assert.assertFalse( property.getValue().isNullable() );
		Assert.assertEquals(
				"type should still be overriden by meta attribute",
				"java.lang.Integer", 
				c2j.getJavaTypeName( property, false ) );
		property = pc.getIdentifierProperty();
		Assert.assertFalse( property.getValue().isNullable() );
		Assert.assertEquals( 
				"wrappers should be used by default", 
				"long", 
				c2j.getJavaTypeName( property, false ) );
		pc = metadata.getEntityBinding( "org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer" );
		Component identifier = (Component) pc.getIdentifier();
		Assert.assertFalse(((Property) identifier.getPropertyIterator().next() )
				.getValue().isNullable() );
		Assert.assertEquals( "long", c2j.getJavaTypeName( property, false ) );
	}

	@Test
	public void testExtendsImplements() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		Assert.assertEquals( null, c2j.getPOJOClass(pc).getExtends() );
		POJOClass entityPOJOClass = c2j.getPOJOClass(metadata.getEntityBinding("HelloWorld" ));
		Assert.assertEquals( "Comparable", entityPOJOClass.getExtends() );
		Assert.assertNull(
				"should be interface which cannot have implements",
				entityPOJOClass.getImplements() );
		Assert.assertEquals(
				"should be interface which cannot have implements", 
				"",
				entityPOJOClass.getImplementsDeclaration() );
		PersistentClass base = new RootClass(null);
		base.setClassName( "Base" );
		PersistentClass sub = new SingleTableSubclass( base, null );
		sub.setClassName( "Sub" );
		Assert.assertEquals( null, c2j.getPOJOClass(base).getExtends() );
		Assert.assertEquals( "Base", c2j.getPOJOClass(sub).getExtends() );
		Map<String, MetaAttribute> m = new HashMap<String, MetaAttribute>();
		MetaAttribute attribute = new MetaAttribute( "extends" );
		attribute.addValue( "x" );
		attribute.addValue( "y" );
		m.put( attribute.getName(), attribute );
		attribute = new MetaAttribute( "interface" );
		attribute.addValue( "true" );
		m.put( attribute.getName(), attribute );
		sub.setMetaAttributes( m );
		Assert.assertEquals( "Base,x,y", c2j.getPOJOClass(sub).getExtends() );
		m = new HashMap<String, MetaAttribute>();
		attribute = new MetaAttribute( "implements" );
		attribute.addValue( "intf" );
		m.put( attribute.getName(), attribute );
		base.setMetaAttributes( m );
		Assert.assertEquals( "intf,java.io.Serializable", c2j.getPOJOClass(base).getImplements() );
	}

	@Test
	public void testDeclarationName() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		PersistentClass hw = metadata.getEntityBinding( "HelloWorld" );
		POJOClass epc = c2j.getPOJOClass(pc);
		Assert.assertEquals( "Order", epc.getDeclarationName() );	
		epc = c2j.getPOJOClass(hw);
		Assert.assertEquals( "BaseHelloWorld", epc.getDeclarationName() );
	}

	@Test
	public void testAsArguments() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding(
				"org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		Assert.assertEquals(
				"java.util.Calendar orderDate, java.math.BigDecimal total, org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer customer, java.util.Collection lineItems",
				c2j.asParameterList( 
						pc.getPropertyIterator(), false, new NoopImportContext() ));
		Assert.assertEquals( 
				"orderDate, total, customer, lineItems", 
				c2j.asArgumentList( pc.getPropertyIterator() ) );
	}

	@Test
	public void testPropertiesForFullConstructor() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding( "HelloWorld" );
		POJOClass pjc = c2j.getPOJOClass(pc);
		List<Property> wl = pjc.getPropertiesForFullConstructor();
		Assert.assertEquals( 3, wl.size() );
		PersistentClass uni = metadata.getEntityBinding( "HelloUniverse" );
		pjc = c2j.getPOJOClass(uni);
		List<Property> local = pjc.getPropertyClosureForFullConstructor();
		Assert.assertEquals( 6, local.size() );
		for(int i=0;i<wl.size();i++) {
			Assert.assertEquals( i + " position should be the same", local.get( i ), wl.get( i ) );
		}
	}

	@Test
	public void testToString() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding( "HelloWorld" );
		POJOClass pjc = c2j.getPOJOClass(pc);
		Assert.assertTrue( pjc.needsToString() );
		Iterator<Property> iter = pjc.getToStringPropertiesIterator();
		// in HelloWorld.hbm.xml there're 2 Properties for toString
		Assert.assertEquals( "id", (iter.next() ).getName() );
		Assert.assertEquals( "hello", (iter.next() ).getName() );
		Assert.assertFalse( iter.hasNext() );
		pc = metadata.getEntityBinding( "org.hibernate.tool.hbm2x.Hbm2JavaTest.Order" );
		pjc = c2j.getPOJOClass(pc);
		Assert.assertFalse( pjc.needsToString() );
		pc = metadata.getEntityBinding( "org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer" );
		Component c = (Component) pc.getProperty( "addressComponent" )
				.getValue();		
		POJOClass cc = c2j.getPOJOClass(c);
		Assert.assertTrue( cc.needsToString() );
		iter = cc.getToStringPropertiesIterator();	
		// in Customer.hbm.xml there's 1 Property for toString
		Assert.assertEquals( "city", (iter.next() ).getName() );
		Assert.assertFalse( iter.hasNext() );
	}

	@Test
	public void testImportOfSameName() {
		ImportContext ic = new ImportContextImpl("foobar");
		Assert.assertEquals("CascadeType", ic.importType("javax.persistence.CascadeType"));
		Assert.assertEquals("org.hibernate.annotations.CascadeType", ic.importType("org.hibernate.annotations.CascadeType"));
		Assert.assertTrue("The hibernate annotation should not be imported to avoid name clashes", ic.generateImports().indexOf("hibernate")<0);	
	}
	
	@Test
	public void testImporter() {
		ImportContext context = new ImportContextImpl( "org.hibernate" );
		Assert.assertEquals("byte", context.importType("byte"));
		Assert.assertEquals("Session", context.importType("org.hibernate.Session"));
		Assert.assertEquals("Long", context.importType("java.lang.Long"));
		Assert.assertEquals("org.test.Session", context.importType("org.test.Session"));	
		Assert.assertEquals("Entity", context.importType("org.test.Entity"));
		Assert.assertEquals("org.other.test.Entity", context.importType("org.other.test.Entity"));		
		Assert.assertEquals("Collection<org.marvel.Hulk>", context.importType("java.util.Collection<org.marvel.Hulk>"));
		Assert.assertEquals("Map<java.lang.String, org.marvel.Hulk>", context.importType("java.util.Map<java.lang.String, org.marvel.Hulk>"));
		Assert.assertEquals("Collection<org.marvel.Hulk>[]", context.importType("java.util.Collection<org.marvel.Hulk>[]"));
		Assert.assertEquals("Map<java.lang.String, org.marvel.Hulk>", context.importType("java.util.Map<java.lang.String, org.marvel.Hulk>"));		
		String string = context.generateImports();
		Assert.assertTrue(string.indexOf("import org.hibernate.Session;")<0);
		Assert.assertTrue(string.indexOf("import org.test.Entity;")>0);
		Assert.assertTrue("Entity can only be imported once", string.indexOf("import org.other.test.Entity;")<0);		
		Assert.assertFalse(string.indexOf("<")>=0);
		Assert.assertEquals("Outer.Entity", context.importType("org.test.Outer$Entity"));
		Assert.assertEquals("org.other.test.Outer.Entity", context.importType("org.other.test.Outer$Entity"));
		Assert.assertEquals("Collection<org.marvel.Outer.Hulk>", context.importType("java.util.Collection<org.marvel.Outer$Hulk>"));
		Assert.assertEquals("Map<java.lang.String, org.marvel.Outer.Hulk>", context.importType("java.util.Map<java.lang.String, org.marvel.Outer$Hulk>"));
		Assert.assertEquals("Collection<org.marvel.Outer.Hulk>[]", context.importType("java.util.Collection<org.marvel.Outer$Hulk>[]"));
		Assert.assertEquals("Map<java.lang.String, org.marvel.Outer.Hulk>", context.importType("java.util.Map<java.lang.String, org.marvel.Outer$Hulk>"));
		//assertEquals("Test.Entry", context.importType("org.hibernate.Test.Entry")); what should be the behavior for this ?
		Assert.assertEquals("Test.Entry", context.importType("org.hibernate.Test$Entry"));
		Assert.assertEquals("Map.Entry", context.importType("java.util.Map$Entry"));
		Assert.assertEquals("Entry", context.importType("java.util.Map.Entry")); // we can't detect that it is the same class here unless we try an load all strings so we fall back to default class name.
		Assert.assertEquals("List<java.util.Map.Entry>", context.importType( "java.util.List<java.util.Map$Entry>" ));
		Assert.assertEquals("List<org.hibernate.Test.Entry>", context.importType( "java.util.List<org.hibernate.Test$Entry>" ));
		string = context.generateImports();
		Assert.assertTrue(string.indexOf("import java.util.Map")>=0);
		Assert.assertTrue(string.indexOf("import java.utilMap$")<0);
		Assert.assertTrue(string.indexOf("$")<0);
	}
	
	@Test
	public void testEqualsHashCode() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		PersistentClass pc = metadata.getEntityBinding( "org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer" );
		POJOClass pjc = c2j.getPOJOClass((Component) pc.getProperty("addressComponent").getValue());
		Assert.assertTrue( pjc.needsEqualsHashCode() );
		Iterator<Property> iter = pjc.getEqualsHashCodePropertiesIterator();
		// in HelloWorld.hbm.xml there're 2 Properties for toString
		Assert.assertEquals( "streetAddress1",  iter.next().getName() );
		Assert.assertEquals( "city", iter.next().getName() );
		Assert.assertEquals( "verified", iter.next().getName() );
		Assert.assertFalse( iter.hasNext() );
	}
	
	// TODO Re-enable this test: HBX-1249
	@Ignore
	@Test
	public void testGenerics() throws Exception {
		File genericsSource = new File(temporaryFolder.getRoot(), "genericssource");
		Exporter exporter = ExporterFactory.createExporter(ExporterType.POJO);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, genericsSource);
		artifactCollector = new DefaultArtifactCollector();
		exporter.getProperties().put(ExporterConstants.ARTIFACT_COLLECTOR, artifactCollector);
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();
		File genericsTarget = new File(temporaryFolder.getRoot(), "genericstarget" );
		genericsTarget.mkdir();
		String helloWorldResourcePath = "/org/hibernate/tool/hbm2x/Hbm2JavaTest/HelloWorld.java_";
		File helloWorldOrigin = new File(getClass().getResource(helloWorldResourcePath).toURI());
		File helloWorldDestination = new File(outputDir, "HelloWorld.java");
		Files.copy(helloWorldOrigin.toPath(), helloWorldDestination.toPath());
		JavaUtil.compile(genericsSource, genericsTarget);
		Assert.assertTrue(new File(genericsTarget, "HelloWorld.class").exists());
	}
	
	@Ignore
	@Test
	public void testDynamicComponent() {
		PersistentClass classMapping = 
				metadata.getEntityBinding(
						"org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer");
		Assert.assertEquals(
				"java.util.Map", 
				new Cfg2JavaTool().getJavaTypeName(
						classMapping.getProperty("dynaMap"), false));
	}
	
	@Test
	public void testCapitializaiton() {
		Assert.assertEquals("Mail", BasicPOJOClass.beanCapitalize("Mail"));
		Assert.assertEquals("Mail", BasicPOJOClass.beanCapitalize("mail"));
		Assert.assertEquals("eMail", BasicPOJOClass.beanCapitalize("eMail"));
		Assert.assertEquals("EMail", BasicPOJOClass.beanCapitalize("EMail"));
	}
	
	@Test
	public void testUserTypes() {
		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaTest.Customer");
		Property property = classMapping.getProperty("customDate");
		Assert.assertEquals("java.sql.Date", new Cfg2JavaTool().getJavaTypeName(property, false));	
	}
	
}
