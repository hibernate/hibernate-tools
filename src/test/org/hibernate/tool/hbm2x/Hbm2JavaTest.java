/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.mapping.Component;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.pojo.BasicPOJOClass;
import org.hibernate.tool.hbm2x.pojo.ImportContext;
import org.hibernate.tool.hbm2x.pojo.ImportContextImpl;
import org.hibernate.tool.hbm2x.pojo.NoopImportContext;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.test.TestHelper;

/**
 * @author max
 * 
 */
public class Hbm2JavaTest extends NonReflectiveTestCase {

	private ArtifactCollector artifactCollector;
	
	public Hbm2JavaTest(String name) {
		super( name, "hbm2javaoutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();

		Exporter exporter = new POJOExporter( getCfg(), getOutputDir() );
		artifactCollector = new ArtifactCollector();
		exporter.setArtifactCollector(artifactCollector);
		exporter.start();
	}

	public void testFileExistence() {

		assertFileAndExists( new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/Customer.java" ) );
		assertFileAndExists( new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/LineItem.java" ) );
		assertFileAndExists( new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/Order.java" ) );
		assertFileAndExists( new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/Train.java" ) );
		assertFileAndExists( new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/Passenger.java" ) );
		assertFileAndExists( new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/Product.java" ) );
		assertFileAndExists( new File( getOutputDir(),
				"generated/BaseHelloWorld.java" ) );
		assertFileAndExists( new File( getOutputDir(), "HelloUniverse.java" ) );
		
		assertFileAndExists( new File( getOutputDir(), "org/hibernate/tool/hbm2x/FatherComponent.java" ) );
		assertFileAndExists( new File( getOutputDir(), "org/hibernate/tool/hbm2x/ChildComponent.java" ) );

		assertEquals(15, artifactCollector.getFileCount("java"));
	}
	
	public void testCompilable() {

		File file = new File( "compilable" );
		file.mkdir();

		ArrayList list = new ArrayList();
		list.add( new File( "src/testoutputdependent/HelloWorld.java" )
				.getAbsolutePath() );
		TestHelper.compile( getOutputDir(), file, TestHelper.visitAllFiles(
				getOutputDir(), list ) );

		TestHelper.deleteDir( file );
	}

	/** HBX-606 */
//  TODO Implement HBX-606 so that the following test succeeds
//	public void testParentComponentFailureExpected() {
//		
//		File file = new File( getOutputDir(), "org/hibernate/tool/hbm2x/FatherComponent.java" );
//		
//		assertEquals("test", findFirstString("testParent", file));
//	}
	
	public void testNoFreeMarkerLeftOvers() {

		assertEquals( null, findFirstString( "$", new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/Customer.java" ) ) );
		assertEquals( null, findFirstString( "$", new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/LineItem.java" ) ) );
		assertEquals( null, findFirstString( "$", new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/Order.java" ) ) );
		assertEquals( null, findFirstString( "$", new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/Product.java" ) ) );
		assertEquals( null, findFirstString( "$", new File( getOutputDir(),
				"org/hibernate/tool/hbm2x/Address.java" ) ) );

	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/";
	}

	protected String[] getMappings() {
		return new String[] { "Customer.hbm.xml", "Order.hbm.xml",
				"LineItem.hbm.xml", "Product.hbm.xml", "HelloWorld.hbm.xml", "Train.hbm.xml", "Passenger.hbm.xml" };
	}

	public void testPackageName() {

		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass classMapping = getCfg()
				.getClassMapping( "org.hibernate.tool.hbm2x.Order" );
		POJOClass pc = c2j.getPOJOClass(classMapping);
		
		assertEquals( "org.hibernate.tool.hbm2x", pc.getPackageName() );
		assertEquals( "package org.hibernate.tool.hbm2x;", pc.getPackageDeclaration() );
		assertEquals( "did not honor generated-class", "package generated;", c2j.getPOJOClass( getCfg().getClassMapping(
		"HelloWorld" )).getPackageDeclaration());
	}
	
	public void testFieldNotThere() {
		assertEquals(null,findFirstString("notgenerated", new File( getOutputDir(),
		"HelloUniverse.java" )));
	}

	public void testJavaDoc() {

		Cfg2JavaTool c2j = new Cfg2JavaTool();

		assertEquals( " * test", c2j.toJavaDoc( "test", 0 ) );
		assertEquals( "   * test", c2j.toJavaDoc( "test", 2 ) );
		assertEquals( "   * test\n   * me", c2j.toJavaDoc( "test\nme", 2 ) );

		PersistentClass local = getCfg()
				.getClassMapping( "HelloWorld" );
		POJOClass pc = c2j.getPOJOClass(local);
		
		assertEquals( " * Hey there", pc.getClassJavaDoc( "fallback", 0 ) );

		assertEquals( " * Test Field Description", pc.getFieldJavaDoc(
				local.getIdentifierProperty(), 0 ) );
	}

	public void testExtraCode() {

		Cfg2JavaTool c2j = new Cfg2JavaTool();

		assertFalse( c2j.hasMetaAttribute( getCfg().getClassMapping(
				"HelloWorld" ), "class-code" ) );

		PersistentClass classMapping = getCfg().getClassMapping(
				"org.hibernate.tool.hbm2x.Order" );

		assertEquals(
				"// extra code line 1\n// extra code line 2\n{ Collator.getInstance(); }",
				c2j.getPOJOClass(classMapping).getExtraClassCode() );
	}

	public void testScope() {

		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass pc = getCfg().getClassMapping(
				"org.hibernate.tool.hbm2x.Order" );
		assertEquals( "public strictfp", c2j.getClassModifiers( pc ) );

		assertEquals( "public", c2j.getClassModifiers( getCfg()
				.getClassMapping( "HelloWorld" ) ) );
	}

	public void testDeclarationType() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass pc = getCfg().getClassMapping(
				"org.hibernate.tool.hbm2x.Order" );
		assertEquals( "class", c2j.getPOJOClass(pc).getDeclarationType() );

		assertEquals( "interface", c2j.getPOJOClass(getCfg()
				.getClassMapping( "HelloWorld" ) ).getDeclarationType() );

	}

	public void testTypeName() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass pc = getCfg().getClassMapping(
				"org.hibernate.tool.hbm2x.Order" );
		Property property = pc.getProperty( "lineItems" );
		assertEquals( "java.util.Collection", c2j.getJavaTypeName( property, false ) );
		

	}

	public void testUseRawTypeNullability() {
		Cfg2JavaTool c2j = new Cfg2JavaTool( /*true*/ );

		PersistentClass pc = getCfg().getClassMapping(
				"org.hibernate.tool.hbm2x.Product" );
		Property property = pc.getProperty( "numberAvailable" );
		assertFalse( property.getValue().isNullable() );
		assertEquals( "typename should be used when rawtypemode", "int", c2j
				.getJavaTypeName( property, false ) );

		property = pc.getProperty( "minStock" );
		assertTrue( property.getValue().isNullable() );
		assertEquals( "typename should be used when rawtypemode", "long", c2j
				.getJavaTypeName( property, false ) );

		property = pc.getProperty( "otherStock" );
		assertFalse( property.getValue().isNullable() );
		assertEquals( "type should still be overriden by meta attribute",
				"java.lang.Integer", c2j.getJavaTypeName( property, false ) );

		property = pc.getIdentifierProperty();
		assertFalse( property.getValue().isNullable() );
		assertEquals( "wrappers should be used by default", "long", c2j
				.getJavaTypeName( property, false ) );

		pc = getCfg().getClassMapping( "org.hibernate.tool.hbm2x.Customer" );
		Component identifier = (Component) pc.getIdentifier();

		assertFalse( ((Property) identifier.getPropertyIterator().next() )
				.getValue().isNullable() );
		assertEquals( "long", c2j.getJavaTypeName( property, false ) );

	}

	public void testExtendsImplements() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass pc = getCfg().getClassMapping(
				"org.hibernate.tool.hbm2x.Order" );
		assertEquals( null, c2j.getPOJOClass(pc).getExtends() );

		POJOClass entityPOJOClass = c2j.getPOJOClass(getCfg().getClassMapping(
		"HelloWorld" ));
		assertEquals( "Comparable", entityPOJOClass.getExtends() );
		assertEquals( "should be interface which cannot have implements", null,
				entityPOJOClass.getImplements() );
		assertEquals( "should be interface which cannot have implements", "",
				entityPOJOClass.getImplementsDeclaration() );

		PersistentClass base = new RootClass();
		base.setClassName( "Base" );

		PersistentClass sub = new SingleTableSubclass( base );
		sub.setClassName( "Sub" );

		assertEquals( null, c2j.getPOJOClass(base).getExtends() );
		assertEquals( "Base", c2j.getPOJOClass(sub).getExtends() );

		Map m = new HashMap();
		MetaAttribute attribute = new MetaAttribute( "extends" );
		attribute.addValue( "x" );
		attribute.addValue( "y" );
		m.put( attribute.getName(), attribute );
		attribute = new MetaAttribute( "interface" );
		attribute.addValue( "true" );
		m.put( attribute.getName(), attribute );

		sub.setMetaAttributes( m );
		assertEquals( "Base,x,y", c2j.getPOJOClass(sub).getExtends() );

		m = new HashMap();
		attribute = new MetaAttribute( "implements" );
		attribute.addValue( "intf" );
		m.put( attribute.getName(), attribute );
		base.setMetaAttributes( m );
		assertEquals( "intf,java.io.Serializable", c2j.getPOJOClass(base).getImplements() );
	}

	public void testDeclarationName() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass pc = getCfg().getClassMapping(
				"org.hibernate.tool.hbm2x.Order" );
		PersistentClass hw = getCfg().getClassMapping( "HelloWorld" );

		POJOClass epc = c2j.getPOJOClass(pc);
		
		assertEquals( "Order", epc.getDeclarationName() );
		
		epc = c2j.getPOJOClass(hw);
		assertEquals( "BaseHelloWorld", epc.getDeclarationName() );

	}

	public void testAsArguments() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass pc = getCfg().getClassMapping(
				"org.hibernate.tool.hbm2x.Order" );

		assertEquals(
				"java.util.Calendar orderDate, java.math.BigDecimal total, org.hibernate.tool.hbm2x.Customer customer, java.util.Collection lineItems",
				c2j.asParameterList( pc.getPropertyIterator(), false, new NoopImportContext() ));
		assertEquals( "orderDate, total, customer, lineItems", c2j
				.asArgumentList( pc.getPropertyIterator() ) );
	}

	public void testPropertiesForFullConstructor() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass pc = getCfg().getClassMapping( "HelloWorld" );
		POJOClass pjc = c2j.getPOJOClass(pc);
		
		List wl = pjc.getPropertiesForFullConstructor();
		assertEquals( 3, wl.size() );

		PersistentClass uni = getCfg().getClassMapping( "HelloUniverse" );
		pjc = c2j.getPOJOClass(uni);
		List local = pjc.getPropertyClosureForFullConstructor();
		assertEquals( 6, local.size() );

		for(int i=0;i<wl.size();i++) {
			assertEquals( i + " position should be the same", local.get( i ), wl.get( i ) );
		}

	}

	public void testToString() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass pc = getCfg().getClassMapping( "HelloWorld" );
		POJOClass pjc = c2j.getPOJOClass(pc);
		assertTrue( pjc.needsToString() );
		Iterator iter = pjc.getToStringPropertiesIterator();

		// in HelloWorld.hbm.xml there're 2 Properties for toString
		assertEquals( "id", ((Property) iter.next() ).getName() );
		assertEquals( "hello", ((Property) iter.next() ).getName() );
		assertFalse( iter.hasNext() );

		pc = getCfg().getClassMapping( "org.hibernate.tool.hbm2x.Order" );
		pjc = c2j.getPOJOClass(pc);
		assertFalse( pjc.needsToString() );

		pc = getCfg().getClassMapping( "org.hibernate.tool.hbm2x.Customer" );
		Component c = (Component) pc.getProperty( "addressComponent" )
				.getValue();		
		POJOClass cc = c2j.getPOJOClass(c);
		assertTrue( cc.needsToString() );
		iter = cc.getToStringPropertiesIterator();
		
		// in Customer.hbm.xml there's 1 Property for toString
		assertEquals( "city", ((Property) iter.next() ).getName() );
		assertFalse( iter.hasNext() );
	}
	
	

	public void testImportOfSameName() {
		ImportContext ic = new ImportContextImpl("foobar");
		
		assertEquals("CascadeType", ic.importType("javax.persistence.CascadeType"));
		assertEquals("org.hibernate.annotations.CascadeType", ic.importType("org.hibernate.annotations.CascadeType"));
		
		assertTrue("The hibernate annotation should not be imported to avoid name clashes", ic.generateImports().indexOf("hibernate")<0);
		
	}
	
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
		assertTrue("Entity can only be imported once", string.indexOf("import org.other.test.Entity;")<0);		
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
	
	public void testEqualsHashCode() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();

		PersistentClass pc = getCfg().getClassMapping( "org.hibernate.tool.hbm2x.Customer" );
		POJOClass pjc = c2j.getPOJOClass((Component) pc.getProperty("addressComponent").getValue());
		 
		assertTrue( pjc.needsEqualsHashCode() );
		Iterator iter = pjc.getEqualsHashCodePropertiesIterator();

		// in HelloWorld.hbm.xml there're 2 Properties for toString
		assertEquals( "streetAddress1", ((Property) iter.next() ).getName() );
		assertEquals( "city", ((Property) iter.next() ).getName() );
		assertEquals( "verified", ((Property) iter.next() ).getName() );
		assertFalse( iter.hasNext() );

	}
	
	public void testGenerics() {
		File file = new File(getOutputDir(), "genericsoutput");
		POJOExporter exporter = new POJOExporter( getCfg(), file );
		artifactCollector = new ArtifactCollector();
		exporter.setArtifactCollector(artifactCollector);
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();

		File cfile = new File( getOutputDir(), "genericscompilable" );
		cfile.mkdir();

		ArrayList list = new ArrayList();
		list.add( new File( "src/testoutputdependent/HelloWorld.java" )
				.getAbsolutePath() );
		TestHelper.compile( file, cfile, TestHelper.visitAllFiles(
				file, list ), "1.5", "" );

		TestHelper.deleteDir( cfile );

		
		TestHelper.deleteDir(file);			
	}
	
	/*public void testDynamicComponent() {
		
		PersistentClass classMapping = getCfg().getClassMapping("org.hibernate.tool.hbm2x.Customer");
		
		assertEquals("java.util.Map", new Cfg2JavaTool().getJavaTypeName(classMapping.getProperty("dynaMap")));
	}
	*/
	
	public void testCapitializaiton() {
		assertEquals("Mail", BasicPOJOClass.beanCapitalize("Mail"));
		assertEquals("Mail", BasicPOJOClass.beanCapitalize("mail"));
		assertEquals("eMail", BasicPOJOClass.beanCapitalize("eMail"));
		assertEquals("EMail", BasicPOJOClass.beanCapitalize("EMail"));
	}
	
	public void testUserTypes() {
		PersistentClass classMapping = getCfg().getClassMapping("org.hibernate.tool.hbm2x.Customer");
		
		Property property = classMapping.getProperty("customDate");
		assertEquals("java.sql.Date", new Cfg2JavaTool().getJavaTypeName(property, false));
		
	}
	
	
}
