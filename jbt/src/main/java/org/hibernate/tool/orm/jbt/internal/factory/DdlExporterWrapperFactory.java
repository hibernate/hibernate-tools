package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.Properties;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.orm.jbt.api.DdlExporterWrapper;

public class DdlExporterWrapperFactory {

	public static DdlExporterWrapper createDdlExporterWrapper(final DdlExporter wrappedDdlExporter) {
		return new DdlExporterWrapperImpl(wrappedDdlExporter);
	}
	
	private static class DdlExporterWrapperImpl implements DdlExporterWrapper {
		
		private DdlExporter ddlExporter = null;
		
		private DdlExporterWrapperImpl(DdlExporter ddlExporter) {
			this.ddlExporter = ddlExporter;
		}
		
		@Override 
		public DdlExporter getWrappedObject() { 
			return ddlExporter; 
		}
		
		@Override
		public void setExport(boolean b) { 
			((DdlExporter)getWrappedObject()).getProperties().put(
					ExporterConstants.EXPORT_TO_DATABASE, b);
		}
		
		@Override
		public Properties getProperties() {
			return ((DdlExporter)getWrappedObject()).getProperties();
		}

	}
	
}
