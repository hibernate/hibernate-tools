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
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.orm.jbt.internal.factory.ConfigurationWrapperFactory;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.hibernate.tool.orm.jbt.wrp.ConfigurationWrapperFactoryTest.FooBar;
import org.hibernate.tool.orm.jbt.wrp.SessionFactoryWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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
	
	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory name='bar'>" + 
			"    <mapping resource='Foo.hbm.xml' />" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String PERSISTENCE_XML = 
			"<persistence version='2.2'" +
	        "  xmlns='http://xmlns.jcp.org/xml/ns/persistence'" +
		    "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
	        "  xsi:schemaLocation='http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd'>" +
	        "  <persistence-unit name='foobar'>" +
	        "    <class>"+ FooBar.class.getName()  +"</class>" +
	        "    <properties>" +
	        "      <property name='" + AvailableSettings.DIALECT + "' value='" + MockDialect.class.getName() + "'/>" +
	        "      <property name='" + AvailableSettings.CONNECTION_PROVIDER + "' value='" + MockConnectionProvider.class.getName() + "'/>" +
	        "      <property name='foo' value='bar'/>" +
	        "    </properties>" +
	        "  </persistence-unit>" +
			"</persistence>";
	
	static class Foo {
		public String id;
	}
	
	@TempDir
	public File tempRoot;
	
	private ClassLoader original = null;

	private ConfigurationWrapper nativeConfigurationWrapper = null;
	private NativeConfiguration wrappedNativeConfiguration = null;
	private ConfigurationWrapper revengConfigurationWrapper = null;
	private RevengConfiguration wrappedRevengConfiguration = null;
	private ConfigurationWrapper jpaConfigurationWrapper = null;
	private JpaConfiguration wrappedJpaConfiguration = null;

	@BeforeEach
	public void beforeEach() throws Exception {
		tempRoot = Files.createTempDirectory("temp").toFile();
		swapClassLoader();
		createPersistenceXml();
		initializeFacadesAndTargets();
	}	
	
	@AfterEach
	public void afterEach() {
		Thread.currentThread().setContextClassLoader(original);
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
	
	@Test
	public void testConfigureFile() throws Exception {
		// For native configuration
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File cfgXmlFile = new File(new File(url.toURI()), "foobarfile.cfg.xml");
		FileWriter fileWriter = new FileWriter(cfgXmlFile);
		fileWriter.write(TEST_CFG_XML_STRING);
		fileWriter.close();
		File hbmXmlFile = new File(new File(url.toURI()), "Foo.hbm.xml");
		fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();

		String fooClassName = 
				"org.hibernate.tool.orm.jbt.api.ConfigurationWrapperTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(wrappedNativeConfiguration);
		assertNull(metadata.getEntityBinding(fooClassName));
		Field metadataField = NativeConfiguration.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		metadataField.set(wrappedNativeConfiguration, null);
		nativeConfigurationWrapper.configure(cfgXmlFile);
		metadata = MetadataHelper.getMetadata(wrappedNativeConfiguration);
		assertNotNull(metadata.getEntityBinding(fooClassName));
		assertTrue(cfgXmlFile.delete());
		assertTrue(hbmXmlFile.delete());

		// For reveng configuration
		try {
			revengConfigurationWrapper.configure(cfgXmlFile);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationWrapper.configure(cfgXmlFile);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testConfigureDefault() throws Exception {
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File cfgXmlFile = new File(new File(url.toURI()), "hibernate.cfg.xml");
		cfgXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(cfgXmlFile);
		fileWriter.write(TEST_CFG_XML_STRING);
		fileWriter.close();
		File hbmXmlFile = new File(new File(url.toURI()), "Foo.hbm.xml");
		hbmXmlFile.deleteOnExit();
		fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		
		// For native configuration
		String fooClassName = 
				"org.hibernate.tool.orm.jbt.api.ConfigurationWrapperTest$Foo";
		Metadata metadata = MetadataHelper.getMetadata(wrappedNativeConfiguration);
		assertNull(metadata.getEntityBinding(fooClassName));
		Field metadataField = NativeConfiguration.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		metadataField.set(wrappedNativeConfiguration, null);
		nativeConfigurationWrapper.configure();
		metadata = MetadataHelper.getMetadata(wrappedNativeConfiguration);
		assertNotNull(metadata.getEntityBinding(fooClassName));
		// For reveng configuration
		try {
			revengConfigurationWrapper.configure();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationWrapper.configure();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'configure' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testAddClass() throws Exception {
		String fooHbmXmlFilePath = "org/hibernate/tool/orm/jbt/api/";
		String fooHbmXmlFileName = "ConfigurationWrapperTest$Foo.hbm.xml";
		String fooClassName = 
				"org.hibernate.tool.orm.jbt.api.ConfigurationWrapperTest$Foo";
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File hbmXmlFileDir = new File(new File(url.toURI()),fooHbmXmlFilePath);
		hbmXmlFileDir.deleteOnExit();
		hbmXmlFileDir.mkdirs();
		File hbmXmlFile = new File(hbmXmlFileDir, fooHbmXmlFileName);
		hbmXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();

		// For native configuration		
		Metadata metadata = MetadataHelper.getMetadata(wrappedNativeConfiguration);
		assertNull(metadata.getEntityBinding(fooClassName));
		Field metadataField = NativeConfiguration.class.getDeclaredField("metadata");
		metadataField.setAccessible(true);
		metadataField.set(wrappedNativeConfiguration, null);
		nativeConfigurationWrapper.addClass(Foo.class);
		metadata = MetadataHelper.getMetadata(wrappedNativeConfiguration);
		assertNotNull(metadata.getEntityBinding(fooClassName));
		// For reveng configuration
		try {
			revengConfigurationWrapper.addClass(Foo.class);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'addClass' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationWrapper.addClass(Foo.class);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'addClass' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testBuildMappings() throws Exception {
		// For native configuration
		Field metadataField = wrappedNativeConfiguration.getClass().getDeclaredField("metadata");
		metadataField.setAccessible(true);
		assertNull(metadataField.get(wrappedNativeConfiguration));
		nativeConfigurationWrapper.buildMappings();
		assertNotNull(metadataField.get(wrappedNativeConfiguration));
		// For reveng configuration
		metadataField = wrappedRevengConfiguration.getClass().getDeclaredField("metadata");
		metadataField.setAccessible(true);
		wrappedRevengConfiguration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		wrappedRevengConfiguration.setProperty("hibernate.default_schema", "PUBLIC");
		assertNull(metadataField.get(wrappedRevengConfiguration));
		revengConfigurationWrapper.buildMappings();
		assertNotNull(metadataField.get(wrappedRevengConfiguration));
		// For jpa configuration
		metadataField = wrappedJpaConfiguration.getClass().getDeclaredField("metadata");
		metadataField.setAccessible(true);
		assertNull(metadataField.get(wrappedJpaConfiguration));
		jpaConfigurationWrapper.buildMappings();
		assertNotNull(metadataField.get(wrappedJpaConfiguration));
	}

	@Test
	public void testBuildSessionFactory() throws Throwable {
		// For native configuration
		SessionFactory sessionFactory = 
				nativeConfigurationWrapper.buildSessionFactory();
		assertNotNull(sessionFactory);
		assertTrue(sessionFactory instanceof SessionFactoryWrapper);
		sessionFactory = null;
		assertNull(sessionFactory);
		// For reveng configuration 
		try {
			revengConfigurationWrapper.buildSessionFactory();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'buildSessionFactory' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		sessionFactory = jpaConfigurationWrapper.buildSessionFactory();
		assertNotNull(sessionFactory);
		assertTrue(sessionFactory instanceof SessionFactoryWrapper);
	}
	
	@Test
	public void testGetClassMappings() throws Exception {
		// For native configuration
		String fooHbmXmlFilePath = "org/hibernate/tool/orm/jbt/api";
		String fooHbmXmlFileName = "ConfigurationWrapperTest$Foo.hbm.xml";
		String fooClassName = 
				"org.hibernate.tool.orm.jbt.api.ConfigurationWrapperTest$Foo";
		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		File hbmXmlFileDir = new File(new File(url.toURI()),fooHbmXmlFilePath);
		hbmXmlFileDir.deleteOnExit();
		hbmXmlFileDir.mkdirs();
		File hbmXmlFile = new File(hbmXmlFileDir, fooHbmXmlFileName);
		hbmXmlFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		wrappedNativeConfiguration.addClass(Foo.class);
		Iterator<PersistentClass> classMappings = nativeConfigurationWrapper.getClassMappings();
		assertTrue(classMappings.hasNext());
		PersistentClass fooClassFacade = classMappings.next();
		assertSame(fooClassFacade.getEntityName(), fooClassName);
		classMappings = null;
		assertNull(classMappings);
		// For reveng configuration
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		wrappedRevengConfiguration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		wrappedRevengConfiguration.setProperty("hibernate.default_schema", "PUBLIC");
		classMappings = revengConfigurationWrapper.getClassMappings();
		assertNotNull(classMappings);
		assertFalse(classMappings.hasNext());
		((RevengConfiguration)wrappedRevengConfiguration).readFromJDBC();
		classMappings = revengConfigurationWrapper.getClassMappings();
		assertNotNull(classMappings);
		assertTrue(classMappings.hasNext());
		fooClassFacade = classMappings.next();
		assertEquals(fooClassFacade.getEntityName(), "Foo");
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
		classMappings = null;
		assertNull(classMappings);
		// For jpa configuration
		classMappings = jpaConfigurationWrapper.getClassMappings();
		assertNotNull(classMappings);
		assertTrue(classMappings.hasNext());
		fooClassFacade = classMappings.next();
		assertEquals(fooClassFacade.getEntityName(), FooBar.class.getName());
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() {
		// For native configuration 
		try {
			nativeConfigurationWrapper.setPreferBasicCompositeIds(false);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setPreferBasicCompositeIds' should not be called on instances of " + NativeConfiguration.class.getName());
		}
		// For reveng configuration
		// the default is true
		assertTrue(((RevengConfiguration)wrappedRevengConfiguration).preferBasicCompositeIds());
		revengConfigurationWrapper.setPreferBasicCompositeIds(false);
		assertFalse(((RevengConfiguration)wrappedRevengConfiguration).preferBasicCompositeIds());
		// For jpa configuration 
		try {
			jpaConfigurationWrapper.setPreferBasicCompositeIds(false);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setPreferBasicCompositeIds' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() {
		RevengStrategy reverseEngineeringStrategy = new DefaultStrategy();
		// For native configuration 
		try {
			nativeConfigurationWrapper.setReverseEngineeringStrategy(reverseEngineeringStrategy);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setReverseEngineeringStrategy' should not be called on instances of " + NativeConfiguration.class.getName());
		}
		// For reveng configuration
		assertNotSame(
				reverseEngineeringStrategy,
				((RevengConfiguration)wrappedRevengConfiguration).getReverseEngineeringStrategy());
		revengConfigurationWrapper.setReverseEngineeringStrategy(reverseEngineeringStrategy);
		assertSame(
				reverseEngineeringStrategy, 
				((RevengConfiguration)wrappedRevengConfiguration).getReverseEngineeringStrategy());
		// For jpa configuration
		try {
			jpaConfigurationWrapper.setReverseEngineeringStrategy(reverseEngineeringStrategy);
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'setReverseEngineeringStrategy' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testReadFromJDBC() throws Exception {
		// For native configuration 
		try {
			nativeConfigurationWrapper.readFromJDBC();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'readFromJDBC' should not be called on instances of " + NativeConfiguration.class.getName());
		}
		// For reveng configuration
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		wrappedRevengConfiguration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		wrappedRevengConfiguration.setProperty("hibernate.default_schema", "PUBLIC");
		Metadata metadata = ((RevengConfiguration)wrappedRevengConfiguration).getMetadata();
		assertNull(metadata);
		revengConfigurationWrapper.readFromJDBC();
		metadata = ((RevengConfiguration)wrappedRevengConfiguration).getMetadata();
		Iterator<PersistentClass> iterator = metadata.getEntityBindings().iterator();
		PersistentClass persistentClass = iterator.next();
		assertEquals("Foo", persistentClass.getClassName());
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
		// For jpa configuration
		try {
			jpaConfigurationWrapper.readFromJDBC();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'readFromJDBC' should not be called on instances of " + JpaConfiguration.class.getName());
		}
	}
	
	@Test
	public void testGetClassMapping() throws Exception {
		// For native configuration
		String fooHbmXmlFilePath = "org/hibernate/tool/orm/jbt/api";
		String fooHbmXmlFileName = "ConfigurationWrapperTest$Foo.hbm.xml";
		String fooClassName = 
				"org.hibernate.tool.orm.jbt.api.ConfigurationWrapperTest$Foo";
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
		assertNull(nativeConfigurationWrapper.getClassMapping("Foo"));
		metadataField.set(wrappedNativeConfiguration, null);
		wrappedNativeConfiguration.addClass(Foo.class);
		assertNotNull(nativeConfigurationWrapper.getClassMapping(fooClassName));
		// For reveng configuration
		assertNull(revengConfigurationWrapper.getClassMapping("Foo"));
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		wrappedRevengConfiguration.setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
		wrappedRevengConfiguration.setProperty("hibernate.default_schema", "PUBLIC");
		((RevengConfiguration)wrappedRevengConfiguration).readFromJDBC();
		assertNotNull(revengConfigurationWrapper.getClassMapping("Foo"));
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
		// For jpa configuration
		assertNull(jpaConfigurationWrapper.getClassMapping("Bar"));
		assertNotNull(jpaConfigurationWrapper.getClassMapping(FooBar.class.getName()));
	}

	@Test
	public void testGetNamingStrategy() {
		// For native configuration 
		NamingStrategy namingStrategy = new DefaultNamingStrategy();
		assertNull(nativeConfigurationWrapper.getNamingStrategy());
		((NativeConfiguration)wrappedNativeConfiguration).setNamingStrategy(namingStrategy);
		assertSame(nativeConfigurationWrapper.getNamingStrategy(), namingStrategy);
		// For reveng configuration 
		try {
			revengConfigurationWrapper.getNamingStrategy();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'getNamingStrategy' should not be called on instances of " + RevengConfiguration.class.getName());
		}
		// For jpa configuration
		try {
			jpaConfigurationWrapper.getNamingStrategy();
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'getNamingStrategy' should not be called on instances of " + JpaConfiguration.class.getName());
		}
		
	}
	
	private void createPersistenceXml() throws Exception {
		File metaInf = new File(tempRoot, "META-INF");
		metaInf.mkdirs();
		File persistenceXml = new File(metaInf, "persistence.xml");
		persistenceXml.createNewFile();
		FileWriter fileWriter = new FileWriter(persistenceXml);
		fileWriter.write(PERSISTENCE_XML);
		fileWriter.close();
	}
	
	private void swapClassLoader() throws Exception {
		original = Thread.currentThread().getContextClassLoader();
		ClassLoader urlCl = URLClassLoader.newInstance(
				new URL[] { new URL(tempRoot.toURI().toURL().toString())} , 
				original);
		Thread.currentThread().setContextClassLoader(urlCl);
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
