/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.Hbm2JavaConstructorTest;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.pojo.Cfg2JavaTool;
import org.hibernate.tool.internal.export.pojo.EntityPOJOClass;
import org.hibernate.tool.internal.export.pojo.POJOClass;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
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
			"Constructors.hbm.xml"
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
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		metadata = metadataDescriptor.createMetadata();
		Exporter exporter = ExporterFactory.createExporter(ExporterType.POJO);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		exporter.start();
	}	
	
	@Test
	public void testCompilable() throws Exception {
		String constructorUsageResourcePath = "/org/hibernate/tool/hbm2x/Hbm2JavaConstructorTest/ConstructorUsage.java_";
		File constructorUsageOrigin = new File(getClass().getResource(constructorUsageResourcePath).toURI());
		File constructorUsageDestination = new File(outputDir, "ConstructorUsage.java");
		File targetDir = new File(temporaryFolder.getRoot(), "compilerOutput" );
		targetDir.mkdir();	
		Files.copy(constructorUsageOrigin.toPath(), constructorUsageDestination.toPath());
		JavaUtil.compile(outputDir, targetDir);
		Assert.assertTrue(new File(targetDir, "ConstructorUsage.class").exists());
		Assert.assertTrue(new File(targetDir, "Company.class").exists());
		Assert.assertTrue(new File(targetDir, "BigCompany.class").exists());
		Assert.assertTrue(new File(targetDir, "EntityAddress.class").exists());
	}

	@Test
	public void testNoVelocityLeftOvers() {
		Assert.assertNull(FileUtil.findFirstString( 
				"$", 
				new File(outputDir, "Company.java" ) ) );
		Assert.assertNull(FileUtil.findFirstString(
				"$", 
				new File(outputDir,"BigCompany.java" ) ) );
		Assert.assertNull(FileUtil.findFirstString(
				"$", 
				new File(outputDir,"EntityAddress.java" ) ) );
	}

	@Test
	public void testEntityConstructorLogic() {
		Cfg2JavaTool c2j = new Cfg2JavaTool();
		POJOClass company = c2j.getPOJOClass(metadata.getEntityBinding("Company"));	
		List<Property> all = company.getPropertyClosureForFullConstructor();
		assertNoDuplicates(all);
		Assert.assertEquals(3, all.size());
		List<Property> superCons = company.getPropertyClosureForSuperclassFullConstructor();
		Assert.assertEquals("company is a base class, should not have superclass cons",0, superCons.size());
		List<Property> subCons = company.getPropertiesForFullConstructor();
		assertNoDuplicates(subCons);
		Assert.assertEquals(3, subCons.size());
		assertNoOverlap(superCons, subCons);
		POJOClass bigCompany = c2j.getPOJOClass(metadata.getEntityBinding("BigCompany"));
		List<Property> bigsuperCons = bigCompany.getPropertyClosureForSuperclassFullConstructor();
		assertNoDuplicates(bigsuperCons);
		//assertEquals(3, bigsuperCons.size());
		List<Property> bigsubCons = bigCompany.getPropertiesForFullConstructor();
		Assert.assertEquals(1, bigsubCons.size());
		assertNoOverlap(bigsuperCons, bigsubCons);
		List<?> bigall = bigCompany.getPropertyClosureForFullConstructor();
		assertNoDuplicates(bigall);
		Assert.assertEquals(4, bigall.size());
		PersistentClass classMapping = metadata.getEntityBinding("Person");
		POJOClass person = c2j.getPOJOClass(classMapping);
		List<Property> propertiesForMinimalConstructor = person.getPropertiesForMinimalConstructor();
		Assert.assertEquals(2,propertiesForMinimalConstructor.size());
		Assert.assertFalse(propertiesForMinimalConstructor.contains(classMapping.getIdentifierProperty()));
		List<Property> propertiesForFullConstructor = person.getPropertiesForFullConstructor();
		Assert.assertEquals(2,propertiesForFullConstructor.size());
		Assert.assertFalse(propertiesForFullConstructor.contains(classMapping.getIdentifierProperty()));	
	}

	@Test
	public void testMinimal() {
		POJOClass bp = new EntityPOJOClass(
				metadata.getEntityBinding("BrandProduct"), 
				new Cfg2JavaTool());
		List<Property> propertiesForMinimalConstructor = bp.getPropertiesForMinimalConstructor();
		Assert.assertEquals(1,propertiesForMinimalConstructor.size());
		List<Property> propertiesForFullConstructor = bp.getPropertiesForFullConstructor();
		Assert.assertEquals(2, propertiesForFullConstructor.size());		
	}
	
	private void assertNoDuplicates(List<?> bigall) {
		Set<Object> set = new HashSet<Object>();
		set.addAll(bigall);
		Assert.assertEquals("list had duplicates!",set.size(),bigall.size());	
	}

	private void assertNoOverlap(List<?> first, List<?> second) {
		Set<Object> set = new HashSet<Object>();
		set.addAll(first);
		set.addAll(second);	
		Assert.assertEquals(set.size(),first.size()+second.size());		
	}

}
