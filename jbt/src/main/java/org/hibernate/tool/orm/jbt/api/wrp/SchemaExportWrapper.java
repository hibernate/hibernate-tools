package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.List;

public interface SchemaExportWrapper extends Wrapper {
	
	void create();
	List<Throwable> getExceptions();

}
