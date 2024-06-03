package org.hibernate.tool.orm.jbt.api;

import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface SchemaExportWrapper extends Wrapper {
	
	Configuration getConfiguration();
	void create();
	List<Throwable> getExceptions();

}
