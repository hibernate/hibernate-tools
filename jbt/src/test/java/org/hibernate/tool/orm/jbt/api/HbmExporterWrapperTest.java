package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.orm.jbt.internal.factory.HbmExporterWrapperFactory;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HbmExporterWrapperTest {

	private HbmExporterWrapper hbmExporterWrapper = null; 
	private HbmExporter wrappedHbmExporter = null;

	private Configuration cfg = null;
	private File f = null;
	
	private boolean delegateHasExported = false;

	@TempDir private File tempFolder;
	
	@BeforeEach
	public void beforeEach() {
		cfg = new Configuration();
		f = new File(tempFolder, "foo");
		hbmExporterWrapper = HbmExporterWrapperFactory.createHbmExporterWrapper(cfg, f);
		wrappedHbmExporter = (HbmExporter)hbmExporterWrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() throws Exception {
		assertTrue(tempFolder.exists());
		assertFalse(f.exists());
		assertNotNull(wrappedHbmExporter);
		assertNotNull(hbmExporterWrapper);
		assertSame(f, wrappedHbmExporter.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
		ConfigurationMetadataDescriptor descriptor = (ConfigurationMetadataDescriptor)wrappedHbmExporter
				.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(descriptor);
		Field configurationField = ConfigurationMetadataDescriptor.class.getDeclaredField("configuration");
		configurationField.setAccessible(true);
		assertSame(cfg, configurationField.get(descriptor));
	}
	
	@Test
	public void testStart() throws Exception {
		MetadataDescriptor descriptor = new TestMetadataDescriptor();
		Properties properties = wrappedHbmExporter.getProperties();
		properties.put(ExporterConstants.METADATA_DESCRIPTOR, descriptor);
		properties.put(ExporterConstants.DESTINATION_FOLDER, tempFolder);
		final File fooHbmXml = new File(tempFolder, "Foo.hbm.xml");
		// First without a 'delegate' exporter
		assertFalse(fooHbmXml.exists());
		hbmExporterWrapper.start();
		assertTrue(fooHbmXml.exists());
		assertTrue(fooHbmXml.delete());
		// Now set a 'delegate' and invoke 'start' again
		Object delegate = new Object() {			
			public void exportPojo(Map<Object, Object> map, Object pojoClass, String qualifiedDeclarationName) {
				try {
					FileWriter fw = new FileWriter(fooHbmXml);
					fw.write("<someDummyXml/>");
					fw.close();
					delegateHasExported = true;
				} catch (Throwable t) {
					fail(t);
				}
			}
		};
		Field delegateField = wrappedHbmExporter.getClass().getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		delegateField.set(wrappedHbmExporter, delegate);
		assertFalse(delegateHasExported);
		hbmExporterWrapper.start();
		assertTrue(delegateHasExported);
	}
	
	private static class TestMetadataDescriptor implements MetadataDescriptor {
		@Override
		public Metadata createMetadata() {
			return (Metadata)Proxy.newProxyInstance(
					getClass().getClassLoader(), 
					new Class<?>[] { Metadata.class }, 
					new TestInvocationHandler());
		}
		@Override
		public Properties getProperties() {
			Properties properties = new Properties();
			properties.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
			return properties;
		}	
	}
	
	private static class TestInvocationHandler implements InvocationHandler {
		private ArrayList<PersistentClass> entities = new ArrayList<PersistentClass>();
		private ArrayList<Table> tables = new ArrayList<Table>();
		private TestInvocationHandler() {
			RootClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
			Table table = new Table("JBoss Tools", "FOO");
			Column keyColumn = new Column("BAR");
			SimpleValue key = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
			key.setTable(table);
			key.setTypeName("String");
			key.addColumn(keyColumn);
			persistentClass.setClassName("Foo");
			persistentClass.setEntityName("Foo");
			persistentClass.setJpaEntityName("Foo");
			persistentClass.setTable(table);
			persistentClass.setIdentifier(key);	
			entities.add(persistentClass);
			tables.add(table);
		}
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getEntityBindings")) {
				return entities;
			} else if (method.getName().equals("collectTableMappings")) {
				return tables;
			} else if (method.getName().equals("getImports")) {
				return Collections.emptyMap();
			}
			return null;
		}		
	}
		
}
