package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.orm.jbt.internal.factory.GenericExporterWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericExporterWrapperTest {

	private GenericExporterWrapper genericExporterWrapper = null; 
	private GenericExporter wrappedGenericExporter = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedGenericExporter = new GenericExporter();
		genericExporterWrapper = GenericExporterWrapperFactory.createGenericExporterWrapper(wrappedGenericExporter);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedGenericExporter);
		assertNotNull(genericExporterWrapper);
	}
	
}
