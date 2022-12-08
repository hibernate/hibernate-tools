package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.orm.jbt.wrp.EntityPersisterWrapperFactory.EntityPersisterExtension;
import org.hibernate.tuple.entity.EntityMetamodel;
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
