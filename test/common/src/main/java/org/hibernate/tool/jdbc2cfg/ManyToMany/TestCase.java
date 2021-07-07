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
package org.hibernate.tool.jdbc2cfg.ManyToMany;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.hibernate.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	@TempDir
	public File temporaryFolder = new File("temp");

	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testNoManyToManyBiDirectional() {
        
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        c.setSettings(new ReverseEngineeringSettings(c).setDetectManyToMany(false)); 
        Metadata metadata =  MetadataDescriptorFactory
        		.createJdbcDescriptor(c, null, true)
        		.createMetadata();

        PersistentClass project = metadata.getEntityBinding("Project");
		
		assertNotNull(project.getProperty("worksOns"));
		//assertNotNull(project.getProperty("employee"));
		assertEquals(3, project.getPropertyClosureSpan());		
		assertEquals("projectId", project.getIdentifierProperty().getName());
		
		PersistentClass employee = metadata.getEntityBinding("Employee");
		
		assertNotNull(employee.getProperty("worksOns"));
		assertNotNull(employee.getProperty("employees"));
		assertNotNull(employee.getProperty("employee"));
		//assertNotNull(employee.getProperty("projects"));
		assertEquals(6, employee.getPropertyClosureSpan());
		assertEquals("id", employee.getIdentifierProperty().getName());
		
		PersistentClass worksOn = metadata.getEntityBinding("WorksOn");
		
		assertNotNull(worksOn.getProperty("project"));
		assertNotNull(worksOn.getProperty("employee"));
		assertEquals(2, worksOn.getPropertyClosureSpan());
		assertEquals("id", worksOn.getIdentifierProperty().getName());		
	}
	
	@Test
	public void testAutoCreation() {
		Metadata metadata = MetadataDescriptorFactory
				.createJdbcDescriptor(null, null, true)
				.createMetadata();
		
        assertNull(metadata.getEntityBinding( "WorksOn" ), "No middle class should be generated.");
        
        assertNotNull(metadata.getEntityBinding( "WorksOnContext" ), "Should create worksontext since one of the foreign keys is not part of pk");
        
        PersistentClass projectClass = metadata.getEntityBinding("Project");
		assertNotNull( projectClass );

		PersistentClass employeeClass = metadata.getEntityBinding("Employee");
		assertNotNull( employeeClass );
				
		assertPropertyNotExist( projectClass, "worksOns" );
		assertPropertyNotExist( employeeClass, "worksOns" );
		
        Property property = employeeClass.getProperty( "projects" );
		assertNotNull( property);
		assertNotNull( projectClass.getProperty( "employees" ));				
		
	}

	@Test
	public void testFalsePositive() {
		Metadata metadata = MetadataDescriptorFactory
				.createJdbcDescriptor(null, null, true)
				.createMetadata();	    
        assertNotNull(metadata.getEntityBinding( "NonMiddle" ), "Middle class should be generated.");	
	}

	@Test
	public void testBuildMappings() {		
		Metadata metadata = MetadataDescriptorFactory
				.createJdbcDescriptor(null, null, true)
				.createMetadata();
		assertNotNull(metadata);	
	}
	
	@Test
	public void testGenerateAndReadable() {
		
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createJdbcDescriptor(null, null, true);
		assertNotNull(metadataDescriptor.createMetadata());
		
		HibernateMappingExporter hme = new HibernateMappingExporter();
		hme.setMetadataDescriptor(metadataDescriptor);
		hme.setOutputDirectory(temporaryFolder);
		hme.start();		
		
		assertFileAndExists( new File(temporaryFolder, "Employee.hbm.xml") );
		assertFileAndExists( new File(temporaryFolder, "Project.hbm.xml") );
		assertFileAndExists( new File(temporaryFolder, "WorksOnContext.hbm.xml") );
		
		assertFileAndExists( new File(temporaryFolder, "RightTable.hbm.xml") );
		assertFileAndExists( new File(temporaryFolder, "LeftTable.hbm.xml") );
		assertFileAndExists( new File(temporaryFolder, "NonMiddle.hbm.xml") ); //Must be there since it has a fkey that is not part of the pk
		
		assertFalse(new File(temporaryFolder, "WorksOn.hbm.xml").exists() );
		
		assertEquals(6, temporaryFolder.listFiles().length);
		
		File[] files = new File[3];
		files[0] = new File(temporaryFolder, "Employee.hbm.xml");
		files[1] = new File(temporaryFolder, "Project.hbm.xml");
		files[2] = new File(temporaryFolder, "WorksOnContext.hbm.xml");
		
		assertNotNull(MetadataDescriptorFactory
				.createNativeDescriptor(null, files, null)
				.createMetadata());
		
	}
	

	private void assertPropertyNotExist(PersistentClass projectClass, String prop) {
		try {
			projectClass.getProperty(prop);
			fail("property " + prop + " should not exist on " + projectClass);
		} catch(MappingException e) {
			// expected
		}
	}
	
	private void assertFileAndExists(File file) {
		assertTrue(file.exists(), file + " does not exist" );
		assertTrue(file.isFile(), file + " not a file" );		
		assertTrue(file.length()>0, file + " does not have any contents");
	}

}
