package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.orm.jbt.api.GenericExporterWrapper;

public class GenericExporterWrapperFactory {

	public static GenericExporterWrapper createGenericExporterWrapper(final GenericExporter wrappedGenericExporter) {
		return new GenericExporterWrapper() {
			@Override public GenericExporter getWrappedObject() { return wrappedGenericExporter; }
		};
	}
	
}
