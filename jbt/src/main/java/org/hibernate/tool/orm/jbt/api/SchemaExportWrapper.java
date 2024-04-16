package org.hibernate.tool.orm.jbt.api;

import java.util.EnumSet;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.schema.TargetType;

public interface SchemaExportWrapper extends Wrapper {
	
	Configuration getConfiguration();
	default void create() { (
			(SchemaExport)getWrappedObject()).create(EnumSet.of(
				TargetType.DATABASE), 
				MetadataHelper.getMetadata(getConfiguration()));
	}
	@SuppressWarnings("unchecked")
	default List<Throwable> getExceptions() { return ((SchemaExport)getWrappedObject()).getExceptions(); }

}
