package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.ddl.DdlExporter;

public class DdlExporterWrapper extends DdlExporter {

	public void setExport(boolean b) {
		getProperties().put(ExporterConstants.EXPORT_TO_DATABASE, b);
	}

}
