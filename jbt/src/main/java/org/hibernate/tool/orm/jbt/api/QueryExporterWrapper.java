package org.hibernate.tool.orm.jbt.api;

import java.util.List;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface QueryExporterWrapper extends Wrapper {

	default void setQueries(List<String> queries) { ((QueryExporter)getWrappedObject()).setQueries(queries); }	
	default void setFilename(String fileName) { ((QueryExporter)getWrappedObject()).getProperties().put(ExporterConstants.OUTPUT_FILE_NAME, fileName); }

}