package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.wrp.ExporterWrapperFactory.ExporterWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExporterWrapperFactoryTest {
	
	private ExporterWrapper exporterWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		exporterWrapper = ExporterWrapperFactory.create(TestExporter.class.getName());
	}
	
	@Test
	public void testCreate() {
		assertNotNull(exporterWrapper);
		Object wrappedExporter = exporterWrapper.getWrappedObject();
		assertTrue(wrappedExporter instanceof TestExporter);
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
		assertNull(exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.METADATA_DESCRIPTOR));
		exporterWrapper.setConfiguration(configuration);	
		metadataDescriptor = exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		assertSame(configuration, field.get(metadataDescriptor));
		// Now test with a CfgExporter
		exporterWrapper = ExporterWrapperFactory.create(CfgExporter.class.getName());
		assertNotSame(properties, ((CfgExporter)exporterWrapper.getWrappedObject()).getCustomProperties());
		assertNull(exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.METADATA_DESCRIPTOR));
		exporterWrapper.setConfiguration(configuration);	
		assertSame(properties, ((CfgExporter)exporterWrapper.getWrappedObject()).getCustomProperties());
		metadataDescriptor = exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		assertSame(configuration, field.get(metadataDescriptor));
	}
	
	@Test
	public void testSetArtifactCollector() {
		ArtifactCollector artifactCollector = new DefaultArtifactCollector();
		assertNotSame(artifactCollector, exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.ARTIFACT_COLLECTOR));
		exporterWrapper.setArtifactCollector(artifactCollector);
		assertSame(artifactCollector, exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.ARTIFACT_COLLECTOR));
	}
	
	@Test
	public void testSetOutputDirectory() {
		File file = new File("");
		assertNotSame(file, exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.DESTINATION_FOLDER));		
		exporterWrapper.setOutputDirectory(file);
		assertSame(file, exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.DESTINATION_FOLDER));		
	}
	
	@Test
	public void testSetTemplatePath() {
		String[] templatePath = new String[] {};
		assertNotSame(templatePath, exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.TEMPLATE_PATH));		
		exporterWrapper.setTemplatePath(templatePath);
		assertSame(templatePath, exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.TEMPLATE_PATH));		
	}
	
	@Test
	public void testStart() throws Exception {
		assertFalse(((TestExporter)exporterWrapper.getWrappedObject()).started);
		exporterWrapper.start();
		assertTrue(((TestExporter)exporterWrapper.getWrappedObject()).started);
	}
	
	@Test
	public void testGetProperties() throws Exception {
		Field propertiesField = AbstractExporter.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);
		Properties properties = new Properties();
		assertNotNull(exporterWrapper.getProperties());
		assertNotSame(properties, exporterWrapper.getProperties());
		propertiesField.set(exporterWrapper.getWrappedObject(), properties);
		assertSame(properties, exporterWrapper.getProperties());
	}
	
	public static class TestExporter extends AbstractExporter {
		private boolean started = false;
		@Override protected void doStart() {}
		@Override public void start() { started = true; }		
	}
	
}
