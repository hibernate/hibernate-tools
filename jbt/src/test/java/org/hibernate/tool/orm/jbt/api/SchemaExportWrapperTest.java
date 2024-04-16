package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.internal.factory.SchemaExportWrapperFactory;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.schema.TargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaExportWrapperTest {
	
	private SchemaExport wrappedSchemaExport = null;
	private SchemaExportWrapper schemaExportWrapper = null;
	private Configuration configuration = null;
	
	@BeforeEach
	public void beforeEach() {
	    configuration = new Configuration();
		configuration.setProperty(Environment.DIALECT, MockDialect.class.getName());
		configuration.setProperty(Environment.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		wrappedSchemaExport = new TestSchemaExport();
		schemaExportWrapper = SchemaExportWrapperFactory.createSchemaExportWrapper(
				wrappedSchemaExport, configuration);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(configuration);
		assertNotNull(wrappedSchemaExport);
		assertNotNull(schemaExportWrapper);
		assertSame(configuration, schemaExportWrapper.getConfiguration());
	}

	@Test
	public void testCreate() {
		assertFalse(((TestSchemaExport)wrappedSchemaExport).created);
		schemaExportWrapper.create();
		assertTrue(((TestSchemaExport)wrappedSchemaExport).created);
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
