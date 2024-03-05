package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.orm.jbt.internal.factory.ConfigurationWrapperFactory;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationWrapperTest {

	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.api'>" +
			"  <class name='ConfigurationWrapperTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String id;
	}
	
	private ConfigurationWrapper nativeConfigurationWrapper = null;
	private NativeConfiguration wrappedNativeConfiguration = null;
	private ConfigurationWrapper revengConfigurationWrapper = null;
	private RevengConfiguration wrappedRevengConfiguration = null;
	private ConfigurationWrapper jpaConfigurationWrapper = null;
	private JpaConfiguration wrappedJpaConfiguration = null;

	@BeforeEach
	public void beforeEach() throws Exception {
		initializeFacadesAndTargets();
	}	
	
	@Test
	public void testConstruction() {
		assertNotNull(nativeConfigurationWrapper);
		assertNotNull(wrappedNativeConfiguration);
		assertNotNull(revengConfigurationWrapper);
		assertNotNull(wrappedRevengConfiguration);
		assertNotNull(jpaConfigurationWrapper);
		assertNotNull(wrappedJpaConfiguration);
	}

	@Test
	public void testGetProperty() {
		// For native configuration
		assertNull(nativeConfigurationWrapper.getProperty("foo"));
		wrappedNativeConfiguration.setProperty("foo", "bar");
		assertEquals("bar", nativeConfigurationWrapper.getProperty("foo"));
		// For reveng configuration
		assertNull(revengConfigurationWrapper.getProperty("foo"));
		wrappedRevengConfiguration.setProperty("foo", "bar");
		assertEquals("bar", revengConfigurationWrapper.getProperty("foo"));
		// For jpa configuration
		assertNull(jpaConfigurationWrapper.getProperty("foo"));
		wrappedJpaConfiguration.setProperty("foo", "bar");
		assertEquals("bar", jpaConfigurationWrapper.getProperty("foo"));
	}

	@Test
	public void testAddFile() throws Exception {
		File testFile = File.createTempFile("test", "hbm.xml");
		PrintWriter printWriter = new PrintWriter(testFile);
		printWriter.write(TEST_HBM_XML_STRING);
		printWriter.close();
		testFile.deleteOnExit();
		// For native configuration
		MetadataSources metadataSources = MetadataHelper.getMetadataSources(wrappedNativeConfiguration);
		assertTrue(metadataSources.getXmlBindings().isEmpty());
		assertSame(
				nativeConfigurationWrapper,
				nativeConfigurationWrapper.addFile(testFile));
		assertFalse(metadataSources.getXmlBindings().isEmpty());
		Binding<?> binding = metadataSources.getXmlBindings().iterator().next();
		assertEquals(testFile.getAbsolutePath(), binding.getOrigin().getName());
		// For reveng configuration
		try {
			revengConfigurationWrapper.addFile(testFile);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'addFile' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationWrapper.addFile(testFile);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'addFile' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test 
	public void testSetProperty() {
		// For native configuration
		assertNull(wrappedNativeConfiguration.getProperty("foo"));
		nativeConfigurationWrapper.setProperty("foo", "bar");
		assertEquals("bar", wrappedNativeConfiguration.getProperty("foo"));
		// For reveng configuration
		assertNull(wrappedRevengConfiguration.getProperty("foo"));
		revengConfigurationWrapper.setProperty("foo", "bar");
		assertEquals("bar", wrappedRevengConfiguration.getProperty("foo"));
		// For jpa configuration
		assertNull(wrappedJpaConfiguration.getProperty("foo"));
		jpaConfigurationWrapper.setProperty("foo", "bar");
		assertEquals("bar", wrappedJpaConfiguration.getProperty("foo"));
	}

	@Test 
	public void testSetProperties() {
		Properties testProperties = new Properties();
		// For native configuration
		assertNotSame(testProperties, wrappedNativeConfiguration.getProperties());
		assertSame(
				nativeConfigurationWrapper, 
				nativeConfigurationWrapper.setProperties(testProperties));
		assertSame(testProperties, wrappedNativeConfiguration.getProperties());
		// For reveng configuration
		assertNotSame(testProperties, wrappedRevengConfiguration.getProperties());
		assertSame(
				revengConfigurationWrapper, 
				revengConfigurationWrapper.setProperties(testProperties));
		assertSame(testProperties, wrappedRevengConfiguration.getProperties());
		// For jpa configuration
		assertNotSame(testProperties, wrappedJpaConfiguration.getProperties());
		assertSame(
				jpaConfigurationWrapper, 
				jpaConfigurationWrapper.setProperties(testProperties));
		assertSame(testProperties, wrappedJpaConfiguration.getProperties());
	}
	
	@Test
	public void testSetEntityResolver() throws Exception {
		EntityResolver testResolver = new DefaultHandler();
		// For native configuration
		Field entityResolverField = wrappedNativeConfiguration.getClass().getDeclaredField("entityResolver");
		entityResolverField.setAccessible(true);
		assertNull(entityResolverField.get(wrappedNativeConfiguration));
		nativeConfigurationWrapper.setEntityResolver(testResolver);
		assertNotNull(entityResolverField.get(wrappedNativeConfiguration));
		assertSame(testResolver, entityResolverField.get(wrappedNativeConfiguration));
		// For reveng configuration
		try {
			revengConfigurationWrapper.setEntityResolver(testResolver);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setEntityResolver' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationWrapper.setEntityResolver(testResolver);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setEntityResolver' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testSetNamingStrategy() throws Exception {
		NamingStrategy namingStrategy = new DefaultNamingStrategy();
		// For native configuration
		Field namingStrategyField = wrappedNativeConfiguration.getClass().getDeclaredField("namingStrategy");
		namingStrategyField.setAccessible(true);
		assertNull(namingStrategyField.get(wrappedNativeConfiguration));
		nativeConfigurationWrapper.setNamingStrategy(namingStrategy);
		assertNotNull(namingStrategyField.get(wrappedNativeConfiguration));
		assertSame(namingStrategyField.get(wrappedNativeConfiguration), namingStrategy);
		// For reveng configuration
		try {
			revengConfigurationWrapper.setNamingStrategy(namingStrategy);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setNamingStrategy' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationWrapper.setNamingStrategy(namingStrategy);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setNamingStrategy' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testGetProperties() {
		Properties testProperties = new Properties();
		// For native configuration
		assertNotSame(testProperties, nativeConfigurationWrapper.getProperties());
		wrappedNativeConfiguration.setProperties(testProperties);
		assertSame(testProperties, nativeConfigurationWrapper.getProperties());
		// For reveng configuration
		assertNotSame(testProperties, revengConfigurationWrapper.getProperties());
		wrappedRevengConfiguration.setProperties(testProperties);
		assertSame(testProperties, revengConfigurationWrapper.getProperties());
		// For jpa configuration
		assertNotSame(testProperties, jpaConfigurationWrapper.getProperties());
		wrappedJpaConfiguration.setProperties(testProperties);
		assertSame(testProperties, jpaConfigurationWrapper.getProperties());
	}
	
	@Test
	public void testAddProperties() {
		Properties testProperties = new Properties();
		testProperties.put("foo", "bar");
		// For native configuration
		assertNull(wrappedNativeConfiguration.getProperty("foo"));
		nativeConfigurationWrapper.addProperties(testProperties);
		assertEquals("bar", wrappedNativeConfiguration.getProperty("foo"));
		// For reveng configuration
		assertNull(wrappedRevengConfiguration.getProperty("foo"));
		revengConfigurationWrapper.addProperties(testProperties);
		assertEquals("bar", wrappedRevengConfiguration.getProperty("foo"));
		// For jpa configuration
		assertNull(wrappedJpaConfiguration.getProperty("foo"));
		jpaConfigurationWrapper.addProperties(testProperties);
		assertEquals("bar", wrappedJpaConfiguration.getProperty("foo"));
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
		
		// For native configuration
		String fooClassName = 
				"org.hibernate.tool.orm.jbt.api.ConfigurationWrapperTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(wrappedNativeConfiguration);
		assertNull(metadata.getEntityBinding(fooClassName));
		nativeConfigurationWrapper.configure(document);
		metadata = MetadataHelper.getMetadata(wrappedNativeConfiguration);
		assertNotNull(metadata.getEntityBinding(fooClassName));
		// For reveng configuration
		try {
			revengConfigurationWrapper.configure(document);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationWrapper.configure(document);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	private void initializeFacadesAndTargets() {
		wrappedNativeConfiguration = new NativeConfiguration();
		wrappedNativeConfiguration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		wrappedNativeConfiguration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		nativeConfigurationWrapper = ConfigurationWrapperFactory.createConfigurationWrapper(wrappedNativeConfiguration);
		wrappedRevengConfiguration = new RevengConfiguration();
		revengConfigurationWrapper = ConfigurationWrapperFactory.createConfigurationWrapper(wrappedRevengConfiguration);
		wrappedJpaConfiguration = new JpaConfiguration(null, null);
		jpaConfigurationWrapper = ConfigurationWrapperFactory.createConfigurationWrapper(wrappedJpaConfiguration);
	}
	
}
