package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.h2.Driver;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.wrp.QueryWrapperFactory.QueryWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class QueryWrapperFactoryTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='hibernate.connection.url'>jdbc:h2:mem:test</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.wrp'>" +
			"  <class name='QueryWrapperFactoryTest$Foo' table='FOO'>" + 
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
	
	private QueryWrapper<?> queryWrapper = null;
	private Query<?> wrappedQuery = null;
	
	private SessionFactory sessionFactory = null;
	private Connection connection = null;
	private Statement statement = null;
	
	
	@SuppressWarnings("deprecation")
	@BeforeEach
	public void before() throws Exception {
		tempDir = Files.createTempDirectory("temp").toFile();
		createDatabase();
		createSessionFactory();
		wrappedQuery = sessionFactory.openSession().createQuery("from " + Foo.class.getName());
		queryWrapper = QueryWrapperFactory.createQueryWrapper(wrappedQuery);
	}
	
	@AfterEach
	public void afterEach() throws Exception {
		dropDatabase();
	}
	
	@Test
	public void testCreateQueryWrapper() {
		assertNotNull(queryWrapper);
		assertTrue(queryWrapper instanceof QueryWrapperFactory.QueryWrapper<?>);
	}
	
	@Test
	public void testList() throws Exception {
		List<?> result = queryWrapper.list();
		assertTrue(result.isEmpty());
		statement.execute("INSERT INTO FOO VALUES(1, 'bars')");
		result = queryWrapper.list();
		assertEquals(1, result.size());
		Object obj = result.get(0);
		assertTrue(obj instanceof Foo);
		Foo foo = (Foo)obj;
		assertEquals(1, foo.id);
		assertEquals("bars", foo.bars);
	}
	
	@Test
	public void testSetMaxResults() {
		queryWrapper.setMaxResults(1);
		assertEquals(1, wrappedQuery.getMaxResults());
		queryWrapper.setMaxResults(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, wrappedQuery.getMaxResults());
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
		sessionFactory = new SessionFactoryWrapper(configuration.buildSessionFactory());
	}
	
	private void dropDatabase() throws Exception {
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}
		
}
