/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.test.TestHelper;

/**
 * @author max
 * 
 */
public class Hbm2JavaConstructorTest extends NonReflectiveTestCase {

	private ArtifactCollector artifactCollector;
	
	public Hbm2JavaConstructorTest(String name) {
		super( name, "hbm2javaoutput" );
	}

	protected void setUp() throws Exception {
		super.setUp();

		Exporter exporter = new POJOExporter( getCfg(), getOutputDir() );
		artifactCollector = new ArtifactCollector();
		exporter.setArtifactCollector(artifactCollector);
		exporter.start();
	}	
	
	public void testCompilable() {

		File file = new File( "compilable" );
		file.mkdir();

		ArrayList list = new ArrayList();
		list.add( new File( "src/testoutputdependent/ConstructorUsage.java" )
				.getAbsolutePath() );		
		TestHelper.compile( getOutputDir(), file, TestHelper.visitAllFiles(
				getOutputDir(), list ) );

		TestHelper.deleteDir( file );
	}

	public void testNoVelocityLeftOvers() {

		assertEquals( null, findFirstString( "$", new File( getOutputDir(),
				"Company.java" ) ) );
		assertEquals( null, findFirstString( "$", new File( getOutputDir(),
				"BigCompany.java" ) ) );
		assertEquals( null, findFirstString( "$", new File( getOutputDir(),
				"EntityAddress.java" ) ) );
		
	}

	public void testEntityConstructorLogic() {
		
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		
		POJOClass company = c2j.getPOJOClass(getCfg().getClassMapping("Company"));
		
		List all = company.getPropertyClosureForFullConstructor();
		assertNoDuplicates(all);
		assertEquals(3, all.size());
		
		List superCons = company.getPropertyClosureForSuperclassFullConstructor();
		assertEquals("company is a base class, should not have superclass cons",0, superCons.size());
		
		List subCons = company.getPropertiesForFullConstructor();
		assertNoDuplicates(subCons);
		assertEquals(3, subCons.size());
		
		assertNoOverlap(superCons, subCons);
		
		POJOClass bigCompany = c2j.getPOJOClass(getCfg().getClassMapping("BigCompany"));
		
		List bigsuperCons = bigCompany.getPropertyClosureForSuperclassFullConstructor();
		assertNoDuplicates(bigsuperCons);
		//assertEquals(3, bigsuperCons.size());
		
		List bigsubCons = bigCompany.getPropertiesForFullConstructor();
		
		assertEquals(1, bigsubCons.size());
		
		assertNoOverlap(bigsuperCons, bigsubCons);
		
		List bigall = bigCompany.getPropertyClosureForFullConstructor();
		assertNoDuplicates(bigall);
		assertEquals(4, bigall.size());
		
		PersistentClass classMapping = getCfg().getClassMapping("Person");
		POJOClass person = c2j.getPOJOClass(classMapping);
		List propertiesForMinimalConstructor = person.getPropertiesForMinimalConstructor();
		assertEquals(2,propertiesForMinimalConstructor.size());
		assertFalse(propertiesForMinimalConstructor.contains(classMapping.getIdentifierProperty()));
		List propertiesForFullConstructor = person.getPropertiesForFullConstructor();
		assertEquals(2,propertiesForFullConstructor.size());
		assertFalse(propertiesForFullConstructor.contains(classMapping.getIdentifierProperty()));
		
	}

	public void testSingleFieldLogic() {
		
		
	}
	
	
	public void testMinimal() {
		POJOClass bp = new EntityPOJOClass(getCfg().getClassMapping("BrandProduct"), new Cfg2JavaTool());
		
		List propertiesForMinimalConstructor = bp.getPropertiesForMinimalConstructor();
		
		assertEquals(1,propertiesForMinimalConstructor.size());
		
		List propertiesForFullConstructor = bp.getPropertiesForFullConstructor();
		
		assertEquals(2, propertiesForFullConstructor.size());		
	}
	
	private void assertNoDuplicates(List bigall) {
		Set set = new HashSet();
		set.addAll(bigall);
		
		assertEquals("list had duplicates!",set.size(),bigall.size());
		
	}

	private void assertNoOverlap(List first, List second) {
		Set set = new HashSet();
		set.addAll(first);
		set.addAll(second);
		
		assertEquals(set.size(),first.size()+second.size());		
	}

	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/";
	}

	protected String[] getMappings() {
		return new String[] { "Constructors.hbm.xml" };
	}


}
