package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.EnumSet;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.api.wrp.SchemaExportWrapper;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.schema.TargetType;

public class SchemaExportWrapperFactory {

	public static SchemaExportWrapper createSchemaExportWrapper(
			SchemaExport wrappedSchemaExport,
			Configuration configuration) { 
		return new SchemaExportWrapperImpl(wrappedSchemaExport, configuration);
	}
	
	private static class SchemaExportWrapperImpl implements SchemaExportWrapper {
		
		private Configuration configuration = null;
		private SchemaExport schemaExport = null;
		
		private SchemaExportWrapperImpl(SchemaExport se, Configuration c) {
			this.configuration = c;
			this.schemaExport = se;
		}
		
		@Override 
		public SchemaExport getWrappedObject() {
			return schemaExport;
		}
		
		@Override
		public void create() { 
				schemaExport.create(EnumSet.of(
					TargetType.DATABASE), 
					MetadataHelper.getMetadata(configuration));
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public List<Throwable> getExceptions() { 
			return schemaExport.getExceptions(); 
		}

	}

}
