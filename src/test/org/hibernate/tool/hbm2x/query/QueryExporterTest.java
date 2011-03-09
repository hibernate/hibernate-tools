package org.hibernate.tool.hbm2x.query;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.classic.Session;
import org.hibernate.tool.NonReflectiveTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.QueryExporter;

public class QueryExporterTest extends NonReflectiveTestCase {

	public QueryExporterTest(String name) {
		super( name );
	}

	protected String[] getMappings() {
		return new String[] { "UserGroup.hbm.xml"};
	}
	
	protected String getBaseForMappings() {
		return super.getBaseForMappings() + "hbm2x/query/";
	}
	
	String FILE = "queryresult.txt";
	
	protected void setUp() throws Exception {
		super.setUp();
		getCfg().setProperty( Environment.HBM2DDL_AUTO, "update" );
		SessionFactory factory = getCfg().buildSessionFactory();
		
		Session s = factory.openSession();
		
		User user = new User("max", "jboss");
		s.persist( user );
		
		user = new User("gavin", "jboss");
		s.persist( user );
		
		s.flush();
		
		s.close();
		
		QueryExporter exporter = new QueryExporter();
		exporter.setConfiguration( getCfg() );
		exporter.setOutputDirectory( getOutputDir() );
		exporter.setFilename( FILE );
		List queries = new ArrayList();
		queries.add("from java.lang.Object");
		exporter.setQueries( queries );
		
		exporter.start();
		
		factory.close();
	}
	
	protected void tearDown() throws Exception {
		SchemaExport export = new SchemaExport(getCfg());
		export.drop( false, true );
		
		super.tearDown();
	}
	
	
	public void testQueryExporter() {
		
		assertFileAndExists( new File(getOutputDir(), FILE ));
		
		
	}

}
