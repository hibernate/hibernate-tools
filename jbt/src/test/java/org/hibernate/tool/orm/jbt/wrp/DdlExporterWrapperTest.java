package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DdlExporterWrapperTest {
	
	private DdlExporterWrapper ddlExporterWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		ddlExporterWrapper = new DdlExporterWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(ddlExporterWrapper);
		assertTrue(ddlExporterWrapper instanceof DdlExporterWrapper);
	}
	
}
