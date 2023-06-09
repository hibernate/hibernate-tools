package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SessionFactoryWrapperTest {
	
	
	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.wrp'>" +
			"  <class name='SessionFactoryWrapperTest$Foo'>" + 
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
	
	private SessionFactoryWrapper sessionFactoryWrapper = null;
	
	@BeforeEach 
	public void beforeEach() throws Exception {
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
		sessionFactoryWrapper = new SessionFactoryWrapper(
				(SessionFactoryImplementor)configuration.buildSessionFactory());
	}
	
	@Test
	public void testOpenSession() {
		Session session = sessionFactoryWrapper.openSession();
		assertTrue(session instanceof Proxy);
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(session);
		assertEquals("org.hibernate.tool.orm.jbt.wrp.SessionWrapperFactory$SessionWrapperInvocationHandler", invocationHandler.getClass().getName());
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(sessionFactoryWrapper);
	}
	
	@Test
	public void testGetAllClassMetadata() throws Exception {
		Map<String, EntityPersister> allClassMetadata = sessionFactoryWrapper.getAllClassMetadata();
		assertNotNull(allClassMetadata);
		assertEquals(1, allClassMetadata.size());
		EntityPersister fooPersister = allClassMetadata.get(Foo.class.getName());
		assertNotNull(fooPersister);
		assertTrue(fooPersister instanceof EntityPersisterWrapperFactory.EntityPersisterExtension);
	}
	
	@Test
	public void testGetAllCollectionMetadata() throws Exception {
		Map<String, CollectionPersister> allCollectionMetadata = sessionFactoryWrapper.getAllCollectionMetadata();
		assertEquals(1, allCollectionMetadata.size());
		assertNotNull(allCollectionMetadata.get(Foo.class.getName() + ".bars"));
	}
	

	@Test
	public void testGetClassMetadata() throws Exception {
		assertNull(sessionFactoryWrapper.getClassMetadata("foo"));
		EntityPersister fooPersister = sessionFactoryWrapper.getClassMetadata(Foo.class.getName());
		assertNotNull(fooPersister);
		assertTrue(fooPersister instanceof EntityPersisterWrapperFactory.EntityPersisterExtension);
		assertNull(sessionFactoryWrapper.getClassMetadata(Object.class));
		fooPersister = sessionFactoryWrapper.getClassMetadata(Foo.class);
		assertNotNull(fooPersister);
		assertTrue(fooPersister instanceof EntityPersisterWrapperFactory.EntityPersisterExtension);
	}
	
	@Test
	public void testGetCollectionMetadata() throws Exception {
		assertNull(sessionFactoryWrapper.getCollectionMetadata("bars"));
		assertNotNull(sessionFactoryWrapper.getCollectionMetadata(Foo.class.getName() + ".bars"));
	}	
	
}
