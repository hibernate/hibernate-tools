/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.jdbc2cfg.OneToOne;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Persistence;

import org.hibernate.MappingException;
import org.hibernate.Version;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.metadata.NativeMetadataDescriptor;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JavaUtil;
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
 */
public class TestCase {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private MetadataDescriptor metadataDescriptor = null;
	private Metadata metadata = null;

	@Before
	public void setUp() throws Exception {
		JdbcUtil.createDatabase(this);
		metadataDescriptor = MetadataDescriptorFactory.createReverseEngineeringDescriptor(null, null);
		metadata = metadataDescriptor.createMetadata();
	}
	
	@After
	public void tearDown() throws Exception {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testOneToOneSingleColumnBiDirectional() {	
		PersistentClass person = metadata.getEntityBinding("Person");		
		Property addressProperty = person.getProperty("addressPerson");
		Assert.assertNotNull(addressProperty);			
		Assert.assertTrue(addressProperty.getValue() instanceof OneToOne);	
		OneToOne oto = (OneToOne) addressProperty.getValue();	
		Assert.assertEquals(oto.getColumnSpan(),1);
		Assert.assertEquals("Person", oto.getEntityName());
		Assert.assertEquals("AddressPerson", oto.getReferencedEntityName());
		Assert.assertEquals(2, person.getPropertyClosureSpan());		
		Assert.assertEquals("personId", person.getIdentifierProperty().getName());
		Assert.assertFalse(oto.isConstrained());		
		PersistentClass addressPerson = metadata.getEntityBinding("AddressPerson");
		Property personProperty = addressPerson.getProperty("person");
		Assert.assertNotNull(personProperty);
		Assert.assertTrue(personProperty.getValue() instanceof OneToOne);	
		oto = (OneToOne) personProperty.getValue();	
		Assert.assertTrue(oto.isConstrained());		
		Assert.assertEquals(oto.getColumnSpan(),1);
		Assert.assertEquals("AddressPerson", oto.getEntityName());
		Assert.assertEquals("Person", oto.getReferencedEntityName());
		Assert.assertEquals(2, addressPerson.getPropertyClosureSpan());
		Assert.assertEquals("addressId", addressPerson.getIdentifierProperty().getName());			
	}
	
	@Test
	public void testAddressWithForeignKeyGeneration() {
		PersistentClass address = metadata.getEntityBinding("AddressPerson");	
		Assert.assertEquals("foreign", ((SimpleValue)address.getIdentifier()).getIdentifierGeneratorStrategy());
	}

	@Test
	public void testOneToOneMultiColumnBiDirectional() {
		PersistentClass person = metadata.getEntityBinding("MultiPerson");	
		Property addressProperty = person.getProperty("addressMultiPerson");
		Assert.assertNotNull(addressProperty);		
		Assert.assertTrue(addressProperty.getValue() instanceof OneToOne);
		OneToOne oto = (OneToOne) addressProperty.getValue();
		Assert.assertEquals(oto.getColumnSpan(),2);
		Assert.assertEquals("MultiPerson", oto.getEntityName());
		Assert.assertEquals("AddressMultiPerson", oto.getReferencedEntityName());
		Assert.assertFalse(oto.isConstrained());
		Assert.assertEquals(2, person.getPropertyClosureSpan());		
		Assert.assertEquals("compositeid gives generic id name", "id", person.getIdentifierProperty().getName());
		PersistentClass addressPerson = metadata.getEntityBinding("AddressMultiPerson");
		Property personProperty = addressPerson.getProperty("multiPerson");
		Assert.assertNotNull(personProperty);
		Assert.assertTrue(personProperty.getValue() instanceof OneToOne);
		oto = (OneToOne) personProperty.getValue();
		Assert.assertEquals(oto.getColumnSpan(),2);
		Assert.assertEquals("AddressMultiPerson", oto.getEntityName());
		Assert.assertEquals("MultiPerson", oto.getReferencedEntityName());
		Assert.assertEquals(2, addressPerson.getPropertyClosureSpan());
		Assert.assertEquals("compositeid gives generic id name","id", addressPerson.getIdentifierProperty().getName());
		Assert.assertTrue(oto.isConstrained());
	}

	@Test
	public void testBuildMappings() {	
		Assert.assertNotNull(metadata);		
	}
	
	@Test
	public void testGenerateMappingAndReadable() throws MalformedURLException {
		File outputDir = temporaryFolder.getRoot();
		HbmExporter hme = new HbmExporter();
		hme.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		hme.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		hme.start();		
		assertFileAndExists( new File(outputDir, "Person.hbm.xml") );
		assertFileAndExists( new File(outputDir, "AddressPerson.hbm.xml") );
		assertFileAndExists( new File(outputDir, "AddressMultiPerson.hbm.xml") );
		assertFileAndExists( new File(outputDir, "MultiPerson.hbm.xml") );
		assertFileAndExists( new File(outputDir, "MiddleTable.hbm.xml") );
		assertFileAndExists( new File(outputDir, "LeftTable.hbm.xml") );
		assertFileAndExists( new File(outputDir, "RightTable.hbm.xml") );		
		Assert.assertEquals(7, outputDir.listFiles().length);	
		Exporter exporter = ExporterFactory.createExporter(ExporterType.JAVA);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.getProperties().put(ExporterConstants.TEMPLATE_PATH, new String[0]);
		exporter.getProperties().setProperty("ejb3", "false");
		exporter.getProperties().setProperty("jdk5", "false");
		exporter.start();			
		JavaUtil.compile(outputDir);
		URL[] urls = new URL[] { outputDir.toURI().toURL() };
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		URLClassLoader ucl = new URLClassLoader(urls, oldLoader );
		try {
	        Thread.currentThread().setContextClassLoader(ucl);
	        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
	        ServiceRegistry serviceRegistry = builder.build();
	        File[] files = new File[7];
	        files[0] = new File(outputDir, "Person.hbm.xml");
	        files[1] = new File(outputDir, "AddressPerson.hbm.xml");
	        files[2] = new File(outputDir, "AddressMultiPerson.hbm.xml");
	        files[3] = new File(outputDir, "MultiPerson.hbm.xml");
	        files[4] = new File(outputDir, "MiddleTable.hbm.xml");
	        files[5] = new File(outputDir, "LeftTable.hbm.xml");
	        files[6] = new File(outputDir, "RightTable.hbm.xml");
	        new SchemaValidator().validate(
	        		MetadataDescriptorFactory
	        			.createNativeDescriptor(null, files, null)
	        			.createMetadata(), 
	        		serviceRegistry);
		} finally {
			Thread.currentThread().setContextClassLoader(oldLoader);			
		}
	}
	
	@Test
	public void testGenerateAnnotatedClassesAndReadable() throws MappingException, ClassNotFoundException, MalformedURLException {
		File outputDir = temporaryFolder.getRoot();
		Exporter exporter = ExporterFactory.createExporter(ExporterType.JAVA);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.getProperties().put(ExporterConstants.TEMPLATE_PATH, new String[0]);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.getProperties().setProperty("jdk5", "true");
		exporter.start();		
		assertFileAndExists( new File(outputDir, "Person.java") );
		assertFileAndExists( new File(outputDir, "AddressPerson.java") );
		assertFileAndExists( new File(outputDir, "MultiPersonId.java") );
		assertFileAndExists( new File(outputDir, "AddressMultiPerson.java") );
		assertFileAndExists( new File(outputDir, "AddressMultiPersonId.java") );
		assertFileAndExists( new File(outputDir, "MultiPerson.java") );
		Assert.assertEquals(9, outputDir.listFiles().length);
		List<String> paths = new ArrayList<String>();
		paths.add(JavaUtil.resolvePathToJarFileFor(Persistence.class)); // for jpa api
		paths.add(JavaUtil.resolvePathToJarFileFor(Version.class)); // for hibernate core
		JavaUtil.compile(outputDir, paths);
        URL[] urls = new URL[] { outputDir.toURI().toURL() };
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		URLClassLoader ucl = new URLClassLoader(urls, oldLoader );
        Class<?> personClass = ucl.loadClass("Person");
        Class<?> multiPersonClass = ucl.loadClass("MultiPerson");
        Class<?> addressMultiPerson = ucl.loadClass("AddressMultiPerson");
        Class<?> addressMultiPersonId = ucl.loadClass("AddressMultiPersonId");
        Class<?> addressPerson = ucl.loadClass("AddressPerson");
        Class<?> multiPersonIdClass = ucl.loadClass("MultiPersonId");
        Class<?> middleClass = ucl.loadClass("MiddleTable");
        Class<?> rightClass = ucl.loadClass("LeftTable");
        Class<?> leftClass = ucl.loadClass("RightTable");
        try {
	        Thread.currentThread().setContextClassLoader(ucl);			
			StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
			ServiceRegistry serviceRegistry = builder.build();			
			NativeMetadataDescriptor mds = new NativeMetadataDescriptor(null, null, null);
			HibernateUtil.addAnnotatedClass(mds, personClass);
			HibernateUtil.addAnnotatedClass(mds, multiPersonClass);
			HibernateUtil.addAnnotatedClass(mds, addressMultiPerson);
			HibernateUtil.addAnnotatedClass(mds, addressMultiPersonId);
			HibernateUtil.addAnnotatedClass(mds, addressPerson);
			HibernateUtil.addAnnotatedClass(mds, multiPersonIdClass);
			HibernateUtil.addAnnotatedClass(mds, middleClass);
			HibernateUtil.addAnnotatedClass(mds, rightClass);
			HibernateUtil.addAnnotatedClass(mds, leftClass);
			Metadata metadata = mds.createMetadata();			
			new SchemaValidator().validate(metadata, serviceRegistry);
        } finally {
        	Thread.currentThread().setContextClassLoader(oldLoader);
        }		
	}

	private void assertFileAndExists(File file) {
		Assert.assertTrue(file + " does not exist", file.exists() );
		Assert.assertTrue(file + " not a file", file.isFile() );		
		Assert.assertTrue(file + " does not have any contents", file.length()>0);
	}

}
