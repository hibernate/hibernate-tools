package org.hibernate.tool.hbm2x.query.QueryExporterTest;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.hibernate.tool.schema.TargetType;
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
	
	@Before
	public void setUp() throws Exception {
		JdbcUtil.createDatabase(this);
		outputDir = temporaryFolder.getRoot();
		SessionFactory factory = buildMetadata().buildSessionFactory();		
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
		MetadataSources metadataSources = new MetadataSources();
		metadataSources.addResource("/org/hibernate/tool/hbm2x/query/QueryExporterTest/UserGroup.hbm.xml");
		exporter.getProperties().put(AvailableSettings.HBM2DDL_AUTO, "update");
		exporter.setMetadataSources(metadataSources);
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
		export.drop(targetTypes, buildMetadata());		
		if (export.getExceptions() != null && export.getExceptions().size() > 0){
			Assert.fail("Schema export failed");
		}		
		JdbcUtil.dropDatabase(this);
	}
	
	private BootstrapServiceRegistry buildBootstrapServiceRegistry() {
		return new BootstrapServiceRegistryBuilder()
			.enableAutoClose()
			.build();
	}
	
	private MetadataSources buildMetadataSources(ServiceRegistry sr) {
		MetadataSources metadataSources = new MetadataSources(sr);
		metadataSources.addResource("/org/hibernate/tool/hbm2x/query/QueryExporterTest/UserGroup.hbm.xml");
		return metadataSources;
	}
	
	private StandardServiceRegistry buildStandardServiceRegistry(BootstrapServiceRegistry sr) {
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder(sr);
		ssrb.applySetting(AvailableSettings.HBM2DDL_AUTO, "update");
		return ssrb.build();
	}
	
	private Metadata buildMetadata() {
		BootstrapServiceRegistry bsr = buildBootstrapServiceRegistry();
		StandardServiceRegistry sr = buildStandardServiceRegistry(bsr);
		MetadataSources mds = buildMetadataSources(bsr);
		return mds.buildMetadata(sr);
	}
	
}
