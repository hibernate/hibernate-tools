package org.hibernate.tool.orm.jbt.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.POJOClass;

public class HbmExporterExt extends HbmExporter {
	
	private Object delegateExporter;

	public HbmExporterExt(Configuration cfg, File file) {
		getProperties().put(
				METADATA_DESCRIPTOR, 
				new ConfigurationMetadataDescriptor(cfg));
		if (file != null) {
			getProperties().put(OUTPUT_FILE_NAME, file);
		}
	}
	
	public void setDelegate(Object delegate) {
		delegateExporter = delegate;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void exportPOJO(Map map, POJOClass pojoClass) {
		if (delegateExporter == null) {
			super.exportPOJO(map, pojoClass);
		} else {
			delegateExporterExportPOJO(
					(Map<Object, Object>)map, 
					pojoClass,
					pojoClass.getQualifiedDeclarationName());
		}
	}
	
	private void delegateExporterExportPOJO (
			Map<Object, Object> map, 
			POJOClass pojoClass, 
			String qualifiedDeclarationName) {
		try {
			delegateExporter
				.getClass()
				.getMethod("exportPojo", Map.class, Object.class, String.class)
				.invoke(delegateExporter, map, pojoClass, qualifiedDeclarationName);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
