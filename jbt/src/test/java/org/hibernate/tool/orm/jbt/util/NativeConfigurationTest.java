package org.hibernate.tool.orm.jbt.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class NativeConfigurationTest {
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.util'>" +
			"  <class name='NativeConfigurationTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String id;
	}
	
	private NativeConfiguration nativeConfiguration = null;
	
	@BeforeEach
	public void beforeEach() {
		nativeConfiguration = new NativeConfiguration();
		nativeConfiguration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		nativeConfiguration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
	}
	
	@Test
	public void testSetEntityResolver() throws Exception {
		Field field = NativeConfiguration.class.getDeclaredField("entityResolver");
		field.setAccessible(true);
		assertNull(field.get(nativeConfiguration));
		EntityResolver entityResolver = new DefaultHandler();
		nativeConfiguration.setEntityResolver(entityResolver);
		assertNotNull(field.get(nativeConfiguration));
		assertSame(field.get(nativeConfiguration), entityResolver);
	}
	
	@Test
	public void testSetNamingStrategy() throws Exception {
		Field field = NativeConfiguration.class.getDeclaredField("namingStrategy");
		field.setAccessible(true);
		assertNull(field.get(nativeConfiguration));
		NamingStrategy namingStrategy = new DefaultNamingStrategy();
		nativeConfiguration.setNamingStrategy(namingStrategy);
		assertNotNull(field.get(nativeConfiguration));
		assertSame(field.get(nativeConfiguration), namingStrategy);
	}

	@Test
	public void testConfigureDocument() throws Exception {
		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.newDocument();
		Element hibernateConfiguration = document.createElement("hibernate-configuration");
		document.appendChild(hibernateConfiguration);
		Element sessionFactory = document.createElement("session-factory");
		sessionFactory.setAttribute("name", "bar");
		hibernateConfiguration.appendChild(sessionFactory);
		Element mapping = document.createElement("mapping");
		mapping.setAttribute("resource", "Foo.hbm.xml");
		sessionFactory.appendChild(mapping);
		
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File hbmXmlFile = new File(new File(url.toURI()), "Foo.hbm.xml");
		hbmXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();

		String fooClassName = 
				"org.hibernate.tool.orm.jbt.util.NativeConfigurationTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(nativeConfiguration);
		assertNull(metadata.getEntityBinding(fooClassName));
		nativeConfiguration.configure(document);
		metadata = MetadataHelper.getMetadata(nativeConfiguration);
		assertNotNull(metadata.getEntityBinding(fooClassName));
	}
	
	@Test
	public void testBuildMappings() throws Exception {
		Field metadataField = NativeConfiguration.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		assertNull(metadataField.get(nativeConfiguration));
		nativeConfiguration.buildMappings();
		assertNotNull(metadataField.get(nativeConfiguration));
	}
	
	@Test
	public void testGetClassMappings() throws Exception {
		String fooHbmXmlFilePath = "org/hibernate/tool/orm/jbt/util";
		String fooHbmXmlFileName = "NativeConfigurationTest$Foo.hbm.xml";
		String fooClassName = 
				"org.hibernate.tool.orm.jbt.util.NativeConfigurationTest$Foo";
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File hbmXmlFileDir = new File(new File(url.toURI()),fooHbmXmlFilePath);
		hbmXmlFileDir.deleteOnExit();
		hbmXmlFileDir.mkdirs();
		File hbmXmlFile = new File(hbmXmlFileDir, fooHbmXmlFileName);
		hbmXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		Field metadataField = NativeConfiguration.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		Iterator<PersistentClass> classesIterator = nativeConfiguration.getClassMappings();
		assertFalse(classesIterator.hasNext());
		metadataField.set(nativeConfiguration, null);
		nativeConfiguration.addClass(Foo.class);
		classesIterator = nativeConfiguration.getClassMappings();
		assertTrue(classesIterator.hasNext());
		PersistentClass pc = classesIterator.next();
		assertEquals(fooClassName, pc.getEntityName());
	}
	
	@Test
	public void testGetNamingStrategy() throws Exception {
		Field field = NativeConfiguration.class.getDeclaredField("namingStrategy");
		field.setAccessible(true);
		assertNull(nativeConfiguration.getNamingStrategy());
		NamingStrategy namingStrategy = new DefaultNamingStrategy();
		field.set(nativeConfiguration, namingStrategy);
		assertNotNull(nativeConfiguration.getNamingStrategy());
		assertSame(nativeConfiguration.getNamingStrategy(), namingStrategy);
	}
	
	@Test
	public void testBuildSessionFactory() {
		try {
			nativeConfiguration.setPreferBasicCompositeIds(false);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setPreferBasicCompositeIds' should not be called on instances of " + NativeConfiguration.class.getName());
		}
	}

	@Test
	public void testSetReverseEngineeringStrategy() {
		try {
			nativeConfiguration.setReverseEngineeringStrategy(new DefaultStrategy());
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setReverseEngineeringStrategy' should not be called on instances of " + NativeConfiguration.class.getName());
		}
	}

	@Test
	public void testReadFromJDBC() {
		try {
			nativeConfiguration.readFromJDBC();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'readFromJDBC' should not be called on instances of " + NativeConfiguration.class.getName());
		}
	}

	@Test
	public void testGetEntityResolver() throws Exception {
		Field field = NativeConfiguration.class.getDeclaredField("entityResolver");
		field.setAccessible(true);
		assertNull(nativeConfiguration.getEntityResolver());
		EntityResolver entityResolver = new DefaultHandler();
		field.set(nativeConfiguration, entityResolver);
		assertNotNull(nativeConfiguration.getEntityResolver());
		assertSame(nativeConfiguration.getEntityResolver(), entityResolver);
	}
	
}
