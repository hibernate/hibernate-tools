package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.tool.orm.jbt.internal.factory.QueryWrapperFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class QueryWrapperTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='hibernate.connection.url'>jdbc:h2:mem:test</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.api'>" +
			"  <class name='QueryWrapperTest$Foo' table='FOO'>" + 
			"    <id name='id' access='field' />" +
			"    <property name='bars' access='field' type='string'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public int id;
		public String bars;
	}
	
	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	@TempDir
	public File tempDir;
	
	private QueryWrapper simpleQueryWrapper = null;
	private Query<?> wrappedSimpleQuery = null;
	
	private QueryWrapper namedParameterizedQueryWrapper = null;
	private Query<?> wrappedNamedParameterizedQuery = null;
	
	private QueryWrapper positionalParameterizedQueryWrapper = null;
	private Query<?> wrappedPositionalParameterizedQuery = null;
	
	private QueryWrapper collectionParameterizedQueryWrapper = null;
	private Query<?> wrappedCollectionParameterizedQuery = null;
	
	private SessionFactory sessionFactory = null;
	private Connection connection = null;
	private Statement statement = null;
	
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void before() throws Exception {
		tempDir = Files.createTempDirectory("temp").toFile();
		createDatabase();
		createSessionFactory();
		Session session = sessionFactory.openSession();
		wrappedSimpleQuery = session.createQuery(
				"from " + Foo.class.getName());
		wrappedNamedParameterizedQuery = session.createQuery(
				"from " + Foo.class.getName() + " where id = :foo");
		wrappedPositionalParameterizedQuery = session.createQuery(
				"from " + Foo.class.getName() + " where id = ?1");
		wrappedCollectionParameterizedQuery = session.createQuery(
				"from " + Foo.class.getName() + " where id in :foo");
		simpleQueryWrapper = QueryWrapperFactory
				.createQueryWrapper(wrappedSimpleQuery);
		namedParameterizedQueryWrapper = QueryWrapperFactory
				.createQueryWrapper(wrappedNamedParameterizedQuery);
		positionalParameterizedQueryWrapper = QueryWrapperFactory
				.createQueryWrapper(wrappedPositionalParameterizedQuery);
		collectionParameterizedQueryWrapper = QueryWrapperFactory
				.createQueryWrapper(wrappedCollectionParameterizedQuery);
	}
	
	@AfterEach
	public void afterEach() throws Exception {
		dropDatabase();
	}
	
	@Test
	public void testCreateQueryWrapper() {
		assertNotNull(wrappedSimpleQuery);
		assertNotNull(simpleQueryWrapper);
		assertNotNull(wrappedNamedParameterizedQuery);
		assertNotNull(namedParameterizedQueryWrapper);
		assertNotNull(wrappedPositionalParameterizedQuery);
		assertNotNull(positionalParameterizedQueryWrapper);
		assertNotNull(wrappedCollectionParameterizedQuery);
		assertNotNull(collectionParameterizedQueryWrapper);
	}
	
	@Test
	public void testList() throws Exception {
		List<?> result = simpleQueryWrapper.list();
		assertTrue(result.isEmpty());
		statement.execute("INSERT INTO FOO VALUES(1, 'bars')");
		result = simpleQueryWrapper.list();
		assertEquals(1, result.size());
		Object obj = result.get(0);
		assertTrue(obj instanceof Foo);
		Foo foo = (Foo)obj;
		assertEquals(1, foo.id);
		assertEquals("bars", foo.bars);
	}
	
	@Test
	public void testSetMaxResults() {
		simpleQueryWrapper.setMaxResults(1);
		assertEquals(1, wrappedSimpleQuery.getMaxResults());
		simpleQueryWrapper.setMaxResults(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, wrappedSimpleQuery.getMaxResults());
	}
	
	@Test
	public void testSetParameterList() {
		QueryParameterBinding<?> foo = null;
		assertNull(foo);
		QueryParameterBinding<?> binding = 
				((QuerySqmImpl<?>)wrappedCollectionParameterizedQuery).getParameterBindings().getBinding("foo");
		assertFalse(binding.isBound());
		collectionParameterizedQueryWrapper.setParameterList("foo", Arrays.asList(1), new Object());
		assertTrue(binding.isBound());
	}
	
	@Test
	public void testSetNamedParameter() {
		QueryParameterBinding<?> binding = 
				((QuerySqmImpl<?>)wrappedNamedParameterizedQuery).getParameterBindings().getBinding("foo");
		assertFalse(binding.isBound());
		namedParameterizedQueryWrapper.setParameter("foo", 1, new Object());
		assertTrue(binding.isBound());
		assertEquals(1, binding.getBindValue());
	}
	
	@Test
	public void testSetPositionalParameter() {
		QueryParameterBinding<?> binding = 
				((QuerySqmImpl<?>)wrappedPositionalParameterizedQuery).getParameterBindings().getBinding(1);
		assertFalse(binding.isBound());
		positionalParameterizedQueryWrapper.setParameter(1, 1, new Object());
		assertTrue(binding.isBound());
		assertEquals(1, binding.getBindValue());
	}
	
	@Test
	public void testGetReturnAliases() {
		String[] aliases = simpleQueryWrapper.getReturnAliases();
		assertNotNull(aliases);
		assertEquals(0, aliases.length);
	}
	
	@Test
	public void testGetReturnTypes() {
		TypeWrapper[] returnTypes = simpleQueryWrapper.getReturnTypes();
		assertNotNull(returnTypes);
		assertEquals(0, returnTypes.length);
	}
	
	private void createDatabase() throws Exception {
		connection = DriverManager.getConnection("jdbc:h2:mem:test");
		statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bars varchar(255))");
	}
	
	private void createSessionFactory() throws Exception {
		File cfgXmlFile = new File(tempDir, "hibernate.cfg.xml");
		FileWriter fileWriter = new FileWriter(cfgXmlFile);
		fileWriter.write(TEST_CFG_XML_STRING);
		fileWriter.close();
		File hbmXmlFile = new File(tempDir, "Foo.hbm.xml");
		fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		Configuration configuration = new Configuration();
		configuration.addFile(hbmXmlFile);
		configuration.configure(cfgXmlFile);
		sessionFactory = configuration.buildSessionFactory();
	}
	
	private void dropDatabase() throws Exception {
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}
		
}
