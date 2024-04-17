package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.internal.factory.SessionFactoryWrapperFactory;
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
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.api'>" +
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
	
	private SessionFactory wrappedSessionFactory = null;
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
		wrappedSessionFactory = configuration.buildSessionFactory();
		sessionFactoryWrapper = SessionFactoryWrapperFactory.createSessionFactoryWrapper(wrappedSessionFactory);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedSessionFactory);
		assertNotNull(sessionFactoryWrapper);
	}
	
	@Test
	public void testClose() {
		assertFalse(wrappedSessionFactory.isClosed());
		sessionFactoryWrapper.close();
		assertTrue(wrappedSessionFactory.isClosed());
	}
	
}
