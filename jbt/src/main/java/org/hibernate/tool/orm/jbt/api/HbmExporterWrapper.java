package org.hibernate.tool.orm.jbt.api;

import java.io.File;
import java.util.Map;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.hibernate.tool.orm.jbt.internal.factory.HbmExporterWrapperFactory.HbmExporterExtension;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface HbmExporterWrapper extends Wrapper {
	
	default void start() {
		((HbmExporter)getWrappedObject()).start();
	}
	
	default File getOutputDirectory() {
		return (File)((HbmExporter)getWrappedObject()).getProperties().get(ExporterConstants.DESTINATION_FOLDER);
	}
	
	default void setOutputDirectory(File f) {
		((HbmExporter)getWrappedObject()).getProperties().put(ExporterConstants.DESTINATION_FOLDER, f);
	}
	
	default void exportPOJO(Map<Object, Object> map, Object pojoClass) {
		((HbmExporterExtension)getWrappedObject()).exportPOJO(map, (POJOClass)pojoClass);
	}
	
	default void setExportPOJODelegate(Object delegate) {
		((HbmExporterExtension)getWrappedObject()).delegateExporter = delegate;
	}

}
