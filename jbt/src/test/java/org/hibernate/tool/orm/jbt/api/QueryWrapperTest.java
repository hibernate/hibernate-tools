package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.internal.factory.QueryWrapperFactory;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
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
		Configuration configuration = new NativeConfiguration();
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
