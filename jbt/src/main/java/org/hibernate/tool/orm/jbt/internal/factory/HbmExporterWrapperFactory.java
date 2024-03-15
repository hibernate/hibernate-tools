package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.orm.jbt.api.HbmExporterWrapper;

public class HbmExporterWrapperFactory {

	public static HbmExporterWrapper createHbmExporterWrapper(final HbmExporter wrappedHbmExporter) {
		return new HbmExporterWrapper() {
			@Override public HbmExporter getWrappedObject() { return wrappedHbmExporter; }
		};
	}
	
}
