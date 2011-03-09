/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.net.MalformedURLException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.MappingException;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.SettingsFactory;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

/**
 * @author max
 *
 */
public class RevEngForeignKeyTests extends JDBCMetaDataBinderTestCase {
	
	private static final String FOREIGN_KEY_TEST_XML = "org/hibernate/tool/test/jdbc2cfg/foreignkeytest.reveng.xml";
	private static final String BAD_FOREIGNKEY_XML = "org/hibernate/tool/test/jdbc2cfg/badforeignkeytest.reveng.xml";;
	
	public static Test suite() {
		return new TestSuite(RevEngForeignKeyTests.class);
	}

	private Settings settings;

	public void testDefaultBiDirectional() {
		
		PersistentClass project = cfg.getClassMapping("Project");
		
		assertNotNull(project.getProperty("worksOns"));
		assertNotNull(project.getProperty("employee"));
		assertEquals(3, project.getPropertyClosureSpan());		
		assertEquals("projectId", project.getIdentifierProperty().getName());
		
		PersistentClass employee = cfg.getClassMapping("Employee");
		
		assertNotNull(employee.getProperty("worksOns"));
		assertNotNull(employee.getProperty("employees"));
		assertNotNull(employee.getProperty("employee"));
		assertNotNull(employee.getProperty("projects"));
		assertEquals(5, employee.getPropertyClosureSpan());
		assertEquals("id", employee.getIdentifierProperty().getName());
		
		PersistentClass worksOn = cfg.getClassMapping("WorksOn");
		
		assertNotNull(worksOn.getProperty("project"));
		assertNotNull(worksOn.getProperty("employee"));
		assertEquals(4, worksOn.getPropertyClosureSpan());
		assertEquals("id", worksOn.getIdentifierProperty().getName());
		
	}

