package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.query.QueryExporter;

public class QueryExporterWrapper extends QueryExporter {

	public void setFilename(String fileName) {
		getProperties().put(ExporterConstants.OUTPUT_FILE_NAME, fileName);
	}

}
