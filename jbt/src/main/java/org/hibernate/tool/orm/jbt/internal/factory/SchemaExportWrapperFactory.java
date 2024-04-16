package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.api.SchemaExportWrapper;

public class SchemaExportWrapperFactory {

	public static SchemaExportWrapper createSchemaExportWrapper(SchemaExport wrappedSchemaExport) {
		return new SchemaExportWrapper() {
			@Override public SchemaExport getWrappedObject() { return wrappedSchemaExport; }
		};
	}

}