	public void testSetAndManyToOne() {
		
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(FOREIGN_KEY_TEST_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
	
		JDBCMetaDataConfiguration localCfg = new JDBCMetaDataConfiguration();
		localCfg.setReverseEngineeringStrategy(repository);
		localCfg.readFromJDBC();			
		
		PersistentClass project = localCfg.getClassMapping("Project");
		
		assertNotNull(project.getProperty("worksOns"));
		assertPropertyNotExists(project, "employee", "should be removed by reveng.xml");
		Property property = project.getProperty("teamLead");
		assertNotNull(property);
		assertTrue(property.getValue() instanceof SimpleValue);
		assertEquals(3, project.getPropertyClosureSpan());		
		assertEquals("projectId", project.getIdentifierProperty().getName());
		
		PersistentClass employee = localCfg.getClassMapping("Employee");
		
		assertNotNull(employee.getProperty("worksOns"));
		assertNotNull("property should be renamed by reveng.xml", employee.getProperty("manager"));		
		assertPropertyNotExists( employee, "employees", "set should be excluded by reveng.xml" );
		Property setProperty = employee.getProperty("managedProjects");
		assertNotNull("should be renamed by reveng.xml", setProperty);
		
		assertEquals("delete, update", setProperty.getCascade());
		
		assertEquals(4, employee.getPropertyClosureSpan());
		assertEquals("id", employee.getIdentifierProperty().getName());
		
		PersistentClass worksOn = localCfg.getClassMapping("WorksOn");
		
		assertNotNull(worksOn.getProperty("project"));
		assertNotNull(worksOn.getProperty("employee"));
		assertEquals(4, worksOn.getPropertyClosureSpan());
		assertEquals("id", worksOn.getIdentifierProperty().getName());
	
	}

	private void addAnnotationJars(List jars) {
		jars.add( "ejb3-persistence.jar" );
		jars.add( "hibernate-annotations.jar" );
		jars.add( "hibernate-commons-annotations.jar" );
		jars.add( "hibernate3.jar" );
		jars.add( "dom4j-1.6.1.jar" );
		jars.add( "commons-logging-1.0.4.jar" );
		
	}
	
	public void testOneToOne() throws MalformedURLException, ClassNotFoundException {
		
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(FOREIGN_KEY_TEST_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());

		JDBCMetaDataConfiguration localCfg = new JDBCMetaDataConfiguration();
		localCfg.setReverseEngineeringStrategy(repository);
		localCfg.readFromJDBC();			

		PersistentClass person = localCfg.getClassMapping("Person");
		PersistentClass addressPerson = localCfg.getClassMapping("AddressPerson");
		PersistentClass addressMultiPerson = localCfg.getClassMapping("AddressMultiPerson");
		PersistentClass multiPerson = localCfg.getClassMapping("MultiPerson");
		
		assertPropertyNotExists(addressPerson, "person", "should be removed by reveng.xml");
		assertPropertyNotExists(person, "addressPerson", "should be removed by reveng.xml");
		
		Property property = addressMultiPerson.getProperty("renamedOne");
		assertNotNull(property);
		
		assertEquals("Casade should be set to delete by reveng.xml", "delete", property.getCascade());
				
		
		assertPropertyNotExists(multiPerson, "addressMultiPerson", "should not be there");
		Property o2o = multiPerson.getProperty("renamedInversedOne");
		assertNotNull(o2o);
		
		assertEquals("update", o2o.getCascade());
		assertEquals("JOIN", o2o.getValue().getFetchMode().toString());

	}
	
	public void testDuplicateForeignKeyDefinition() {
		
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(BAD_FOREIGNKEY_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());

		JDBCMetaDataConfiguration localCfg = new JDBCMetaDataConfiguration();
		localCfg.setReverseEngineeringStrategy(repository);
		
		try {
			localCfg.readFromJDBC();
			fail("Should fail because foreign key is already defined in the database"); // maybe we should ignore the definition and only listen to what is overwritten ? For now we error. 
		} catch(MappingException me) {
			assertTrue(me.getMessage().indexOf("already defined")>=0);			
		}
		
	}

	public void testManyToOneAttributeDefaults() {	

		PersistentClass classMapping = cfg.getClassMapping("Employee");
		Property property = classMapping.getProperty("employee");	

		assertEquals("none", property.getCascade());
		assertEquals(true, property.isUpdateable());
		assertEquals(true, property.isInsertable());
		assertEquals("SELECT", property.getValue().getFetchMode().toString());
		
	}
	
	public void testManyToOneAttributeOverrides() {

		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(FOREIGN_KEY_TEST_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
	
		JDBCMetaDataConfiguration localCfg = new JDBCMetaDataConfiguration();
		localCfg.setReverseEngineeringStrategy(repository);
		localCfg.readFromJDBC();			
			
		PersistentClass classMapping = localCfg.getClassMapping("Employee");
		Property property = classMapping.getProperty("manager");	

		assertEquals("all", property.getCascade());
		assertEquals(false, property.isUpdateable());
		assertEquals(false, property.isInsertable());
		assertEquals("JOIN", property.getValue().getFetchMode().toString());

		
	}	

	
	

	private void assertPropertyNotExists(PersistentClass employee, String name, String msg) {
		try {
			employee.getProperty(name);
			fail(msg);
		} catch(MappingException me) {
			// excpected
		}
	}

	private OverrideRepository buildOverrideRepository() {
		if(settings==null) {
			settings = new SettingsFactory() {
				// trick to get hibernate.properties settings for defaultschema/catalog in here
			}.buildSettings(Environment.getProperties());
		}
		//return new OverrideRepository(settings.getDefaultCatalogName(),settings.getDefaultSchemaName());
		return new OverrideRepository();
	}
	
	protected String[] getCreateSQL() {
		return new String[] {
			"create table PROJECT ( project_id integer not null, name varchar(50), team_lead integer, primary key (project_id) )",
			"create table EMPLOYEE ( id integer not null, name varchar(50), manager_id integer, primary key (id), constraint employee_manager foreign key (manager_id) references EMPLOYEE)",
			"create table WORKS_ON ( project_id integer not null, employee_id integer not null, start_date date, end_date date, primary key (project_id, employee_id), constraint workson_employee foreign key (employee_id) references EMPLOYEE, foreign key (project_id) references PROJECT )",
			"create table PERSON ( person_id integer not null, name varchar(50), primary key (person_id) )",
			"create table ADDRESS_PERSON ( address_id integer not null, name varchar(50), primary key (address_id), constraint address_person foreign key (address_id) references PERSON)",			
			"create table MULTI_PERSON ( person_id integer not null, person_compid integer not null, name varchar(50), primary key (person_id, person_compid) )",
			"create table ADDRESS_MULTI_PERSON ( address_id integer not null, address_compid integer not null, name varchar(50), primary key (address_id, address_compid), constraint address_multi_person foreign key (address_id, address_compid) references MULTI_PERSON)",
			"alter  table PROJECT add constraint project_manager foreign key (team_lead) references EMPLOYEE"
		};
	}

	protected String[] getDropSQL() {
		return new String[] {
				"alter table PROJECT drop constraint project_manager",
				"drop table WORKS_ON",
				"drop table EMPLOYEE",
				"drop table PROJECT",	
				"drop table ADDRESS_PERSON",
				"drop table PERSON",
				"drop table ADDRESS_MULTI_PERSON",
				"drop table MULTI_PERSON"
			};
	}

}
