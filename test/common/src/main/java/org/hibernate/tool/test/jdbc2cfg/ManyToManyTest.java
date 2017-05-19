/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;

import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.util.MetadataHelper;
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
 *
 */
public class ManyToManyTest {
	
	static final String[] CREATE_SQL = new String[] {
			"CREATE TABLE PROJECT (PROJECT_ID INTEGER NOT NULL, NAME VARCHAR(50), PRIMARY KEY (PROJECT_ID) )",
			"CREATE TABLE EMPLOYEE (ID INTEGER NOT NULL, NAME VARCHAR(50), MANAGER_ID INTEGER, PRIMARY KEY (ID), CONSTRAINT EMPLOYEE_MANAGER FOREIGN KEY (MANAGER_ID) REFERENCES EMPLOYEE(ID))",
			"CREATE TABLE WORKS_ON (PROJECT_ID INTEGER NOT NULL, EMPLOYEE_ID INTEGER NOT NULL, PRIMARY KEY (PROJECT_ID, EMPLOYEE_ID), CONSTRAINT WORKSON_EMPLOYEE FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE(ID), FOREIGN KEY (PROJECT_ID) REFERENCES PROJECT(PROJECT_ID) )",
			"CREATE TABLE WORKS_ON_CONTEXT (PROJECT_ID INTEGER NOT NULL, EMPLOYEE_ID INTEGER NOT NULL, CREATED_BY INTEGER, PRIMARY KEY (PROJECT_ID, EMPLOYEE_ID), CONSTRAINT WORKSON_CTX_EMPLOYEE FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE, FOREIGN KEY (PROJECT_ID) REFERENCES PROJECT(PROJECT_ID), FOREIGN KEY (CREATED_BY) REFERENCES EMPLOYEE(ID) )",
			//"alter  table PROJECT add constraint project_manager foreign key (team_lead) references EMPLOYEE"
			// nonmiddle left and right are used to test a false association table isn't detected.
			"CREATE TABLE LEFT_TABLE ( id INTEGER NOT NULL, PRIMARY KEY (ID) )",
			"CREATE TABLE RIGHT_TABLE ( id INTEGER NOT NULL, PRIMARY KEY (ID) )",
			"CREATE TABLE NON_MIDDLE ( left_id INTEGER NOT NULL, RIGHT_ID INTEGER NOT NULL, PRIMARY KEY (LEFT_ID), CONSTRAINT FK_MIDDLE_LEFT FOREIGN KEY (LEFT_ID) REFERENCES LEFT_TABLE(ID), CONSTRAINT FK_MIDDLE_RIGHT FOREIGN KEY (RIGHT_ID) REFERENCES RIGHT_TABLE(ID))",
		};

	static final String[] DROP_SQL = new String[] {
				//"alter table PROJECT drop constraint project_manager",
				"DROP TABLE WORKS_ON_CONTEXT",
				"DROP TABLE WORKS_ON",
				"DROP TABLE EMPLOYEE",
				"DROP TABLE PROJECT",
				"DROP TABLE NON_MIDDLE",
				"DROP TABLE LEFT_TABLE",
				"DROP TABLE RIGHT_TABLE",
			};

	private JDBCMetaDataConfiguration jmdcfg = null;
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testNoManyToManyBiDirectional() {
		
		JDBCMetaDataConfiguration localCfg = new JDBCMetaDataConfiguration();
        
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy();
        c.setSettings(new ReverseEngineeringSettings(c).setDetectManyToMany(false));        
        localCfg.setReverseEngineeringStrategy(c);
        localCfg.readFromJDBC();

        PersistentClass project = localCfg.getMetadata().getEntityBinding("Project");
		
		Assert.assertNotNull(project.getProperty("worksOns"));
		//assertNotNull(project.getProperty("employee"));
		Assert.assertEquals(3, project.getPropertyClosureSpan());		
		Assert.assertEquals("projectId", project.getIdentifierProperty().getName());
		
		PersistentClass employee = localCfg.getMetadata().getEntityBinding("Employee");
		
		Assert.assertNotNull(employee.getProperty("worksOns"));
		Assert.assertNotNull(employee.getProperty("employees"));
		Assert.assertNotNull(employee.getProperty("employee"));
		//assertNotNull(employee.getProperty("projects"));
		Assert.assertEquals(6, employee.getPropertyClosureSpan());
		Assert.assertEquals("id", employee.getIdentifierProperty().getName());
		
		PersistentClass worksOn = localCfg.getMetadata().getEntityBinding("WorksOn");
		
		Assert.assertNotNull(worksOn.getProperty("project"));
		Assert.assertNotNull(worksOn.getProperty("employee"));
		Assert.assertEquals(2, worksOn.getPropertyClosureSpan());
		Assert.assertEquals("id", worksOn.getIdentifierProperty().getName());		
	}
	
