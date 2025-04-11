/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2023-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.internal.factory.SessionFactoryWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.internal.util.MockDialect;
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
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.api.wrp'>" +
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
	
	@Test
	public void testGetAllClassMetadata() throws Exception {
		Map<String, ClassMetadataWrapper> allClassMetadata = sessionFactoryWrapper.getAllClassMetadata();
		assertNotNull(allClassMetadata);
		assertEquals(1, allClassMetadata.size());
		assertNotNull(allClassMetadata.get(Foo.class.getName()));
	}
	
	@Test
	public void testGetAllCollectionMetadata() throws Exception {
		Map<String, CollectionMetadataWrapper> allCollectionMetadata = sessionFactoryWrapper.getAllCollectionMetadata();
		assertNotNull(allCollectionMetadata);
		assertEquals(1, allCollectionMetadata.size());
		CollectionMetadataWrapper barsPersister = allCollectionMetadata.get(Foo.class.getName() + ".bars");
		assertNotNull(barsPersister);
	}
	
	@Test
	public void testOpenSession() {
		SessionWrapper session = sessionFactoryWrapper.openSession();
		assertNotNull(session);
		assertSame(((Session)session.getWrappedObject()).getSessionFactory(), wrappedSessionFactory);
	}
	
	@Test
	public void testGetClassMetadata() throws Exception {
		// first 'getClassMetadata(String)'
		assertNull(sessionFactoryWrapper.getClassMetadata("foo"));
		assertNotNull(sessionFactoryWrapper.getClassMetadata(Foo.class.getName()));
		// then "getClassMetadata(Class)'
		assertNull(sessionFactoryWrapper.getClassMetadata(Object.class));
		assertNotNull(sessionFactoryWrapper.getClassMetadata(Foo.class));
	}
	
	@Test
	public void testGetCollectionMetadata() throws Exception {
		assertNull(sessionFactoryWrapper.getCollectionMetadata("bars"));
		assertNotNull(sessionFactoryWrapper.getCollectionMetadata(Foo.class.getName() + ".bars"));
	}	
}
