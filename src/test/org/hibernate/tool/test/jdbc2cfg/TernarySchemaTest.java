/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.visitor.DefaultValueVisitor;

/**
 * 
 * Tests multi schema used in collections (set)'s. See JBIDE-5628.
 * 
 * Excluded from default tests for now since it currently requires HSQLDB 2.0 since previous versions of hsqldb does not support cross-schema foreign key checks.
 * 
 * @author max
 * 
 */
public class TernarySchemaTest extends JDBCMetaDataBinderTestCase {

	/**
	 * @return
	 */
	protected String[] getDropSQL() {
		return new String[] { 
				"drop table plainuserroles",
				"drop table plainrole",
				"drop table thirdschema.userroles",
				"drop table user",
				"drop table otherschema.role",				
				"drop schema otherschema",
				"drop schema thirdschema"};
	}

	/**
	 * @return
	 */
	protected String[] getCreateSQL() {

		return new String[] {
				"create schema otherschema authorization dba",
				"create schema thirdschema authorization dba",
				
				"create table user ( id int not null, name varchar(20), primary key(id))",
				"create table otherschema.role ( id int not null, name varchar(20), primary key(id))",				
				"create table thirdschema.userroles ( userid int not null, roleid int not null, primary key(userid, roleid))",
				"alter table thirdschema.userroles add constraint toroles foreign key (roleid) references otherschema.role(id)",
				"alter table thirdschema.userroles add constraint tousers foreign key (userid) references public.user(id)",				
				
				"create table plainrole ( id int not null, name varchar(20), primary key(id))",				
				"create table plainuserroles ( userid int not null, roleid int not null, primary key(userid, roleid))",
				"alter table plainuserroles add constraint plaintoroles foreign key (roleid) references plainrole(id)",
				"alter table plainuserroles add constraint plaintousers foreign key (userid) references user(id)",				
				
		};
	}

	protected void setUp() throws Exception {
		try {
			super.setUp();
		} catch (SQLException e) {
			//since it currently requires HSQLDB 2.0
			executeDDL(getDropSQL(), true);
			throw e;
		}
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTernaryModel() throws SQLException {

		assertMultiSchema(getConfiguration());
		
	}

	private void assertMultiSchema(Configuration cfg) {
		assertHasNext("There should be three tables!", 5, cfg
				.getTableMappings());

		final PersistentClass role = cfg.getClassMapping("Role");
		PersistentClass userroles = cfg.getClassMapping("Userroles");
		PersistentClass user = cfg.getClassMapping("User");
		PersistentClass plainRole = cfg.getClassMapping("Plainrole");
		

		Property property = role.getProperty("users");
		assertEquals(role.getTable().getSchema(), "OTHERSCHEMA");
		assertNotNull(property);
		property.getValue().accept(new DefaultValueVisitor(true) {
			public Object accept(Set o) {
				assertEquals(o.getCollectionTable().getSchema(), "THIRDSCHEMA");
				return null;
			}
		});
		
		

		property = plainRole.getProperty("users");
		assertEquals(role.getTable().getSchema(), "OTHERSCHEMA");
		assertNotNull(property);
		property.getValue().accept(new DefaultValueVisitor(true) {
			public Object accept(Set o) {
				assertEquals(o.getCollectionTable().getSchema(), null);
				return null;
			}
		});

	}
	
	public void testGeneration() {
		
	
		cfg.buildMappings();
		
		HibernateMappingExporter hme = new HibernateMappingExporter(cfg, getOutputDir());
		hme.start();		
		
		assertFileAndExists( new File(getOutputDir(), "Role.hbm.xml") );
		assertFileAndExists( new File(getOutputDir(), "User.hbm.xml") );
		assertFileAndExists( new File(getOutputDir(), "Plainrole.hbm.xml") );
		
		assertEquals(3, getOutputDir().listFiles().length);
		
		Configuration configuration = new Configuration()
		    .addFile( new File(getOutputDir(), "Role.hbm.xml") )
		    .addFile( new File(getOutputDir(), "User.hbm.xml") )
		    .addFile( new File(getOutputDir(), "Plainrole.hbm.xml"));
		
		configuration.buildMappings();
		
		assertMultiSchema(configuration);
	}
	
	protected void configure(JDBCMetaDataConfiguration configuration) {
		
		super.configure(configuration);
		 DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy() {
			 public List getSchemaSelections() {
				 List selections = new ArrayList();
				 selections.add(new SchemaSelection(null, "PUBLIC"));
				 selections.add(new SchemaSelection(null, "otherschema"));
				 selections.add(new SchemaSelection(null, "thirdschema"));
				return selections;
			}
		 };
	             
	     configuration.setReverseEngineeringStrategy(c);
	}

	
	public static Test suite() {
		return new TestSuite( TernarySchemaTest.class );
	}

}
