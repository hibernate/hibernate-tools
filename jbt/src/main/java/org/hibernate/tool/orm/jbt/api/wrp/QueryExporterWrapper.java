package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.List;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface QueryExporterWrapper extends Wrapper {

	void setQueries(List<String> queries);
	void setFilename(String fileName);

}