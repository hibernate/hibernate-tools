package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DdlExporterWrapperFactoryTest {

	private DdlExporter wrappedDdlExporter = null;
	private DdlExporterWrapperFactory.DdlExporterWrapper ddlExporterWrapper = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedDdlExporter = new DdlExporter();
		ddlExporterWrapper = DdlExporterWrapperFactory.create(wrappedDdlExporter);
	}
	
	@Test
	public void testCreate() {
		assertNotNull(ddlExporterWrapper);
		assertSame(wrappedDdlExporter, ddlExporterWrapper.getWrappedObject());
	}

	@Test
	public void testSetExport() {
		assertNull(wrappedDdlExporter.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
		ddlExporterWrapper.setExport(false);
		assertFalse((Boolean)wrappedDdlExporter.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
		ddlExporterWrapper.setExport(true);
		assertTrue((Boolean)wrappedDdlExporter.getProperties().get(ExporterConstants.EXPORT_TO_DATABASE));
	}

	@Test
	public void testGetProperties() throws Exception {
		Field propertiesField = AbstractExporter.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);
		Properties properties = new Properties();
		assertNotSame(properties, ddlExporterWrapper.getProperties());
		propertiesField.set(wrappedDdlExporter, properties);
		assertSame(properties, ddlExporterWrapper.getProperties());
	}

}
