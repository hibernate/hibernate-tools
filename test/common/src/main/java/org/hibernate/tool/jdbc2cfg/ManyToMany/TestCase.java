/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.jdbc2cfg.ManyToMany;

import java.io.File;

import org.hibernate.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.reveng.strategy.AbstractStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
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
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testNoManyToManyBiDirectional() {
        
        AbstractStrategy c = new DefaultStrategy();
        c.setSettings(new RevengSettings(c).setDetectManyToMany(false)); 
        Metadata metadata =  MetadataDescriptorFactory
        		.createReverseEngineeringDescriptor(c, null)
        		.createMetadata();

        PersistentClass project = metadata.getEntityBinding("Project");
		
		Assert.assertNotNull(project.getProperty("worksOns"));
		//assertNotNull(project.getProperty("employee"));
		Assert.assertEquals(3, project.getPropertyClosureSpan());		
		Assert.assertEquals("projectId", project.getIdentifierProperty().getName());
		
		PersistentClass employee = metadata.getEntityBinding("Employee");
		
		Assert.assertNotNull(employee.getProperty("worksOns"));
		Assert.assertNotNull(employee.getProperty("employees"));
		Assert.assertNotNull(employee.getProperty("employee"));
		//assertNotNull(employee.getProperty("projects"));
		Assert.assertEquals(6, employee.getPropertyClosureSpan());
		Assert.assertEquals("id", employee.getIdentifierProperty().getName());
		
		PersistentClass worksOn = metadata.getEntityBinding("WorksOn");
		
		Assert.assertNotNull(worksOn.getProperty("project"));
		Assert.assertNotNull(worksOn.getProperty("employee"));
		Assert.assertEquals(2, worksOn.getPropertyClosureSpan());
		Assert.assertEquals("id", worksOn.getIdentifierProperty().getName());		
	}
	
	@Test
	public void testAutoCreation() {
		Metadata metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata();
		
        Assert.assertNull("No middle class should be generated.", metadata.getEntityBinding( "WorksOn" ));
        
        Assert.assertNotNull("Should create worksontext since one of the foreign keys is not part of pk", metadata.getEntityBinding( "WorksOnContext" ));
        
        PersistentClass projectClass = metadata.getEntityBinding("Project");
		Assert.assertNotNull( projectClass );

		PersistentClass employeeClass = metadata.getEntityBinding("Employee");
		Assert.assertNotNull( employeeClass );
				
		assertPropertyNotExist( projectClass, "worksOns" );
		assertPropertyNotExist( employeeClass, "worksOns" );
		
        Property property = employeeClass.getProperty( "projects" );
		Assert.assertNotNull( property);
		Assert.assertNotNull( projectClass.getProperty( "employees" ));				
		
	}

	@Test
	public void testFalsePositive() {
		Metadata metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata();	    
        Assert.assertNotNull("Middle class should be generated.", metadata.getEntityBinding( "NonMiddle" ));	
	}

	@Test
	public void testBuildMappings() {		
		Metadata metadata = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null)
				.createMetadata();
		Assert.assertNotNull(metadata);	
	}
	
	@Test
	public void testGenerateAndReadable() {
		
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null);
		File outputDir = temporaryFolder.getRoot();
		
		Assert.assertNotNull(metadataDescriptor.createMetadata());
		
		HbmExporter hme = new HbmExporter();
		hme.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hme.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		hme.start();		
		
		assertFileAndExists( new File(outputDir, "Employee.hbm.xml") );
		assertFileAndExists( new File(outputDir, "Project.hbm.xml") );
		assertFileAndExists( new File(outputDir, "WorksOnContext.hbm.xml") );
		
		assertFileAndExists( new File(outputDir, "RightTable.hbm.xml") );
		assertFileAndExists( new File(outputDir, "LeftTable.hbm.xml") );
		assertFileAndExists( new File(outputDir, "NonMiddle.hbm.xml") ); //Must be there since it has a fkey that is not part of the pk
		
		Assert.assertFalse(new File(outputDir, "WorksOn.hbm.xml").exists() );
		
		Assert.assertEquals(6, outputDir.listFiles().length);
		
		File[] files = new File[3];
		files[0] = new File(outputDir, "Employee.hbm.xml");
		files[1] = new File(outputDir, "Project.hbm.xml");
		files[2] = new File(outputDir, "WorksOnContext.hbm.xml");
		
		Assert.assertNotNull(MetadataDescriptorFactory
				.createNativeDescriptor(null, files, null)
				.createMetadata());
		
	}
	

	private void assertPropertyNotExist(PersistentClass projectClass, String prop) {
		try {
			projectClass.getProperty(prop);
			Assert.fail("property " + prop + " should not exist on " + projectClass);
		} catch(MappingException e) {
			// expected
		}
	}
	
	private void assertFileAndExists(File file) {
		Assert.assertTrue(file + " does not exist", file.exists() );
		Assert.assertTrue(file + " not a file", file.isFile() );		
		Assert.assertTrue(file + " does not have any contents", file.length()>0);
	}

}
