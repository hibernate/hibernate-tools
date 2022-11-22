package org.hibernate.tool.orm.jbt.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	private RevengConfiguration jdbcMetadataConfiguration = null;
	
	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	@BeforeEach
	public void beforeEach() {
		jdbcMetadataConfiguration = new RevengConfiguration();
	}
	
	@Test
	public void testInstance() {
		assertNotNull(jdbcMetadataConfiguration);
	}
	
	@Test
	public void testGetProperties() {
		Properties properties = new Properties();
		assertNotNull(jdbcMetadataConfiguration.properties);
		assertNotSame(properties,  jdbcMetadataConfiguration.getProperties());
		jdbcMetadataConfiguration.properties = properties;
		assertSame(properties, jdbcMetadataConfiguration.getProperties());
	}
	
	@Test
	public void testSetProperties() {
		Properties properties = new Properties();
		assertNotNull(jdbcMetadataConfiguration.properties);
		assertNotSame(properties,  jdbcMetadataConfiguration.getProperties());
		jdbcMetadataConfiguration.setProperties(properties);
		assertSame(properties, jdbcMetadataConfiguration.getProperties());
	}
	
	@Test
	public void testGetProperty() {
		assertNull(jdbcMetadataConfiguration.getProperty("foo"));
		jdbcMetadataConfiguration.properties.put("foo", "bar");
		assertEquals("bar", jdbcMetadataConfiguration.getProperty("foo"));
	}

	@Test
	public void testSetProperty() {
		assertNull(jdbcMetadataConfiguration.properties.get("foo"));
		jdbcMetadataConfiguration.setProperty("foo", "bar");
		assertEquals("bar", jdbcMetadataConfiguration.properties.get("foo"));
	}
	
	@Test
	public void testAddProperties() {
		Properties properties = new Properties();
		properties.put("foo", "bar");
		assertNull(jdbcMetadataConfiguration.properties.get("foo"));
		jdbcMetadataConfiguration.addProperties(properties);
		assertEquals("bar", jdbcMetadataConfiguration.properties.get("foo"));
	}
	
	@Test
	public void testGetReverseEngineeringStrategy() {
		RevengStrategy strategy = new DefaultStrategy();
		assertNull(jdbcMetadataConfiguration.getReverseEngineeringStrategy());
		jdbcMetadataConfiguration.revengStrategy = strategy;
		assertSame(strategy, jdbcMetadataConfiguration.getReverseEngineeringStrategy());
	}
	
	@Test
	public void testSetReverseEngineeringStrategy() {
		RevengStrategy strategy = new DefaultStrategy();
		assertNull(jdbcMetadataConfiguration.revengStrategy);
		jdbcMetadataConfiguration.setReverseEngineeringStrategy(strategy);
		assertSame(strategy, jdbcMetadataConfiguration.revengStrategy);
	}
	
	@Test
	public void testPreferBasicCompositeIds() {
		assertTrue(jdbcMetadataConfiguration.preferBasicCompositeIds());
		jdbcMetadataConfiguration.properties.put(MetadataConstants.PREFER_BASIC_COMPOSITE_IDS, false);
		assertFalse(jdbcMetadataConfiguration.preferBasicCompositeIds());
	}
	
	@Test
	public void testSetPreferBasicCompositeIds() {
		assertNull(
				jdbcMetadataConfiguration.properties.get(
						MetadataConstants.PREFER_BASIC_COMPOSITE_IDS));
		jdbcMetadataConfiguration.setPreferBasicCompositeIds(true);
		assertEquals(
				true, 
				jdbcMetadataConfiguration.properties.get(
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
		assertNull(jdbcMetadataConfiguration.getMetadata());
		jdbcMetadataConfiguration.metadata = metadata;
		assertSame(metadata, jdbcMetadataConfiguration.getMetadata());
	}
	
	@Test
	public void testReadFromJdbc() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		jdbcMetadataConfiguration.properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		jdbcMetadataConfiguration.properties.put("hibernate.default_schema", "PUBLIC");
		jdbcMetadataConfiguration.revengStrategy = new DefaultStrategy();
		assertNull(jdbcMetadataConfiguration.metadata);
		jdbcMetadataConfiguration.readFromJdbc();
		assertNotNull(jdbcMetadataConfiguration.metadata);
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}
	
	@Test
	public void testGetClassMappings() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		jdbcMetadataConfiguration.properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		jdbcMetadataConfiguration.properties.put("hibernate.default_schema", "PUBLIC");
		jdbcMetadataConfiguration.revengStrategy = new DefaultStrategy();
		jdbcMetadataConfiguration.readFromJdbc();
		Iterator<PersistentClass> classMappings = jdbcMetadataConfiguration.getClassMappings();
		assertNotNull(classMappings);
		assertTrue(classMappings.hasNext());
		PersistentClass pc = classMappings.next();
		assertEquals(pc.getEntityName(), "Foo");
		assertFalse(classMappings.hasNext());
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}

}
