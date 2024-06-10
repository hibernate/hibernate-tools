package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.List;

public interface QueryExporterWrapper extends Wrapper {

	void setQueries(List<String> queries);
	void setFilename(String fileName);

}