package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.orm.jbt.internal.factory.HbmExporterWrapperFactory;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HbmExporterWrapperTest {

	private HbmExporterWrapper hbmExporterWrapper = null; 
	private HbmExporter wrappedHbmExporter = null;

	private Configuration cfg = null;
	private File f = null;
	
	@TempDir private File tempFolder;
	
	@BeforeEach
	public void beforeEach() {
		cfg = new Configuration();
		f = new File(tempFolder, "foo");
		hbmExporterWrapper = HbmExporterWrapperFactory.createHbmExporterWrapper(cfg, f);
		wrappedHbmExporter = (HbmExporter)hbmExporterWrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() throws Exception {
		assertTrue(tempFolder.exists());
		assertFalse(f.exists());
		assertNotNull(wrappedHbmExporter);
		assertNotNull(hbmExporterWrapper);
		assertSame(f, wrappedHbmExporter.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
		ConfigurationMetadataDescriptor descriptor = (ConfigurationMetadataDescriptor)wrappedHbmExporter
				.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(descriptor);
		Field configurationField = ConfigurationMetadataDescriptor.class.getDeclaredField("configuration");
		configurationField.setAccessible(true);
		assertSame(cfg, configurationField.get(descriptor));
	}
	
}
