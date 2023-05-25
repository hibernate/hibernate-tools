package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericExporterWrapperTest {
	
	private GenericExporterWrapper genericExporterWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		genericExporterWrapper = new GenericExporterWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(genericExporterWrapper);
		assertTrue(genericExporterWrapper instanceof GenericExporterWrapper);
	}
	
}
