package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.orm.jbt.wrp.ExporterWrapperFactory.ExporterWrapper;
import org.junit.jupiter.api.Test;

public class ExporterWrapperFactoryTest {
	
	@Test
	public void testCreate() {
		ExporterWrapper exporterWrapper = ExporterWrapperFactory.create(DdlExporter.class.getName());
		assertNotNull(exporterWrapper);
	}

}
