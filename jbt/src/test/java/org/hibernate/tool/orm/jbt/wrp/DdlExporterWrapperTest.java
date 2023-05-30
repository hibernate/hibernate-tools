package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.api.export.ExporterConstants;
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
	
	@Test
	public void testSetExport() {
		assertNull(ddlExporterWrapper.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
		ddlExporterWrapper.setExport(false);
		assertFalse((Boolean)ddlExporterWrapper.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
		ddlExporterWrapper.setExport(true);
		assertTrue((Boolean)ddlExporterWrapper.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
	}

}
