package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;

import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaExportWrapperTest {
	
	private SchemaExportWrapper schemaExportWrapper = null;
	private Configuration configuration = null;
	
	@BeforeEach
	public void beforeEach() {
		configuration = new Configuration();
		schemaExportWrapper = new SchemaExportWrapper(configuration);
	}
	
	@Test
	public void testConstruction() throws Exception {
		assertNotNull(schemaExportWrapper);
		Field configurationField = SchemaExportWrapper.class.getDeclaredField("configuration");
		configurationField.setAccessible(true);
		assertSame(configuration, configurationField.get(schemaExportWrapper));
	}
	
}
