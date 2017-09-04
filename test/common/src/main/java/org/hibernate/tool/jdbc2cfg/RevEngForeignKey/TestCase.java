/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.jdbc2cfg.RevEngForeignKey;

import java.net.MalformedURLException;

import org.hibernate.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.metadata.MetadataSourcesFactory;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	private static final String FOREIGN_KEY_TEST_XML = "org/hibernate/tool/jdbc2cfg/RevEngForeignKey/foreignkeytest.reveng.xml";
	private static final String BAD_FOREIGNKEY_XML = "org/hibernate/tool/jdbc2cfg/RevEngForeignKey/badforeignkeytest.reveng.xml";
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testDefaultBiDirectional() {
		Metadata metadata = MetadataSourcesFactory
				.createJdbcSources(null, null)
				.buildMetadata();
		PersistentClass project = metadata.getEntityBinding("Project");
		Assert.assertNotNull(project.getProperty("worksOns"));
		Assert.assertNotNull(project.getProperty("employee"));
		Assert.assertEquals(3, project.getPropertyClosureSpan());		
		Assert.assertEquals("projectId", project.getIdentifierProperty().getName());
		PersistentClass employee = metadata.getEntityBinding("Employee");
		Assert.assertNotNull(employee.getProperty("worksOns"));
		Assert.assertNotNull(employee.getProperty("employees"));
		Assert.assertNotNull(employee.getProperty("employee"));
		Assert.assertNotNull(employee.getProperty("projects"));
		Assert.assertEquals(5, employee.getPropertyClosureSpan());
		Assert.assertEquals("id", employee.getIdentifierProperty().getName());
		PersistentClass worksOn = metadata.getEntityBinding("WorksOn");
		Assert.assertNotNull(worksOn.getProperty("project"));
		Assert.assertNotNull(worksOn.getProperty("employee"));
		Assert.assertEquals(4, worksOn.getPropertyClosureSpan());
		Assert.assertEquals("id", worksOn.getIdentifierProperty().getName());	
	}

	@Test
	public void testSetAndManyToOne() {
		OverrideRepository or = new OverrideRepository();
		or.addResource(FOREIGN_KEY_TEST_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		Metadata metadata = MetadataSourcesFactory.createJdbcSources(repository, null).buildMetadata();			
		PersistentClass project = metadata.getEntityBinding("Project");		
		Assert.assertNotNull(project.getProperty("worksOns"));
		assertPropertyNotExists(project, "employee", "should be removed by reveng.xml");
		Property property = project.getProperty("teamLead");
		Assert.assertNotNull(property);
		Assert.assertTrue(property.getValue() instanceof SimpleValue);
		Assert.assertEquals(3, project.getPropertyClosureSpan());		
		Assert.assertEquals("projectId", project.getIdentifierProperty().getName());
		PersistentClass employee = metadata.getEntityBinding("Employee");	
		Assert.assertNotNull(employee.getProperty("worksOns"));
		Assert.assertNotNull("property should be renamed by reveng.xml", employee.getProperty("manager"));		
		assertPropertyNotExists( employee, "employees", "set should be excluded by reveng.xml" );
		Property setProperty = employee.getProperty("managedProjects");
		Assert.assertNotNull("should be renamed by reveng.xml", setProperty);
		Assert.assertEquals("delete, update", setProperty.getCascade());
		Assert.assertEquals(4, employee.getPropertyClosureSpan());
		Assert.assertEquals("id", employee.getIdentifierProperty().getName());
		PersistentClass worksOn = metadata.getEntityBinding("WorksOn");
		Assert.assertNotNull(worksOn.getProperty("project"));
		Assert.assertNotNull(worksOn.getProperty("employee"));
		Assert.assertEquals(4, worksOn.getPropertyClosureSpan());
		Assert.assertEquals("id", worksOn.getIdentifierProperty().getName());
	}

	@Test
	public void testOneToOne() throws MalformedURLException, ClassNotFoundException {
		OverrideRepository or = new OverrideRepository();
		or.addResource(FOREIGN_KEY_TEST_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		Metadata metadata = MetadataSourcesFactory.createJdbcSources(repository, null).buildMetadata();
		PersistentClass person = metadata.getEntityBinding("Person");
		PersistentClass addressPerson = metadata.getEntityBinding("AddressPerson");
		PersistentClass addressMultiPerson = metadata.getEntityBinding("AddressMultiPerson");
		PersistentClass multiPerson = metadata.getEntityBinding("MultiPerson");	
		assertPropertyNotExists(addressPerson, "person", "should be removed by reveng.xml");
		assertPropertyNotExists(person, "addressPerson", "should be removed by reveng.xml");	
		Property property = addressMultiPerson.getProperty("renamedOne");
		Assert.assertNotNull(property);	
		Assert.assertEquals("Casade should be set to delete by reveng.xml", "delete", property.getCascade());
		assertPropertyNotExists(multiPerson, "addressMultiPerson", "should not be there");
		Property o2o = multiPerson.getProperty("renamedInversedOne");
		Assert.assertNotNull(o2o);
		Assert.assertEquals("update", o2o.getCascade());
		Assert.assertEquals("JOIN", o2o.getValue().getFetchMode().toString());
	}
	
	@Test
	public void testDuplicateForeignKeyDefinition() {
		try {
			OverrideRepository or = new OverrideRepository();
			or.addResource(BAD_FOREIGNKEY_XML);
			ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
			MetadataSourcesFactory
					.createJdbcSources(repository, null)
					.buildMetadata();
			Assert.fail("Should fail because foreign key is already defined in the database"); // maybe we should ignore the definition and only listen to what is overwritten ? For now we error. 
		} catch(MappingException me) {
			Assert.assertTrue(me.getMessage().indexOf("already defined")>=0);			
		}		
	}

	@Test
	public void testManyToOneAttributeDefaults() {	
		Metadata metadata = MetadataSourcesFactory
				.createJdbcSources(null, null)
				.buildMetadata();
		PersistentClass classMapping = metadata.getEntityBinding("Employee");
		Property property = classMapping.getProperty("employee");	
		Assert.assertEquals("none", property.getCascade());
		Assert.assertEquals(true, property.isUpdateable());
		Assert.assertEquals(true, property.isInsertable());
		Assert.assertEquals("SELECT", property.getValue().getFetchMode().toString());
	}
	
	@Test
	public void testManyToOneAttributeOverrides() {
		OverrideRepository or = new OverrideRepository();	
		or.addResource(FOREIGN_KEY_TEST_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
		Metadata metadata = MetadataSourcesFactory
				.createJdbcSources(repository, null)
				.buildMetadata();
		PersistentClass classMapping = metadata.getEntityBinding("Employee");
		Property property = classMapping.getProperty("manager");	
		Assert.assertEquals("all", property.getCascade());
		Assert.assertEquals(false, property.isUpdateable());
		Assert.assertEquals(false, property.isInsertable());
		Assert.assertEquals("JOIN", property.getValue().getFetchMode().toString());
	}	

	private void assertPropertyNotExists(PersistentClass employee, String name, String msg) {
		try {
			employee.getProperty(name);
			Assert.fail(msg);
		} catch(MappingException me) {
			// excpected
		}
	}

}
