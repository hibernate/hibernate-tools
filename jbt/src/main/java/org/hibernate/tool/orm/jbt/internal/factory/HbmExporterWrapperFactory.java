package org.hibernate.tool.orm.jbt.internal.factory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.HbmExporterWrapper;
import org.hibernate.tool.orm.jbt.internal.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class HbmExporterWrapperFactory {

	public static HbmExporterWrapper createHbmExporterWrapper(
			ConfigurationWrapper configurationWrapper, 
			File file) {
		return createHbmExporterWrapper((Configuration)configurationWrapper.getWrappedObject(), file);
	}
	
	private static HbmExporterWrapper createHbmExporterWrapper(Configuration cfg, File f) {
		return new HbmExporterWrapperImpl(new HbmExporterExtension(cfg, f)) ;
	}
	
	public static class HbmExporterExtension 
			extends HbmExporter {
		
		public Object delegateExporter = null;
		
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
	
	private static class HbmExporterWrapperImpl 
			extends AbstractWrapper
			implements HbmExporterWrapper {
		
		private HbmExporterExtension hbmExporterExtension;
		
		private HbmExporterWrapperImpl(HbmExporterExtension hbmExporterExtension) {
			this.hbmExporterExtension = hbmExporterExtension;
		}
		
		@Override 
		public HbmExporter getWrappedObject() { 
			return hbmExporterExtension; 
		}
		
		@Override
		public void start() {
			hbmExporterExtension.start();
		}
		
		@Override
		public File getOutputDirectory() {
			return (File)hbmExporterExtension.getProperties().get(ExporterConstants.DESTINATION_FOLDER);
		}
		
		@Override
		public void setOutputDirectory(File f) {
			hbmExporterExtension.getProperties().put(ExporterConstants.DESTINATION_FOLDER, f);
		}
		
		@Override
		public void exportPOJO(Map<Object, Object> map, Object pojoClass) {
			hbmExporterExtension.exportPOJO(map, (POJOClass)pojoClass);
		}
		
		@Override
		public void setExportPOJODelegate(Object delegate) {
			hbmExporterExtension.delegateExporter = delegate;
		}

		
		
	}

}
