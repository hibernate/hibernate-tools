package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
	
}
