package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.orm.jbt.api.GenericExporterWrapper;

public class GenericExporterWrapperFactory {

	public static GenericExporterWrapper createGenericExporterWrapper(final GenericExporter wrappedGenericExporter) {
		return new GenericExporterWrapperImpl(wrappedGenericExporter);
	}
	
	private static class GenericExporterWrapperImpl implements GenericExporterWrapper {
		
		private GenericExporter genericExporter = null;
		
		private GenericExporterWrapperImpl(GenericExporter genericExporter) {
			this.genericExporter = genericExporter;
		}
		
		@Override 
		public GenericExporter getWrappedObject() { 
			return genericExporter; 
		}
		
		@Override
		public void setFilePattern(String filePattern) { 
			genericExporter.getProperties().setProperty(
					ExporterConstants.FILE_PATTERN, filePattern);
		}
		
		@Override
		public void setTemplateName(String templateName) {
			genericExporter.getProperties().setProperty(
					ExporterConstants.TEMPLATE_NAME, templateName);
		}
		
		@Override
		public void setForEach(String forEach) {
			genericExporter.getProperties().setProperty(
					ExporterConstants.FOR_EACH, forEach);
		}
		
		@Override
		public String getFilePattern() {
			return genericExporter.getProperties().getProperty(
					ExporterConstants.FILE_PATTERN);
		}
		
		@Override
		public String getTemplateName() {
			return genericExporter.getProperties().getProperty(
					ExporterConstants.TEMPLATE_NAME);
		}

	}
	
}
