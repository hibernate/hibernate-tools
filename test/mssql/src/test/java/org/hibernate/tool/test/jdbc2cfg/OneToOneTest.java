/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

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
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.util.MetadataHelper;
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
 *
 */
public class OneToOneTest {
	
	static final String[] CREATE_SQL = new String[] {
			"CREATE TABLE LEFT_TABLE ( ID INT NOT NULL, PRIMARY KEY (ID) )",
			"CREATE TABLE RIGHT_TABLE ( ID INT NOT NULL, PRIMARY KEY (ID) )",
			"CREATE TABLE MIDDLE_TABLE ( LEFT_ID INT NOT NULL, RIGHT_ID INT NOT NULL, PRIMARY KEY (LEFT_ID), CONSTRAINT FK_MIDDLE_LEFT FOREIGN KEY (LEFT_ID) REFERENCES LEFT_TABLE, CONSTRAINT FK_MIDDLE_RIGHT FOREIGN KEY (RIGHT_ID) REFERENCES RIGHT_TABLE)",
			"CREATE TABLE PERSON ( PERSON_ID INT NOT NULL, NAME VARCHAR(50), PRIMARY KEY (PERSON_ID) )",
			"CREATE TABLE ADDRESS_PERSON ( ADDRESS_ID INT NOT NULL, NAME VARCHAR(50), PRIMARY KEY (ADDRESS_ID), CONSTRAINT ADDRESS_PERSON FOREIGN KEY (ADDRESS_ID) REFERENCES PERSON)",			
			"CREATE TABLE MULTI_PERSON ( PERSON_ID INT NOT NULL, PERSON_COMPID INT NOT NULL, NAME VARCHAR(50), PRIMARY KEY (PERSON_ID, PERSON_COMPID) )",
			"CREATE TABLE ADDRESS_MULTI_PERSON ( ADDRESS_ID INT NOT NULL, ADDRESS_COMPID INT NOT NULL, NAME VARCHAR(50), PRIMARY KEY (ADDRESS_ID, ADDRESS_COMPID), CONSTRAINT ADDRESS_MULTI_PERSON FOREIGN KEY (ADDRESS_ID, ADDRESS_COMPID) REFERENCES MULTI_PERSON)",		
		};

	static final String[] DROP_SQL = new String[] {
				"DROP TABLE MIDDLE_TABLE",
				"DROP TABLE LEFT_TABLE",
				"DROP TABLE RIGHT_TABLE",
				"DROP TABLE ADDRESS_PERSON",
				"DROP TABLE PERSON",
				"DROP TABLE ADDRESS_MULTI_PERSON",
				"DROP TABLE MULTI_PERSON",
			};

	private JDBCMetaDataConfiguration localCfg;

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		JdbcUtil.createDatabase(this);
		localCfg = new JDBCMetaDataConfiguration();       
        localCfg.setReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());
        localCfg.readFromJDBC();
	}
	
	@After
	public void tearDown() throws Exception {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testOneToOneSingleColumnBiDirectional() {	
		PersistentClass person = localCfg.getMetadata().getEntityBinding("Person");		
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
		PersistentClass addressPerson = localCfg.getMetadata().getEntityBinding("AddressPerson");
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
		PersistentClass address = localCfg.getMetadata().getEntityBinding("AddressPerson");	
		Assert.assertEquals("foreign", ((SimpleValue)address.getIdentifier()).getIdentifierGeneratorStrategy());
	}

	@Test
	public void testOneToOneMultiColumnBiDirectional() {
		PersistentClass person = localCfg.getMetadata().getEntityBinding("MultiPerson");	
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
		PersistentClass addressPerson = localCfg.getMetadata().getEntityBinding("AddressMultiPerson");
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
		Assert.assertNotNull(MetadataHelper.getMetadata(localCfg));		
	}
	
	@Test
	public void testGenerateMappingAndReadable() throws MalformedURLException {
		File outputDir = temporaryFolder.getRoot();
		MetadataHelper.getMetadata(localCfg);
		HibernateMappingExporter hme = new HibernateMappingExporter(localCfg, outputDir);
		hme.start();		
		assertFileAndExists( new File(outputDir, "Person.hbm.xml") );
		assertFileAndExists( new File(outputDir, "AddressPerson.hbm.xml") );
		assertFileAndExists( new File(outputDir, "AddressMultiPerson.hbm.xml") );
		assertFileAndExists( new File(outputDir, "MultiPerson.hbm.xml") );
		assertFileAndExists( new File(outputDir, "MiddleTable.hbm.xml") );
		assertFileAndExists( new File(outputDir, "LeftTable.hbm.xml") );
		assertFileAndExists( new File(outputDir, "RightTable.hbm.xml") );		
		Assert.assertEquals(7, outputDir.listFiles().length);	
		POJOExporter exporter = new POJOExporter(localCfg, outputDir);
		exporter.setTemplatePath(new String[0]);
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
	        MetadataSources mds = new MetadataSources(serviceRegistry)
			    .addFile( new File(outputDir, "Person.hbm.xml") )
			    .addFile( new File(outputDir, "AddressPerson.hbm.xml") )
				.addFile( new File(outputDir, "AddressMultiPerson.hbm.xml"))
				.addFile( new File(outputDir, "MultiPerson.hbm.xml"))
				.addFile( new File(outputDir, "MiddleTable.hbm.xml"))
				.addFile( new File(outputDir, "LeftTable.hbm.xml"))
				.addFile( new File(outputDir, "RightTable.hbm.xml"));
	        Metadata metadata = mds.buildMetadata();
	        new SchemaValidator().validate(metadata, serviceRegistry);
		} finally {
			Thread.currentThread().setContextClassLoader(oldLoader);			
		}
	}
	
	@Test
	public void testGenerateAnnotatedClassesAndReadable() throws MappingException, ClassNotFoundException, MalformedURLException {
		File outputDir = temporaryFolder.getRoot();
		MetadataHelper.getMetadata(localCfg);
		POJOExporter exporter = new POJOExporter(localCfg, outputDir);
		exporter.setTemplatePath(new String[0]);
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
			MetadataSources mds = new MetadataSources(serviceRegistry);
			mds.addAnnotatedClass(personClass)
				.addAnnotatedClass(multiPersonClass)
				.addAnnotatedClass(addressMultiPerson)
				.addAnnotatedClass(addressMultiPersonId)
				.addAnnotatedClass(addressPerson)
				.addAnnotatedClass(multiPersonIdClass)
				.addAnnotatedClass(middleClass)
				.addAnnotatedClass(rightClass)
				.addAnnotatedClass(leftClass);
			Metadata metadata = mds.buildMetadata();			
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
