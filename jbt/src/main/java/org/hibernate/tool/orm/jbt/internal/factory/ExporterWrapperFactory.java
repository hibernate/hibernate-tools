package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.orm.jbt.api.ExporterWrapper;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.DummyMetadataDescriptor;

public class ExporterWrapperFactory {

	public static ExporterWrapper createExporterWrapper(final Exporter wrappedExporter) {
		ExporterWrapper result = new ExporterWrapper() {
			@Override public Exporter getWrappedObject() { return wrappedExporter; }
		};
		if (CfgExporter.class.isAssignableFrom(wrappedExporter.getClass())) {
			wrappedExporter.getProperties().put(
					ExporterConstants.METADATA_DESCRIPTOR, 
					new DummyMetadataDescriptor());
		} else {
			wrappedExporter.getProperties().put(
					ExporterConstants.METADATA_DESCRIPTOR,
					new ConfigurationMetadataDescriptor(new Configuration()));
		}
		return result;
	}
	
}
