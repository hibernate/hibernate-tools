package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.api.export.ExporterConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericExporterWrapperTest {
	
	private GenericExporterWrapper genericExporterWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		genericExporterWrapper = new GenericExporterWrapper();
	}

	@Test
	public void testConstruction() {
		assertNotNull(genericExporterWrapper);
		assertTrue(genericExporterWrapper instanceof GenericExporterWrapper);
	}
	
	@Test
	public void testSetFilePattern() {
		assertNull(genericExporterWrapper.getProperties().get(ExporterConstants.FILE_PATTERN));
		genericExporterWrapper.setFilePattern("foobar");
		assertEquals("foobar", genericExporterWrapper.getProperties().get(ExporterConstants.FILE_PATTERN));
	}
	
	@Test
	public void testSetTemplate() {
		assertNull(genericExporterWrapper.getProperties().get(ExporterConstants.TEMPLATE_NAME));
		genericExporterWrapper.setTemplateName("barfoo");
		assertEquals("barfoo", genericExporterWrapper.getProperties().get(ExporterConstants.TEMPLATE_NAME));
	}
	
}
