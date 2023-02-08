package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.persistence.Query;

public class SessionWrapperFactoryTest {
	
	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.wrp'>" +
			"  <class name='SessionWrapperFactoryTest$Foo'>" + 
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
	
	private SessionFactoryImplementor sessionFactory, sessionFactoryWrapper = null;
	private SessionImplementor session, sessionWrapper = null;
	
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
		sessionFactory = (SessionFactoryImplementor)configuration.buildSessionFactory();
	    sessionFactoryWrapper = new SessionFactoryWrapper(sessionFactory);
		session = sessionFactory.openSession();
		sessionWrapper = SessionWrapperFactory.createSessionWrapper(sessionFactoryWrapper, session);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(sessionFactory);
		assertNotNull(sessionFactoryWrapper);
		assertNotNull(session);
		assertNotNull(sessionWrapper);
	}
	
	@Test
	public void testGetSessionFactory() {
		assertSame(sessionFactory, session.getSessionFactory());
		assertSame(sessionFactoryWrapper, sessionWrapper.getSessionFactory());
		assertNotSame(sessionFactory, sessionWrapper.getSessionFactory());
		assertNotSame(sessionFactoryWrapper, session.getSessionFactory());
	}
	
	@Test
	public void testCreateQuery() {
		assertNotNull(sessionWrapper.createQuery("from " + Foo.class.getName()));
	}
	
	@Test
	public void testIsOpen() {
		assertTrue(sessionWrapper.isOpen());
		session.close();
		assertFalse(sessionWrapper.isOpen());
	}
	
	@Test
	public void testClose() {
		assertTrue(session.isOpen());
		sessionWrapper.close();
		assertFalse(session.isOpen());
	}
	
	@Test
	public void testContains() {
		Foo first = new Foo();
		first.id = "1";
		session.persist(first);
		Foo second = new Foo();
		assertTrue(sessionWrapper.contains(first));
		assertFalse(sessionWrapper.contains(second));
		assertFalse(sessionWrapper.contains("blah"));
	}
	
	@Test
	public void testCreateCriteria() {
		Query query = ((SessionWrapperFactory.SessionImplementorExtension)sessionWrapper)
				.createCriteria(Foo.class);
		assertTrue(Proxy.isProxyClass(query.getClass()));
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(query);
		assertSame(CriteriaWrapperFactory.CriteriaInvocationHandler.class, invocationHandler.getClass());
	}

}
