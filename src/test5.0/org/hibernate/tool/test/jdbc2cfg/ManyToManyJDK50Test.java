/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.MappingException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;

/**
 * @author max
 *
 */
public class ManyToManyJDK50Test extends JDBCMetaDataBinderTestCase {
	
	public static Test suite() {
		return new TestSuite(ManyToManyJDK50Test.class);
	}

	private JDBCMetaDataConfiguration localCfg;

	protected void configure(JDBCMetaDataConfiguration configuration) {
    	super.configure( configuration );    	    
        
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		localCfg = new JDBCMetaDataConfiguration();
        
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        c.setSettings(new ReverseEngineeringSettings(c).setDetectManyToMany(false));        
        localCfg.setReverseEngineeringStrategy(c);
        localCfg.readFromJDBC();
	}
	protected void tearDown() throws Exception {
		localCfg = null;
		
		super.tearDown();
	}
	
	public void testNoManyToManyBiDirectional() {
		
		PersistentClass project = localCfg.getClassMapping("Project");
		
		assertNotNull(project.getProperty("worksOns"));
		//assertNotNull(project.getProperty("employee"));
		assertEquals(3, project.getPropertyClosureSpan());		
		assertEquals("projectId", project.getIdentifierProperty().getName());
		
		PersistentClass employee = localCfg.getClassMapping("Employee");
		
		assertNotNull(employee.getProperty("worksOns"));
		assertNotNull(employee.getProperty("employees"));
		assertNotNull(employee.getProperty("employee"));
		//assertNotNull(employee.getProperty("projects"));
		assertEquals(6, employee.getPropertyClosureSpan());
		assertEquals("id", employee.getIdentifierProperty().getName());
		
		PersistentClass worksOn = localCfg.getClassMapping("WorksOn");
		
		assertNotNull(worksOn.getProperty("project"));
		assertNotNull(worksOn.getProperty("employee"));
		assertEquals(2, worksOn.getPropertyClosureSpan());
		assertEquals("id", worksOn.getIdentifierProperty().getName());		
	}
	
	public void testAutoCreation() {
	    
        assertNull("No middle class should be generated.", cfg.getClassMapping( "WorksOn" ));
        
        assertNotNull("Should create worksontext since one of the foreign keys is not part of pk", cfg.getClassMapping( "WorksOnContext" ));
        
        PersistentClass projectClass = cfg.getClassMapping("Project");
		assertNotNull( projectClass );

		PersistentClass employeeClass = cfg.getClassMapping("Employee");
		assertNotNull( employeeClass );
				
		assertPropertyNotExist( projectClass, "worksOns" );
		assertPropertyNotExist( employeeClass, "worksOns" );
				
        Property property = employeeClass.getProperty( "projects" );
		assertNotNull( property);
		Property property2 = projectClass.getProperty( "employees" );
		assertNotNull( property2);
		
		assertTrue(((Collection)property.getValue()).isInverse());
		assertFalse(((Collection)property2.getValue()).isInverse());
		
	}
	
	public void testBuildMappings() {
		
		localCfg.buildMappings();
	}
	
	public void testGenerateAndReadable() throws Exception {

		cfg.buildMappings();

		HibernateMappingExporter hme = new HibernateMappingExporter(cfg, getOutputDir());
		hme.start();		

		getConfiguration().buildMappings();
		POJOExporter exporter = new POJOExporter(getConfiguration(), getOutputDir());
		Properties p = new Properties();
		p.setProperty("jdk5", "true");
		p.setProperty("ejb3", "true");

		exporter.setProperties(p);
		exporter.start();

		File file = new File( "ejb3compilable" );
		file.mkdir();

		ArrayList list = new ArrayList();
		List jars = new ArrayList();
		addAnnotationJars(jars);

		new ExecuteContext(getOutputDir(), file, jars) {

			protected void execute() throws Exception {
				AnnotationConfiguration configuration = new AnnotationConfiguration();
				configuration.addAnnotatedClass( getUcl().loadClass( "Project" ) );
				configuration.addAnnotatedClass( getUcl().loadClass( "Employee" ) );
			    configuration.addAnnotatedClass( getUcl().loadClass( "WorksOnContext" ) );

				SessionFactory sf = configuration.buildSessionFactory();
				Session s = sf.openSession();
				Query createQuery = s.createQuery("from java.lang.Object");
				createQuery.list();
				s.close();
				sf.close();

			}

		}.run();

	}

	private void addAnnotationJars(List jars) {
		jars.add( "ejb3-persistence.jar" );
		jars.add( "hibernate-annotations.jar" );
		jars.add( "hibernate-commons-annotations.jar" );
		jars.add( "hibernate3.jar" );
		jars.add( "dom4j-1.6.1.jar" );
		jars.add( "commons-logging-1.0.4.jar" );
		
	}

	private void assertPropertyNotExist(PersistentClass projectClass, String prop) {
		try {
			projectClass.getProperty(prop);
			fail("property " + prop + " should not exist on " + projectClass);
		} catch(MappingException e) {
			// expected
		}
	}
	
	protected String[] getCreateSQL() {
		return new String[] {
			"create table PROJECT ( project_id integer not null, name varchar(50), primary key (project_id) )",
			"create table EMPLOYEE ( id integer not null, name varchar(50), manager_id integer, primary key (id), constraint employee_manager foreign key (manager_id) references EMPLOYEE)",
			"create table WORKS_ON ( project_id integer not null, employee_id integer not null, primary key (project_id, employee_id), constraint workson_employee foreign key (employee_id) references EMPLOYEE, foreign key (project_id) references PROJECT )",
			"create table WORKS_ON_CONTEXT ( project_id integer not null, employee_id integer not null, created_by integer, primary key (project_id, employee_id), constraint workson_ctx_employee foreign key (employee_id) references EMPLOYEE, foreign key (project_id) references PROJECT, foreign key (created_by) references EMPLOYEE )",
			//"alter  table PROJECT add constraint project_manager foreign key (team_lead) references EMPLOYEE"
		};
	}

	protected String[] getDropSQL() {
		return new String[] {
				//"alter table PROJECT drop constraint project_manager",
				"drop table WORKS_ON_CONTEXT",
				"drop table WORKS_ON",
				"drop table EMPLOYEE",
				"drop table PROJECT",											
			};
	}

}
