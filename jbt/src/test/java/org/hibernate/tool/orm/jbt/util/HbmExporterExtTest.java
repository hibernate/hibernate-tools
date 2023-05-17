package org.hibernate.tool.orm.jbt.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.ExporterConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HbmExporterExtTest {
	
	private HbmExporterExt hbmExporterExt = null;
	private Configuration cfg = null;
	private File f = null;

	@TempDir private File tempFolder;
	
	@BeforeEach
	public void beforeEach() {
		cfg = new Configuration();
		f = new File(tempFolder, "foo");
		hbmExporterExt = new HbmExporterExt(cfg, f);
	}
	
	@Test
	public void testConstruction() throws Exception {
		assertTrue(tempFolder.exists());
		assertFalse(f.exists());
		assertNotNull(hbmExporterExt);
		assertSame(f, hbmExporterExt.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
		ConfigurationMetadataDescriptor descriptor = (ConfigurationMetadataDescriptor)hbmExporterExt
				.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(descriptor);
		Field configurationField = ConfigurationMetadataDescriptor.class.getDeclaredField("configuration");
		configurationField.setAccessible(true);
		assertSame(cfg, configurationField.get(descriptor));
	}

}
