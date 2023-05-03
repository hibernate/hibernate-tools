package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.EnumSet;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.schema.TargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaExportWrapperTest {
	
	private SchemaExportWrapper schemaExportWrapper = null;
	private Configuration configuration = null;
	
	@BeforeEach
	public void beforeEach() {
		configuration = new Configuration();
		configuration.setProperty(Environment.DIALECT, MockDialect.class.getName());
		configuration.setProperty(Environment.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		schemaExportWrapper = new TestSchemaExportWrapper(configuration);
	}
	
	@Test
	public void testConstruction() throws Exception {
		assertNotNull(schemaExportWrapper);
		Field configurationField = SchemaExportWrapper.class.getDeclaredField("configuration");
		configurationField.setAccessible(true);
		assertSame(configuration, configurationField.get(schemaExportWrapper));
	}
	
	@Test
	public void testCreate() {
		assertFalse(((TestSchemaExportWrapper)schemaExportWrapper).created);
		schemaExportWrapper.create();
		assertTrue(((TestSchemaExportWrapper)schemaExportWrapper).created);
	}
	
	private class TestSchemaExportWrapper extends SchemaExportWrapper {
		
		private boolean created = false;

		public TestSchemaExportWrapper(Configuration configuration) {
			super(configuration);
		}
		
		@Override
		public void create(EnumSet<TargetType> targetTypes, Metadata metadata) {
			created = true;
		}

		
	}
	
}
