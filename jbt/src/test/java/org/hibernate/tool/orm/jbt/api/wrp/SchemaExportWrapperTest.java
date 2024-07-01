package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.List;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.internal.factory.ConfigurationWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.SchemaExportWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.internal.util.MockDialect;
import org.hibernate.tool.schema.TargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaExportWrapperTest {
	
	private SchemaExport wrappedSchemaExport = null;
	private SchemaExportWrapper schemaExportWrapper = null;
	private ConfigurationWrapper configurationWrapper = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
	    configurationWrapper = ConfigurationWrapperFactory.createNativeConfigurationWrapper();
		configurationWrapper.setProperty(Environment.DIALECT, MockDialect.class.getName());
		configurationWrapper.setProperty(Environment.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		wrappedSchemaExport = new TestSchemaExport();
		schemaExportWrapper = SchemaExportWrapperFactory.createSchemaExportWrapper(configurationWrapper);
		Field field = schemaExportWrapper.getClass().getDeclaredField("schemaExport");
		field.setAccessible(true);
		field.set(schemaExportWrapper, wrappedSchemaExport);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(configurationWrapper);
		assertNotNull(wrappedSchemaExport);
		assertNotNull(schemaExportWrapper);
	}

	@Test
	public void testCreate() {
		assertFalse(((TestSchemaExport)wrappedSchemaExport).created);
		schemaExportWrapper.create();
		assertTrue(((TestSchemaExport)wrappedSchemaExport).created);
	}
	
	@Test
	public void testGetExceptions() throws Exception {
		Field exceptionsField = SchemaExport.class.getDeclaredField("exceptions");
		exceptionsField.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<Throwable> exceptionList = (List<Throwable>)exceptionsField.get(wrappedSchemaExport);
		assertTrue(exceptionList.isEmpty());
		Throwable t = new RuntimeException("foobar");
		exceptionList.add(t);
		List<Throwable> list = schemaExportWrapper.getExceptions();
		assertSame(list, exceptionList);
		assertTrue(list.contains(t));
	}
	
	private class TestSchemaExport extends SchemaExport {
		
		private boolean created = false;

		public TestSchemaExport() {
			super();
		}
		
		@Override 
		public void create(EnumSet<TargetType> targetTypes, Metadata metadata) {
			created = true;
			super.create(targetTypes, metadata);
		}

		
	}
}
