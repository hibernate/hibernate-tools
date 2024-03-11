package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.orm.jbt.api.ExporterWrapper;

public class ExporterWrapperFactory {

	public static ExporterWrapper createExporterWrapper(final Exporter wrappedExporter) {
		return new ExporterWrapper() {
			@Override public Exporter getWrappedObject() { return wrappedExporter; }
		};
	}
	
}
