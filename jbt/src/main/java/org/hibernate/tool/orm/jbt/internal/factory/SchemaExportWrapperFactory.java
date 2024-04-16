package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.api.SchemaExportWrapper;

public class SchemaExportWrapperFactory {

	public static SchemaExportWrapper createSchemaExportWrapper(
			SchemaExport wrappedSchemaExport,
			Configuration configuration) { 
		return new SchemaExportWrapper() {
			@Override public SchemaExport getWrappedObject() { return wrappedSchemaExport; }
			@Override public Configuration getConfiguration() { return configuration; }
		};
	}

}
