package org.hibernate.tool.orm.jbt.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;

import org.h2.Driver;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.api.metadata.MetadataConstants;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RevengConfigurationTest {

	private RevengConfiguration revengConfiguration = null;
	
	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	@BeforeEach
	public void beforeEach() {
		revengConfiguration = new RevengConfiguration();
	}
	
	@Test
	public void testInstance() {
		assertNotNull(revengConfiguration);
	}
	
	@Test
	public void testGetProperties() {
		Properties properties = new Properties();
		assertNotNull(revengConfiguration.properties);
		assertNotSame(properties,  revengConfiguration.getProperties());
		revengConfiguration.properties = properties;
		assertSame(properties, revengConfiguration.getProperties());
	}
	
	@Test
	public void testSetProperties() {
		Properties properties = new Properties();
		assertNotNull(revengConfiguration.properties);
		assertNotSame(properties,  revengConfiguration.getProperties());
		assertSame(revengConfiguration, revengConfiguration.setProperties(properties));
		assertSame(properties, revengConfiguration.getProperties());
	}
	
	@Test
	public void testGetProperty() {
		assertNull(revengConfiguration.getProperty("foo"));
		revengConfiguration.properties.put("foo", "bar");
		assertEquals("bar", revengConfiguration.getProperty("foo"));
	}

	@Test
	public void testSetProperty() {
		assertNull(revengConfiguration.properties.get("foo"));
		revengConfiguration.setProperty("foo", "bar");
		assertEquals("bar", revengConfiguration.properties.get("foo"));
	}
	
	@Test
	public void testAddProperties() {
		Properties properties = new Properties();
		properties.put("foo", "bar");
		assertNull(revengConfiguration.properties.get("foo"));
		revengConfiguration.addProperties(properties);
		assertEquals("bar", revengConfiguration.properties.get("foo"));
	}
	
	@Test
	public void testGetReverseEngineeringStrategy() {
		RevengStrategy strategy = new DefaultStrategy();
		assertNull(revengConfiguration.getReverseEngineeringStrategy());
		revengConfiguration.revengStrategy = strategy;
		assertSame(strategy, revengConfiguration.getReverseEngineeringStrategy());
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() {
		RevengStrategy strategy = new DefaultStrategy();
		assertNull(revengConfiguration.revengStrategy);
		revengConfiguration.setReverseEngineeringStrategy(strategy);
		assertSame(strategy, revengConfiguration.revengStrategy);
	}
	
	@Test
	public void testPreferBasicCompositeIds() {
		assertTrue(revengConfiguration.preferBasicCompositeIds());
		revengConfiguration.properties.put(MetadataConstants.PREFER_BASIC_COMPOSITE_IDS, false);
		assertFalse(revengConfiguration.preferBasicCompositeIds());
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() {
		assertNull(
				revengConfiguration.properties.get(
						MetadataConstants.PREFER_BASIC_COMPOSITE_IDS));
		revengConfiguration.setPreferBasicCompositeIds(true);
		assertEquals(
				true, 
				revengConfiguration.properties.get(
						MetadataConstants.PREFER_BASIC_COMPOSITE_IDS));
	}
	
	@Test
	public void testGetMetadata() {
		Metadata metadata = (Metadata)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Metadata.class }, 
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
		assertNull(revengConfiguration.getMetadata());
		revengConfiguration.metadata = metadata;
		assertSame(metadata, revengConfiguration.getMetadata());
	}
	
	@Test
	public void testReadFromJDBC() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		revengConfiguration.properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		revengConfiguration.properties.put("hibernate.default_schema", "PUBLIC");
		revengConfiguration.revengStrategy = new DefaultStrategy();
		assertNull(revengConfiguration.metadata);
		revengConfiguration.readFromJDBC();
		assertNotNull(revengConfiguration.metadata);
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}
	
	@Test
	public void testGetClassMappings() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		revengConfiguration.properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		revengConfiguration.properties.put("hibernate.default_schema", "PUBLIC");
		revengConfiguration.revengStrategy = new DefaultStrategy();
		Iterator<PersistentClass> classMappings = revengConfiguration.getClassMappings();
		assertNotNull(classMappings);
		assertFalse(classMappings.hasNext());
		revengConfiguration.readFromJDBC();
		classMappings = revengConfiguration.getClassMappings();
		assertNotNull(classMappings);
		assertTrue(classMappings.hasNext());
		PersistentClass pc = classMappings.next();
		assertEquals(pc.getEntityName(), "Foo");
		assertFalse(classMappings.hasNext());
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}
	
	@Test
	public void testAddFile() {
		try {
			revengConfiguration.addFile(new File("Foo"));
			fail();
		} catch (RuntimeException e) {
			assertEquals(
					e.getMessage(),
					"Method 'addFile' should not be called on instances of " + RevengConfiguration.class.getName());
		}
	}

}
