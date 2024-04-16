package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.internal.factory.SchemaExportWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaExportWrapperTest {
	
	private SchemaExport wrappedSchemaExport = null;
	private SchemaExportWrapper schemaExportWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedSchemaExport = new SchemaExport();
		schemaExportWrapper = SchemaExportWrapperFactory.createSchemaExportWrapper(wrappedSchemaExport);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedSchemaExport);
		assertNotNull(schemaExportWrapper);
	}

}
