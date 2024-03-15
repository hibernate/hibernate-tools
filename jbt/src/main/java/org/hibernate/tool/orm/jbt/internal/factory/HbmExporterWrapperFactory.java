package org.hibernate.tool.orm.jbt.internal.factory;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.orm.jbt.api.HbmExporterWrapper;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;

public class HbmExporterWrapperFactory {

	public static HbmExporterWrapper createHbmExporterWrapper(Configuration cfg, File f) {
		final HbmExporterExtension wrappedHbmExporterExtension = new HbmExporterExtension(cfg, f);
		return new HbmExporterWrapper() {
			@Override public HbmExporter getWrappedObject() { return wrappedHbmExporterExtension; }
		};
	}
	
	private static class HbmExporterExtension extends HbmExporter {
		
		private HbmExporterExtension(Configuration cfg, File f) {
			getProperties().put(
					METADATA_DESCRIPTOR, 
					new ConfigurationMetadataDescriptor(cfg));
			if (f != null) {
				getProperties().put(OUTPUT_FILE_NAME, f);
			}
		}
		
	}
	
}
