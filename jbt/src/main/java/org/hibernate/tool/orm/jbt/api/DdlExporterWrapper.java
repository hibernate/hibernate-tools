package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface DdlExporterWrapper extends Wrapper {
	
	default void setExport(boolean b) { 
		((DdlExporter)getWrappedObject()).getProperties().put(
				ExporterConstants.EXPORT_TO_DATABASE, b);
	}

}
