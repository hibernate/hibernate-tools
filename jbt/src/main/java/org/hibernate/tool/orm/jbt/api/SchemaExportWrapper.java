package org.hibernate.tool.orm.jbt.api;

import java.util.List;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface SchemaExportWrapper extends Wrapper {
	
	void create();
	List<Throwable> getExceptions();

}
