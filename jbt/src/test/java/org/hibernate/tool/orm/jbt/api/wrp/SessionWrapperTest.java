package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.internal.factory.SessionWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SessionWrapperTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.api.wrp'>" +
			"  <class name='SessionWrapperTest$Foo'>" + 
			"    <id name='id' access='field' />" +
			"    <set name='bars' access='field' >" +
			"      <key column='barId' />" +
			"      <element column='barVal' type='string' />" +
			"    </set>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String id;
		public Set<String> bars = new HashSet<String>();
	}
	
	@TempDir
	public File tempDir;
	
	private Session wrappedSession = null;
	private SessionWrapper  sessionWrapper = null;

	@BeforeEach
	public void before() throws Exception {
		tempDir = Files.createTempDirectory("temp").toFile();
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
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		wrappedSession = sessionFactory.openSession();
		sessionWrapper = SessionWrapperFactory.createSessionWrapper(wrappedSession);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedSession);
		assertNotNull(sessionWrapper);
	}
	
	@Test
	public void testGetEntityName() {
		Foo foo = new Foo();
		foo.id = "bar";
		wrappedSession.persist(foo);
		assertEquals(Foo.class.getName(), sessionWrapper.getEntityName(foo));
	}
	
	@Test
	public void testGetSessionFactory() {
		SessionFactory sessionFactory = wrappedSession.getSessionFactory();
		assertSame(sessionFactory, sessionWrapper.getSessionFactory().getWrappedObject());
	}
	
	@Test
	public void testCreateQuery() {
		assertNotNull(sessionWrapper.createQuery("from " + Foo.class.getName()));
	}
	
	@Test
	public void testIsOpen() {
		assertTrue(sessionWrapper.isOpen());
		wrappedSession.close();
		assertFalse(sessionWrapper.isOpen());
	}
	
	@Test
	public void testClose() {
		assertTrue(wrappedSession.isOpen());
		sessionWrapper.close();
		assertFalse(wrappedSession.isOpen());
	}
	
	@Test
	public void testContains() {
		Foo first = new Foo();
		first.id = "1";
		wrappedSession.persist(first);
		Foo second = new Foo();
		assertTrue(sessionWrapper.contains(first));
		assertFalse(sessionWrapper.contains(second));
		assertFalse(sessionWrapper.contains("blah"));
	}
	
	@Test
	public void testCreateCriteria() {
		QueryWrapper queryWrapper = sessionWrapper.createCriteria(Foo.class);
		assertNotNull(queryWrapper);
		Query<?> q = (Query)queryWrapper.getWrappedObject();
		assertSame(q.getSession(), sessionWrapper.getWrappedObject());
	}

}