	@Test
	public void testAutoCreation() {
	    
        Assert.assertNull("No middle class should be generated.", jmdcfg.getMetadata().getEntityBinding( "WorksOn" ));
        
        Assert.assertNotNull("Should create worksontext since one of the foreign keys is not part of pk", jmdcfg.getMetadata().getEntityBinding( "WorksOnContext" ));
        
        PersistentClass projectClass = jmdcfg.getMetadata().getEntityBinding("Project");
		Assert.assertNotNull( projectClass );

		PersistentClass employeeClass = jmdcfg.getMetadata().getEntityBinding("Employee");
		Assert.assertNotNull( employeeClass );
				
		assertPropertyNotExist( projectClass, "worksOns" );
		assertPropertyNotExist( employeeClass, "worksOns" );
		
        Property property = employeeClass.getProperty( "projects" );
		Assert.assertNotNull( property);
		Assert.assertNotNull( projectClass.getProperty( "employees" ));				
		
	}

	@Test
	public void testFalsePositive() {
	    
        Assert.assertNotNull("Middle class should be generated.", jmdcfg.getMetadata().getEntityBinding( "NonMiddle" ));
                				
		
	}

	@Test
	public void testBuildMappings() {		
		Assert.assertNotNull(MetadataHelper.getMetadata(jmdcfg));
		
	}
	
	@Test
	public void testGenerateAndReadable() {
		
		File outputDir = temporaryFolder.getRoot();
		
		Assert.assertNotNull(MetadataHelper.getMetadata(jmdcfg));
		
		HibernateMappingExporter hme = new HibernateMappingExporter(jmdcfg, outputDir);
		hme.start();		
		
		assertFileAndExists( new File(outputDir, "Employee.hbm.xml") );
		assertFileAndExists( new File(outputDir, "Project.hbm.xml") );
		assertFileAndExists( new File(outputDir, "WorksOnContext.hbm.xml") );
		
		assertFileAndExists( new File(outputDir, "RightTable.hbm.xml") );
		assertFileAndExists( new File(outputDir, "LeftTable.hbm.xml") );
		assertFileAndExists( new File(outputDir, "NonMiddle.hbm.xml") ); //Must be there since it has a fkey that is not part of the pk
		
		Assert.assertFalse(new File(outputDir, "WorksOn.hbm.xml").exists() );
		
		Assert.assertEquals(6, outputDir.listFiles().length);
		
		Configuration configuration = new Configuration()
		    .addFile( new File(outputDir, "Employee.hbm.xml") )
		    .addFile( new File(outputDir, "Project.hbm.xml") )
			.addFile( new File(outputDir, "WorksOnContext.hbm.xml") );
		
		Assert.assertNotNull(MetadataHelper.getMetadata(configuration));
		
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

	protected String[] getCreateSQL() {
		return new String[] {
			"create table PROJECT ( project_id integer not null, name varchar(50), primary key (project_id) )",
			"create table EMPLOYEE ( id integer not null, name varchar(50), manager_id integer, primary key (id), constraint employee_manager foreign key (manager_id) references EMPLOYEE(id))",
			"create table WORKS_ON ( project_id integer not null, employee_id integer not null, primary key (project_id, employee_id), constraint workson_employee foreign key (employee_id) references EMPLOYEE(id), foreign key (project_id) references PROJECT(project_id) )",
			"create table WORKS_ON_CONTEXT ( project_id integer not null, employee_id integer not null, created_by integer, primary key (project_id, employee_id), constraint workson_ctx_employee foreign key (employee_id) references EMPLOYEE, foreign key (project_id) references PROJECT(project_id), foreign key (created_by) references EMPLOYEE(id) )",
			//"alter  table PROJECT add constraint project_manager foreign key (team_lead) references EMPLOYEE"
			// nonmiddle left and right are used to test a false association table isn't detected.
			"create table LEFT_TABLE ( id integer not null, primary key (id) )",
			"create table RIGHT_TABLE ( id integer not null, primary key (id) )",
			"create table NON_MIDDLE ( left_id integer not null, right_id integer not null, primary key (left_id), constraint FK_MIDDLE_LEFT foreign key (left_id) references LEFT_TABLE(id), constraint FK_MIDDLE_RIGHT foreign key (right_id) references RIGHT_TABLE(id))",
		};
	}

	protected String[] getDropSQL() {
		return new String[] {
				//"alter table PROJECT drop constraint project_manager",
				"drop table WORKS_ON_CONTEXT",
				"drop table WORKS_ON",
				"drop table EMPLOYEE",
				"drop table PROJECT",
				"drop table NON_MIDDLE",
				"drop table LEFT_TABLE",
				"drop table RIGHT_TABLE",
			};
	}

}
