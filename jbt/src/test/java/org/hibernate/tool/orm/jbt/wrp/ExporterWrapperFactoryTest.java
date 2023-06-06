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
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.DummyMetadataDescriptor;
import org.hibernate.tool.orm.jbt.wrp.DdlExporterWrapperFactory.DdlExporterWrapper;
import org.hibernate.tool.orm.jbt.wrp.ExporterWrapperFactory.ExporterWrapper;
import org.hibernate.tool.orm.jbt.wrp.GenericExporterWrapperFactory.GenericExporterWrapper;
import org.hibernate.tool.orm.jbt.wrp.QueryExporterWrapperFactory.QueryExporterWrapper;
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
		Exporter wrappedExporter = null;
		Object metadataDescriptor = null;
		// first try the default TestExporter
		assertNotNull(exporterWrapper);
		wrappedExporter = exporterWrapper.getWrappedObject();
		assertTrue(wrappedExporter instanceof TestExporter);
		metadataDescriptor = wrappedExporter.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		// next try the CfgExporter
		exporterWrapper = ExporterWrapperFactory.create(CfgExporter.class.getName());
		assertNotNull(exporterWrapper);
		wrappedExporter = exporterWrapper.getWrappedObject();
		assertTrue(wrappedExporter instanceof CfgExporter);
		metadataDescriptor = wrappedExporter.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertTrue(metadataDescriptor instanceof DummyMetadataDescriptor);
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
		metadataDescriptor = exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		assertNotSame(configuration, field.get(metadataDescriptor));
		exporterWrapper.setConfiguration(configuration);	
		metadataDescriptor = exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof ConfigurationMetadataDescriptor);
		assertSame(configuration, field.get(metadataDescriptor));
		// Now test with a CfgExporter
		exporterWrapper = ExporterWrapperFactory.create(CfgExporter.class.getName());
		assertNotSame(properties, ((CfgExporter)exporterWrapper.getWrappedObject()).getCustomProperties());
		metadataDescriptor = exporterWrapper.getWrappedObject().getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(metadataDescriptor);
		assertTrue(metadataDescriptor instanceof DummyMetadataDescriptor);
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
	
	@Test
	public void testGetGenericExporter() {
		// TestExporter should not return a GenericExporterFacade instance
		assertNull(exporterWrapper.getGenericExporter());
		// try now with a GenericExporter
		exporterWrapper = ExporterWrapperFactory.create(GenericExporter.class.getName());
		GenericExporterWrapper genericExporterWrapper = exporterWrapper.getGenericExporter();
		assertNotNull(genericExporterWrapper);
		Object exporterTarget = ((Wrapper)exporterWrapper).getWrappedObject();
		Object genericExporterTarget = ((Wrapper)genericExporterWrapper).getWrappedObject();
		assertSame(exporterTarget, genericExporterTarget);
	}
	
	@Test
	public void testGetHbm2DDlExporter() {
		// TestExporter should not return a GenericExporterFacade instance
		assertNull(exporterWrapper.getHbm2DDLExporter());
		// try now with a DdlExporter
		exporterWrapper = ExporterWrapperFactory.create(DdlExporter.class.getName());
		DdlExporterWrapper ddlExporterWrapper = exporterWrapper.getHbm2DDLExporter();
		assertNotNull(ddlExporterWrapper);
		Object exporterTarget = ((Wrapper)exporterWrapper).getWrappedObject();
		Object ddlExporterTarget = ((Wrapper)ddlExporterWrapper).getWrappedObject();
		assertSame(exporterTarget, ddlExporterTarget);
	}
	
	@Test
	public void testGetQueryExporter() {
		// TestExporter should not return a GenericExporterFacade instance
		assertNull(exporterWrapper.getQueryExporter());
		// try now with a QueryExporter
		exporterWrapper = ExporterWrapperFactory.create(QueryExporter.class.getName());
		QueryExporterWrapper queryExporterWrapper = exporterWrapper.getQueryExporter();
		assertNotNull(queryExporterWrapper);
		Object exporterTarget = ((Wrapper)exporterWrapper).getWrappedObject();
		Object queryExporterTarget = ((Wrapper)queryExporterWrapper).getWrappedObject();
		assertSame(exporterTarget, queryExporterTarget);
	}
	
	public static class TestExporter extends AbstractExporter {
		private boolean started = false;
		@Override protected void doStart() {}
		@Override public void start() { started = true; }		
	}
	
}
