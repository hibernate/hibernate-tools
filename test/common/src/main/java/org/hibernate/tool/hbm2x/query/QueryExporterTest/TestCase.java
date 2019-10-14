package org.hibernate.tool.hbm2x.query.QueryExporterTest;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.hibernate.tools.test.util.ResourceUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCase {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File destinationDir = null;
	private File resourcesDir = null;
	private File userGroupHbmXmlFile = null;
	
	@Before
	public void setUp() throws Exception {
		JdbcUtil.createDatabase(this);
		destinationDir = new File(temporaryFolder.getRoot(), "destination");
		destinationDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		String[] resources = { "UserGroup.hbm.xml" };		
		ResourceUtil.createResources(this, resources, resourcesDir);
		userGroupHbmXmlFile = new File(resourcesDir, "UserGroup.hbm.xml");
		SessionFactory factory = createMetadata().buildSessionFactory();		
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
	public void testQueryExporter() throws Exception {		
		Exporter exporter = ExporterFactory.createExporter(ExporterType.QUERY);
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(
						null, 
						new File[] { userGroupHbmXmlFile }, 
						null);
		exporter.getProperties().put(AvailableSettings.HBM2DDL_AUTO, "update");
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, destinationDir);
		exporter.getProperties().put(ExporterConstants.OUTPUT_FILE_NAME, "queryresult.txt");
		List<String> queries = new ArrayList<String>();
		queries.add("from java.lang.Object");
		exporter.getProperties().put(ExporterConstants.QUERY_LIST, queries);
		exporter.start();
		JUnitUtil.assertIsNonEmptyFile(new File(destinationDir, "queryresult.txt"));		
	}
	
	@After
	public void tearDown() throws Exception {
		SchemaExport export = new SchemaExport();
		final EnumSet<TargetType> targetTypes = EnumSet.noneOf( TargetType.class );
		targetTypes.add( TargetType.DATABASE );
		export.drop(targetTypes, createMetadata());		
		if (export.getExceptions() != null && export.getExceptions().size() > 0){
			Assert.fail("Schema export failed");
		}		
		JdbcUtil.dropDatabase(this);
	}
	
	private Metadata createMetadata() {
		Properties properties = new Properties();
		properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(
						null, 
						new File[] { userGroupHbmXmlFile }, 
						properties);
		return metadataDescriptor.createMetadata();
	}
	
}
