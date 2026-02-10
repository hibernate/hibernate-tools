/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.jdbc2cfg.OneToOne;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.hibernate.MappingException;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	@TempDir
	public File outputDir = new File("output");
	
	private MetadataDescriptor metadataDescriptor = null;
	private Metadata metadata = null;

	@BeforeEach
	public void setUp() throws Exception {
		JdbcUtil.createDatabase(this);
		metadataDescriptor = MetadataDescriptorFactory.createReverseEngineeringDescriptor(null, null);
		metadata = metadataDescriptor.createMetadata();
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testOneToOneSingleColumnBiDirectional() {	
		PersistentClass person = metadata.getEntityBinding("Person");		
		Property addressProperty = person.getProperty("addressPerson");
		assertNotNull(addressProperty);			
		assertTrue(addressProperty.getValue() instanceof OneToOne);	
		OneToOne oto = (OneToOne) addressProperty.getValue();	
		assertEquals(oto.getColumnSpan(),1);
		assertEquals("Person", oto.getEntityName());
		assertEquals("AddressPerson", oto.getReferencedEntityName());
		assertEquals(2, person.getPropertyClosureSpan());		
		assertEquals("personId", person.getIdentifierProperty().getName());
		assertFalse(oto.isConstrained());		
		PersistentClass addressPerson = metadata.getEntityBinding("AddressPerson");
		Property personProperty = addressPerson.getProperty("person");
		assertNotNull(personProperty);
		assertTrue(personProperty.getValue() instanceof OneToOne);	
		oto = (OneToOne) personProperty.getValue();	
		assertTrue(oto.isConstrained());		
		assertEquals(oto.getColumnSpan(),1);
		assertEquals("AddressPerson", oto.getEntityName());
		assertEquals("Person", oto.getReferencedEntityName());
		assertEquals(2, addressPerson.getPropertyClosureSpan());
		assertEquals("addressId", addressPerson.getIdentifierProperty().getName());			
	}
	
	@Test
	public void testAddressWithForeignKeyGeneration() {
		PersistentClass address = metadata.getEntityBinding("AddressPerson");	
		assertEquals("foreign", ((SimpleValue)address.getIdentifier()).getIdentifierGeneratorStrategy());
	}

	@Test
	public void testOneToOneMultiColumnBiDirectional() {
		PersistentClass person = metadata.getEntityBinding("MultiPerson");	
		Property addressProperty = person.getProperty("addressMultiPerson");
		assertNotNull(addressProperty);		
		assertTrue(addressProperty.getValue() instanceof OneToOne);
		OneToOne oto = (OneToOne) addressProperty.getValue();
		assertEquals(oto.getColumnSpan(),2);
		assertEquals("MultiPerson", oto.getEntityName());
		assertEquals("AddressMultiPerson", oto.getReferencedEntityName());
		assertFalse(oto.isConstrained());
		assertEquals(2, person.getPropertyClosureSpan());		
		assertEquals("id", person.getIdentifierProperty().getName(), "compositeid gives generic id name");
		PersistentClass addressPerson = metadata.getEntityBinding("AddressMultiPerson");
		Property personProperty = addressPerson.getProperty("multiPerson");
		assertNotNull(personProperty);
		assertTrue(personProperty.getValue() instanceof OneToOne);
		oto = (OneToOne) personProperty.getValue();
		assertEquals(oto.getColumnSpan(),2);
		assertEquals("AddressMultiPerson", oto.getEntityName());
		assertEquals("MultiPerson", oto.getReferencedEntityName());
		assertEquals(2, addressPerson.getPropertyClosureSpan());
		assertEquals("id", addressPerson.getIdentifierProperty().getName(), "compositeid gives generic id name");
		assertTrue(oto.isConstrained());
	}

	@Test
	public void testBuildMappings() {	
		assertNotNull(metadata);		
	}

	@Test
	public void testGenerateMappingAndReadable() throws MalformedURLException {
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
		assertEquals(7, outputDir.listFiles().length);	
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
		assertEquals(9, outputDir.listFiles().length);
		JavaUtil.compile(outputDir);
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
		assertTrue(file.exists(), file + " does not exist");
		assertTrue(file.isFile(), file + " not a file");		
		assertTrue(file.length()>0, file + " does not have any contents");
	}

}
