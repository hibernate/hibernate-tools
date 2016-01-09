package org.hibernate.tool.hbm2x.query;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
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

		setCfg(createConfiguration());
		assertNoTables();		
		if(getOutputDir()!=null) {
			getOutputDir().mkdirs();
		}

		SessionFactory factory = getCfg().buildSessionFactory();		
		Session s = factory.openSession();	
		Transaction t = s.beginTransaction();
		User user = new User("max", "jboss");
		s.persist( user );		
		user = new User("gavin", "jboss");
		s.persist( user );		
		s.flush();		
		t.commit();
		s.close();
		factory.close();
		
	}
	
	public void testQueryExporter() {		

		QueryExporter exporter = new QueryExporter();
		exporter.setConfiguration(createConfiguration());
		exporter.setOutputDirectory( getOutputDir() );
		exporter.setFilename( FILE );
		List<String> queries = new ArrayList<String>();
		queries.add("from java.lang.Object");
		exporter.setQueries( queries );		
		exporter.start();

		assertFileAndExists( new File(getOutputDir(), FILE ));		
	}
	
	protected void tearDown() throws Exception {

		SchemaExport export = new SchemaExport(createMetadata());
		export.drop( false, true );
		
		if (export.getExceptions() != null && export.getExceptions().size() > 0){
			fail("Schema export failed");
		}		
		super.tearDown();
	}
	
	
	private Configuration createConfiguration() {
		Configuration result = new Configuration();
		addMappings(getMappings(), result);
		result.setProperty(Environment.HBM2DDL_AUTO, "update");
		return result;
	}

	private MetadataImplementor createMetadata() {
		MetadataSources mds = new MetadataSources();
		addMappings(getMappings(), mds);
		return (MetadataImplementor)mds.buildMetadata();
	}

}
