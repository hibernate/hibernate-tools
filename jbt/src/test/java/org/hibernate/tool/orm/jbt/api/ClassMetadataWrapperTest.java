package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.internal.factory.ClassMetadataWrapperFactory;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.hibernate.type.internal.NamedBasicTypeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ClassMetadataWrapperTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.api'>" +
			"  <class name='ClassMetadataWrapperTest$Foo'>" + 
			"    <id name='id' access='field' />" +
			"    <set name='bars' access='field' >" +
			"      <key column='barId' />" +
			"      <element column='barVal' type='string' />" +
			"    </set>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	public static class Foo {
		public String id;
		public Set<String> bars = new HashSet<String>();
	}
	
	@TempDir
	public File tempDir;
	
	private ClassMetadataWrapper classMetadataWrapper = null;
	private SessionFactory sessionFactory = null;
		
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
		sessionFactory = configuration.buildSessionFactory();
		EntityPersister entityPersister = ((SessionFactoryImplementor)sessionFactory)
				.getMappingMetamodel().getEntityDescriptor(Foo.class.getName());
		classMetadataWrapper = ClassMetadataWrapperFactory.createClassMetadataWrapper(entityPersister);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(classMetadataWrapper);
	}
	
	@Test
	public void testGetEntityName() {
		assertEquals(Foo.class.getName(), classMetadataWrapper.getEntityName());
	}
	
	@Test
	public void testGetIdentifierPropertyName() {
		assertEquals("id", classMetadataWrapper.getIdentifierPropertyName());
	}
	
	@Test
	public void testGetPropertyNames() {
		String[] propertyNames = classMetadataWrapper.getPropertyNames();
 		assertEquals(1, propertyNames.length);
 		assertEquals("bars", propertyNames[0]);
	}
	
	@Test
	public void testGetPropertyTypes() {
		Type[] types = classMetadataWrapper.getPropertyTypes();
		assertEquals(1, types.length);
		Type type = types[0];
		assertTrue(type.isCollectionType());
		assertEquals(
				"org.hibernate.tool.orm.jbt.api.ClassMetadataWrapperTest$Foo.bars",
				((CollectionType)type).getRole());
 	}
	
	@Test
	public void testGetMappedClass() {
		assertSame(Foo.class, classMetadataWrapper.getMappedClass());
	}
	
	@Test
	public void testGetIdentifierType() {
		Type identifierType = classMetadataWrapper.getIdentifierType();
		assertNotNull(identifierType);
		assertTrue(identifierType instanceof NamedBasicTypeImpl);
		assertSame("string", ((NamedBasicTypeImpl<?>)identifierType).getName());
	}
	
	@Test
	public void testGetPropertyValue() {
		Foo foo = new Foo();
		Set<String> foobarSet = new HashSet<String>(Arrays.asList("foo", "bar"));
		foo.bars = foobarSet;
		assertSame(foobarSet, classMetadataWrapper.getPropertyValue(foo, "bars"));
	}
	
	@Test
	public void testHasIdentifierProperty() {
		assertTrue(classMetadataWrapper.hasIdentifierProperty());
	}
	
	@Test 
	public void testGetIdentifier() {
		Session session = sessionFactory.openSession();
		Foo foo = new Foo();
		foo.id = "bar";
		Object identifier = classMetadataWrapper.getIdentifier(foo, session);
		assertSame("bar", identifier);
	}
	
	@Test
	public void testIsInstanceOfAbstractEntityPersister() {
		assertTrue(classMetadataWrapper.isInstanceOfAbstractEntityPersister());
	}
	
	@Test
	public void testGetPropertyIndexOrNull() {
		assertSame(0, classMetadataWrapper.getPropertyIndexOrNull("bars"));
		assertNull(classMetadataWrapper.getPropertyIndexOrNull("foo"));
	}
	
}
