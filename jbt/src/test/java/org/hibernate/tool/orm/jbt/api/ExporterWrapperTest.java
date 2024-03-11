package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.StringWriter;
import java.util.Properties;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.orm.jbt.internal.factory.ExporterWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExporterWrapperTest {

	private ExporterWrapper exporterWrapper = null;
	private Exporter wrappedExporter = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedExporter = new TestExporter();
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(wrappedExporter);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(exporterWrapper);
		assertNotNull(wrappedExporter);
	}

	public static class TestExporter extends AbstractExporter {
		private boolean started = false;
		private Properties props = null;
		private StringWriter output = null;
		@Override protected void doStart() {}
		@Override public void start() { started = true; }
		public void setCustomProperties(Properties p) {
			props = p;
		}
		
	}
	
}
