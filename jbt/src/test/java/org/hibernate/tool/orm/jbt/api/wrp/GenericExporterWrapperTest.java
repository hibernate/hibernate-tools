package org.hibernate.tool.orm.jbt.api.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.orm.jbt.internal.factory.GenericExporterWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericExporterWrapperTest {

	private GenericExporterWrapper genericExporterWrapper = null; 
	private GenericExporter wrappedGenericExporter = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedGenericExporter = new GenericExporter();
		genericExporterWrapper = GenericExporterWrapperFactory.createGenericExporterWrapper(wrappedGenericExporter);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedGenericExporter);
		assertNotNull(genericExporterWrapper);
	}
	
	@Test
	public void testSetFilePattern() {
		assertNull(wrappedGenericExporter.getProperties().get(ExporterConstants.FILE_PATTERN));
		genericExporterWrapper.setFilePattern("foobar");
		assertEquals("foobar", wrappedGenericExporter.getProperties().get(ExporterConstants.FILE_PATTERN));
	}
	
	@Test
	public void testSetTemplate() {
		assertNull(wrappedGenericExporter.getProperties().get(ExporterConstants.TEMPLATE_NAME));
		genericExporterWrapper.setTemplateName("barfoo");
		assertEquals("barfoo", wrappedGenericExporter.getProperties().get(ExporterConstants.TEMPLATE_NAME));
	}
	
	@Test
	public void testSetForEach() {
		assertNull(wrappedGenericExporter.getProperties().get(ExporterConstants.FOR_EACH));
		genericExporterWrapper.setForEach("foobar");
		assertEquals("foobar", wrappedGenericExporter.getProperties().get(ExporterConstants.FOR_EACH));
	}
	
	@Test
	public void testGetFilePattern() {
		assertNull(genericExporterWrapper.getFilePattern());
		wrappedGenericExporter.getProperties().put(ExporterConstants.FILE_PATTERN, "foobar");
		assertEquals("foobar", genericExporterWrapper.getFilePattern());
	}
	
	@Test
	public void testGetTemplateName() {
		assertNull(genericExporterWrapper.getTemplateName());
		wrappedGenericExporter.getProperties().put(ExporterConstants.TEMPLATE_NAME, "foobar");
		assertEquals("foobar", genericExporterWrapper.getTemplateName());
	}
	
}
