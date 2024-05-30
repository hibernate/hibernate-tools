package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.internal.factory.ArtifactCollectorWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.factory.ConfigurationWrapperFactory;
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
		ConfigurationWrapper configurationWrapper = ConfigurationWrapperFactory.createConfigurationWrapper(configuration);
		Field field = ConfigurationMetadataDescriptor.class.getDeclaredField("configuration");
		field.setAccessible(true);
		// First use the TestExporter 
		metadataDescriptor = wrappedExporter.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		assertNotSame(configuration, field.get(metadataDescriptor));
		exporterWrapper.setConfiguration(configurationWrapper);	
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
		exporterWrapper.setConfiguration(configurationWrapper);	
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
	
	@Test
	public void testSetOutputDirectory() {
		File file = new File("");
		assertNotSame(file, wrappedExporter.getProperties().get(ExporterConstants.DESTINATION_FOLDER));		
		exporterWrapper.setOutputDirectory(file);
		assertSame(file, wrappedExporter.getProperties().get(ExporterConstants.DESTINATION_FOLDER));		
	}
	
	@Test
	public void testSetTemplatePath() {
		String[] templatePath = new String[] {};
		assertNotSame(templatePath, wrappedExporter.getProperties().get(ExporterConstants.TEMPLATE_PATH));		
		exporterWrapper.setTemplatePath(templatePath);
		assertSame(templatePath, wrappedExporter.getProperties().get(ExporterConstants.TEMPLATE_PATH));		
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
	
	@Test
	public void testGetGenericExporter() {
		// TestExporter should not return a GenericExporterFacade instance
		assertNull(exporterWrapper.getGenericExporter());
		// try now with a GenericExporter
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(new GenericExporter());
		ExporterWrapper genericExporterWrapper = exporterWrapper.getGenericExporter();
		assertSame(exporterWrapper, genericExporterWrapper);
	}
	
	@Test
	public void testGetHbm2DDlExporter() {
		// TestExporter should not return a GenericExporterFacade instance
		assertNull(exporterWrapper.getHbm2DDLExporter());
		// try now with a DdlExporter
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(new DdlExporter());
		ExporterWrapper ddlExporterWrapper = exporterWrapper.getHbm2DDLExporter();
		assertSame(exporterWrapper, ddlExporterWrapper);
	}
	
	@Test
	public void testGetQueryExporter() {
		// TestExporter should not return a GenericExporterFacade instance
		assertNull(exporterWrapper.getQueryExporter());
		// try now with a QueryExporter
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(new QueryExporter());
		ExporterWrapper queryExporterWrapper = exporterWrapper.getQueryExporter();
		assertSame(exporterWrapper, queryExporterWrapper);
	}
	
	@Test
	public void testSetCustomProperties() {
		Properties properties = new Properties();
		// 'setCustomProperties()' should not be called on other exporters than CfgExporter
		TestExporter wrappedTestExporter = (TestExporter)exporterWrapper.getWrappedObject();
		assertNull(wrappedTestExporter.props);
		exporterWrapper.setCustomProperties(properties);
		assertNull(wrappedTestExporter.props);
		// try now with CfgExporter 
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(new CfgExporter());
		CfgExporter wrappedCfgExporter = (CfgExporter)exporterWrapper.getWrappedObject();
		assertNotSame(properties, wrappedCfgExporter.getCustomProperties());
		exporterWrapper.setCustomProperties(properties);
		assertSame(properties, wrappedCfgExporter.getCustomProperties());
	}
	
	@Test
	public void testSetOutput() {
		StringWriter stringWriter = new StringWriter();
		// 'setOutput()' should not be called on other exporters than CfgExporter
		TestExporter wrappedTestExporter = (TestExporter)exporterWrapper.getWrappedObject();
		assertNull(wrappedTestExporter.output);
		exporterWrapper.setOutput(stringWriter);
		assertNull(wrappedTestExporter.output);
		// try now with CfgExporter 
		exporterWrapper = ExporterWrapperFactory.createExporterWrapper(new CfgExporter());
		CfgExporter wrappedCfgExporter = (CfgExporter)exporterWrapper.getWrappedObject();
		assertNotSame(stringWriter, wrappedCfgExporter.getOutput());
		exporterWrapper.setOutput(stringWriter);
		assertSame(stringWriter, wrappedCfgExporter.getOutput());
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
