package org.hibernate.tool.orm.jbt.util;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.internal.export.hbm.HbmExporter;

public class HbmExporterExt extends HbmExporter {
	
	public HbmExporterExt(Configuration cfg, File file) {
		getProperties().put(
				METADATA_DESCRIPTOR, 
				new ConfigurationMetadataDescriptor(cfg));
		if (file != null) {
			getProperties().put(OUTPUT_FILE_NAME, file);
		}
	}
	
}
