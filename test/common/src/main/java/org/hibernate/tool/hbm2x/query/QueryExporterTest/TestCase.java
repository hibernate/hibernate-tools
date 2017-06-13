package org.hibernate.tool.hbm2x.query.QueryExporterTest;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.util.MetadataHelper;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCase {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File outputDir = null;
	private Configuration configuration = null;
	private Metadata metadata = null;
	
	@Before
	public void setUp() throws Exception {
		JdbcUtil.createDatabase(this);
		configuration = new Configuration();
		configuration.addResource("/org/hibernate/tool/hbm2x/query/QueryExporterTest/UserGroup.hbm.xml");
		configuration.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");
		metadata = MetadataHelper.getMetadata(configuration);
		outputDir = temporaryFolder.getRoot();
		SessionFactory factory = configuration.buildSessionFactory();		
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
	
	@Test
	public void testQueryExporter() {		
		QueryExporter exporter = new QueryExporter();
		exporter.setConfiguration(configuration);
		exporter.setOutputDirectory(outputDir);
		exporter.setFilename("queryresult.txt");
		List<String> queries = new ArrayList<String>();
		queries.add("from java.lang.Object");
		exporter.setQueries( queries );		
		exporter.start();
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "queryresult.txt"));		
	}
	
	@After
	public void tearDown() throws Exception {
		SchemaExport export = new SchemaExport();
		final EnumSet<TargetType> targetTypes = EnumSet.noneOf( TargetType.class );
		targetTypes.add( TargetType.DATABASE );
		export.drop(targetTypes, metadata);		
		if (export.getExceptions() != null && export.getExceptions().size() > 0){
			Assert.fail("Schema export failed");
		}		
		JdbcUtil.dropDatabase(this);
	}
	
}
