package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.orm.jbt.internal.factory.ArtifactCollectorWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ExporterWrapperFactory;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.DummyMetadataDescriptor;
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

	@Test
	public void testSetConfiguration() throws Exception {
		Object metadataDescriptor = null;
		Properties properties = new Properties();
		Configuration configuration = new Configuration();
		configuration.setProperties(properties);
		Field field = ConfigurationMetadataDescriptor.class.getDeclaredField("configuration");
		field.setAccessible(true);
		// First use the TestExporter 
		metadataDescriptor = wrappedExporter.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		assertNotSame(configuration, field.get(metadataDescriptor));
		exporterWrapper.setConfiguration(configuration);	
		metadataDescriptor = wrappedExporter.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		assertSame(configuration, field.get(metadataDescriptor));
		// Now test with a CfgExporter
		wrappedExporter = new CfgExporter();
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(wrappedExporter);
		assertNotSame(properties, ((CfgExporter)exporterWrapper.getWrappedObject()).getCustomProperties());
		metadataDescriptor = wrappedExporter.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof DummyMetadataDescriptor);
		exporterWrapper.setConfiguration(configuration);	
		assertSame(properties, ((CfgExporter)exporterWrapper.getWrappedObject()).getCustomProperties());
		metadataDescriptor = wrappedExporter.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		assertSame(configuration, field.get(metadataDescriptor));
	}
	
	@Test
	public void testSetArtifactCollector() {
		ArtifactCollectorWrapper artifactCollectorWrapper = ArtifactCollectorWrapperFactory.createArtifactCollectorWrapper();
		Object wrappedArtifactCollector = artifactCollectorWrapper.getWrappedObject();
		assertNotSame(wrappedArtifactCollector, wrappedExporter.getProperties().get(ExporterConstants.ARTIFACT_COLLECTOR));
		exporterWrapper.setArtifactCollector(artifactCollectorWrapper);
		assertSame(wrappedArtifactCollector, wrappedExporter.getProperties().get(ExporterConstants.ARTIFACT_COLLECTOR));
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
