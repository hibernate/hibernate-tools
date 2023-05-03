package org.hibernate.tool.orm.jbt.wrp;

import java.util.EnumSet;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.schema.TargetType;

public class SchemaExportWrapper extends SchemaExport {
	
	private Configuration configuration = null;
	
	public SchemaExportWrapper(Configuration configuration) {
		this.configuration = configuration;
	}

	public void create() {
		create(EnumSet.of(
				TargetType.DATABASE), 
				MetadataHelper.getMetadata(configuration));
	}

}