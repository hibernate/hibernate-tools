package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class SchemaExportWrapper extends SchemaExport {
	
	private Configuration configuration = null;
	
	public SchemaExportWrapper(Configuration configuration) {
		this.configuration = configuration;
	}

}