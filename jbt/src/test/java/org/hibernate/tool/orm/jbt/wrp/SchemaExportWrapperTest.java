package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaExportWrapperTest {
	
	private SchemaExportWrapper schemaExportWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		schemaExportWrapper = new SchemaExportWrapper();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(schemaExportWrapper);
	}

}
