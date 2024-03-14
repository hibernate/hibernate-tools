package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.orm.jbt.internal.factory.DdlExporterWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DdlExporterWrapperTest {

	private DdlExporterWrapper ddlExporterWrapper = null; 
	private DdlExporter wrappedDdlExporter = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedDdlExporter = new DdlExporter();
		ddlExporterWrapper = DdlExporterWrapperFactory.createDdlExporterWrapper(wrappedDdlExporter);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(ddlExporterWrapper);
		assertNotNull(wrappedDdlExporter);
	}
	
	@Test
	public void testSetExport() {
		assertNull(wrappedDdlExporter.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
		ddlExporterWrapper.setExport(false);
		assertFalse((Boolean)wrappedDdlExporter.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
		ddlExporterWrapper.setExport(true);
		assertTrue((Boolean)wrappedDdlExporter.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
	}

}
