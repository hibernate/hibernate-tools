package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.orm.jbt.wrp.EntityPersisterWrapperFactory.EntityPersisterExtension;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeExtension;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.hibernate.type.internal.NamedBasicTypeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class EntityPersisterWrapperFactoryTest {
	
	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.wrp'>" +
			"  <class name='EntityPersisterWrapperFactoryTest$Foo'>" + 
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
	
	private EntityPersister entityPersisterWrapper = null;
	private EntityPersister wrappedEntityPersister = null;
	
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
	    wrappedEntityPersister = ((SessionFactoryImplementor)sessionFactory)
				.getMetamodel()
				.entityPersister(Foo.class.getName());
	    entityPersisterWrapper = (EntityPersister)EntityPersisterWrapperFactory.create(wrappedEntityPersister);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(entityPersisterWrapper);
		assertNotNull(wrappedEntityPersister);
	}
	
	@Test
	public void testGetEntityName() {
		assertEquals(Foo.class.getName(), entityPersisterWrapper.getEntityName());
	}
	
	@Test
	public void testGetIdentifierPropertyName() {
		assertEquals("id", entityPersisterWrapper.getIdentifierPropertyName());
	}
	
	@Test
	public void testGetPropertyNames() {
		String[] propertyNames = entityPersisterWrapper.getPropertyNames();
 		assertEquals(1, propertyNames.length);
 		assertEquals("bars", propertyNames[0]);
	}
	
	@Test
	public void testGetPropertyTypes() {
		Type[] propertyTypeWrappers = entityPersisterWrapper.getPropertyTypes();
		assertEquals(1, propertyTypeWrappers.length);
		Type propertyTypeWrapper = propertyTypeWrappers[0];
		assertTrue(propertyTypeWrapper instanceof Wrapper);
		assertTrue(propertyTypeWrapper instanceof TypeExtension);
		Object wrappedPropertyType = ((Wrapper)propertyTypeWrapper).getWrappedObject();
		assertTrue(wrappedPropertyType instanceof CollectionType);
		assertTrue(propertyTypeWrapper.isCollectionType());
		assertEquals(
				"org.hibernate.tool.orm.jbt.wrp.EntityPersisterWrapperFactoryTest$Foo.bars",
				((TypeExtension)propertyTypeWrapper).getRole());
 	}
	
	@Test
	public void testGetMappedClass() {
		assertSame(Foo.class, entityPersisterWrapper.getMappedClass());
	}
	
	@Test
	public void testGetIdentifierType() {
		Type typeWrapper = entityPersisterWrapper.getIdentifierType();
		assertNotNull(typeWrapper);
		assertTrue(typeWrapper instanceof Wrapper);
		Object wrappedType = ((Wrapper)typeWrapper).getWrappedObject();
		assertNotNull(wrappedType);
		assertTrue(wrappedType instanceof NamedBasicTypeImpl);
		assertSame("string", ((NamedBasicTypeImpl<?>)wrappedType).getName());
	}
	
	@Test
	public void testGetPropertyValue() {
		Foo foo = new Foo();
		Set<String> foobarSet = new HashSet<String>(Arrays.asList("foo", "bar"));
		foo.bars = foobarSet;
		assertSame(foobarSet, entityPersisterWrapper.getPropertyValue(foo, "bars"));
	}
	
	@Test
	public void testHasIdentifierProperty() {
		assertTrue(entityPersisterWrapper.hasIdentifierProperty());
	}
	
	@Test 
	public void testGetIdentifier() {
		SharedSessionContractImplementor sessionFacade = (SharedSessionContractImplementor)sessionFactory.openSession();
		Foo foo = new Foo();
		foo.id = "bar";
		Object identifier = entityPersisterWrapper.getIdentifier(foo, sessionFacade);
		assertSame("bar", identifier);
	}
	
	@Test
	public void testIsInstanceOfAbstractEntityPersister() throws Exception {
		Object dummyEntityPersister = EntityPersisterWrapperFactory.create(createTestEntityPersister());
		Method isInstanceOfAbstractEntityPersisterMethod =
				EntityPersisterExtension.class
				.getDeclaredMethod("isInstanceOfAbstractEntityPersister");
		assertTrue((Boolean)isInstanceOfAbstractEntityPersisterMethod.invoke(dummyEntityPersister, (Object[])null));
	}
	
	@Test
	public void testGetTuplizerPropertyValue() throws Exception {
		Object dummyEntityPersister = EntityPersisterWrapperFactory.create(createTestEntityPersister());
		Method getTuplizerPropertyValueMethod =
				EntityPersisterExtension.class
				.getDeclaredMethod("getTuplizerPropertyValue", new Class[] { Object.class, int.class });
		assertSame(PROPERTY_VALUE, getTuplizerPropertyValueMethod.invoke(dummyEntityPersister, new Object(), 0));
	}
	
	@Test
	public void testGetPropertyIndexOrNull() throws Exception {
		Object dummyEntityPersister = EntityPersisterWrapperFactory.create(createTestEntityPersister());
		Method getPropertyIndexOrNullMethod =
				EntityPersisterExtension.class
				.getDeclaredMethod("getPropertyIndexOrNull", new Class[] { String.class });
		assertEquals(0, getPropertyIndexOrNullMethod.invoke(dummyEntityPersister, "bars"));
		assertNull(getPropertyIndexOrNullMethod.invoke(dummyEntityPersister, "foo"));
	}
	
	private EntityPersister createTestEntityPersister() {
		return (EntityPersister)Proxy.newProxyInstance(
				EntityPersisterWrapperFactoryTest.class.getClassLoader(), 
				new Class[] { EntityPersister.class }, 
				new TestInvocationHandler());
	}
	
	private EntityMetamodel createTestEntityMetamodel() throws Exception {
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
		SessionFactoryImplementor sfi = (SessionFactoryImplementor)configuration.buildSessionFactory();
		return sfi
				.getMetamodel()
				.entityPersister(Foo.class)
				.getEntityMetamodel();		
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("getValue".equals(method.getName())) {
				return PROPERTY_VALUE;
			} if ("getEntityMetamodel".equals(method.getName())) {
				return createTestEntityMetamodel();
			}
			return null;
		}		
	}
	
	private static final Object PROPERTY_VALUE = new Object();

}
