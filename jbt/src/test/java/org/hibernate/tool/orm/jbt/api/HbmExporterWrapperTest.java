package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.orm.jbt.internal.factory.HbmExporterWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HbmExporterWrapperTest {

	private HbmExporterWrapper hbmExporterWrapper = null; 
	private HbmExporter wrappedHbmExporter = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedHbmExporter = new HbmExporter();
		hbmExporterWrapper = HbmExporterWrapperFactory.createHbmExporterWrapper(wrappedHbmExporter);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedHbmExporter);
		assertNotNull(hbmExporterWrapper);
	}
	
}
