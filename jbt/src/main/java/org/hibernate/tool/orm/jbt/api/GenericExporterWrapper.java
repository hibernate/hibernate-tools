package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface GenericExporterWrapper extends Wrapper {
	
	default void setFilePattern(String filePattern) { 
		((GenericExporter)getWrappedObject()).getProperties().setProperty(
				ExporterConstants.FILE_PATTERN, filePattern);
	}

}
