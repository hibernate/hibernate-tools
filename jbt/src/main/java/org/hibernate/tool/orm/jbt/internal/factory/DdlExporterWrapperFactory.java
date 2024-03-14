package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.orm.jbt.api.DdlExporterWrapper;

public class DdlExporterWrapperFactory {

	public static DdlExporterWrapper createDdlExporterWrapper(final DdlExporter wrappedDdlExporter) {
		return new DdlExporterWrapper() {
			@Override public DdlExporter getWrappedObject() { return wrappedDdlExporter; }
		};
	}
	
}
