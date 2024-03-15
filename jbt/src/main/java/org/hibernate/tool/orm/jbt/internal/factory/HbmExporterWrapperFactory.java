package org.hibernate.tool.orm.jbt.internal.factory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.hibernate.tool.orm.jbt.api.HbmExporterWrapper;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;

public class HbmExporterWrapperFactory {

	public static HbmExporterWrapper createHbmExporterWrapper(Configuration cfg, File f) {
		final HbmExporterExtension wrappedHbmExporterExtension = new HbmExporterExtension(cfg, f);
		return new HbmExporterWrapper() {
			@Override public HbmExporter getWrappedObject() { return wrappedHbmExporterExtension; }
		};
	}
	
	public static class HbmExporterExtension extends HbmExporter {
		
		private Object delegateExporter = null;
		
		private HbmExporterExtension(Configuration cfg, File f) {
			getProperties().put(
					METADATA_DESCRIPTOR, 
					new ConfigurationMetadataDescriptor(cfg));
			if (f != null) {
				getProperties().put(OUTPUT_FILE_NAME, f);
			}
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void exportPOJO(Map map, POJOClass pojoClass) {
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
				Method method = delegateExporter
						.getClass()
						.getDeclaredMethod("exportPojo", Map.class, Object.class, String.class);
				method.setAccessible(true);
				method.invoke(delegateExporter, map, pojoClass, qualifiedDeclarationName);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
