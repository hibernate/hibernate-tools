package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.internal.export.common.GenericExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericExporterWrapperFactoryTest {

	private GenericExporter wrappedGenericExporter = null;
	private GenericExporterWrapperFactory.GenericExporterWrapper genericExporterWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedGenericExporter = new GenericExporter();
		genericExporterWrapper = GenericExporterWrapperFactory.create(wrappedGenericExporter);
	}
	
	@Test
	public void testCreate() {
		assertNotNull(genericExporterWrapper);
		assertSame(wrappedGenericExporter, genericExporterWrapper.getWrappedObject());
	}

}
