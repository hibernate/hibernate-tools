package org.hibernate.tool.orm.jbt.wrp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.internal.export.common.TemplateHelper;
import org.hibernate.tool.internal.export.java.Cfg2JavaTool;
import org.hibernate.tool.internal.export.java.EntityPOJOClass;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HbmExporterWrapperTest {
	
	private HbmExporterWrapper hbmExporterWrapper = null;
	private Configuration cfg = null;
	private File f = null;
	private boolean templateProcessed = false;
	private boolean delegateHasExported = false;


	@TempDir private File tempFolder;
	
	@BeforeEach
	public void beforeEach() {
		cfg = new Configuration();
		f = new File(tempFolder, "foo");
		hbmExporterWrapper = new HbmExporterWrapper(cfg, f);
	}
	
	@Test
	public void testConstruction() throws Exception {
		assertTrue(tempFolder.exists());
		assertFalse(f.exists());
		assertNotNull(hbmExporterWrapper);
		assertSame(f, hbmExporterWrapper.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
		ConfigurationMetadataDescriptor descriptor = (ConfigurationMetadataDescriptor)hbmExporterWrapper
				.getProperties().get(ExporterConstants.METADATA_DESCRIPTOR);
		assertNotNull(descriptor);
		Field configurationField = ConfigurationMetadataDescriptor.class.getDeclaredField("configuration");
		configurationField.setAccessible(true);
		assertSame(cfg, configurationField.get(descriptor));
	}
	
	@Test
	public void testSetDelegate() throws Exception {
		Object delegate = new Object();
		Field delegateField = HbmExporterWrapper.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		assertNull(delegateField.get(hbmExporterWrapper));
		hbmExporterWrapper.setDelegate(delegate);
		assertSame(delegate, delegateField.get(hbmExporterWrapper));
	}
	
	@Test
	public void testExportPOJO() throws Exception {
		// first without a delegate exporter
		Map<Object, Object> context = new HashMap<>();
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		pc.setEntityName("foo");
		pc.setClassName("foo");
		POJOClass pojoClass = new EntityPOJOClass(pc, new Cfg2JavaTool());
		TemplateHelper templateHelper = new TemplateHelper() {
		    public void processTemplate(String templateName, Writer output, String rootContext) {
		    	templateProcessed = true;
		    }
		};
		templateHelper.init(null, new String[] {});
		Field templateHelperField = AbstractExporter.class.getDeclaredField("vh");
		templateHelperField.setAccessible(true);
		templateHelperField.set(hbmExporterWrapper, templateHelper);
		assertFalse(templateProcessed);
		assertFalse(delegateHasExported);
		hbmExporterWrapper.exportPOJO(context, pojoClass);
		assertTrue(templateProcessed);
		assertFalse(delegateHasExported);
		// now with a delegate exporter
		templateProcessed = false;
		Object delegateExporter = new Object() {
			@SuppressWarnings("unused")
			private void exportPojo(Map<Object, Object> map, Object object, String string) {
				assertSame(map, context);
				assertSame(object, pojoClass);
				assertEquals(string, pojoClass.getQualifiedDeclarationName());
				delegateHasExported = true;
			}
		};
		hbmExporterWrapper.setDelegate(delegateExporter);
		assertFalse(templateProcessed);
		assertFalse(delegateHasExported);
		hbmExporterWrapper.exportPOJO(context, pojoClass);
		assertFalse(templateProcessed);
		assertTrue(delegateHasExported);
	}
	
	@Test
	public void testSuperExportPOJO() throws Exception {
		// first without a delegate exporter
		Map<String, Object> context = new HashMap<>();
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		pc.setEntityName("foo");
		pc.setClassName("foo");
		POJOClass pojoClass = new EntityPOJOClass(pc, new Cfg2JavaTool());
		TemplateHelper templateHelper = new TemplateHelper() {
		    public void processTemplate(String templateName, Writer output, String rootContext) {
		    	templateProcessed = true;
		    }
		};
		templateHelper.init(null, new String[] {});
		Field templateHelperField = AbstractExporter.class.getDeclaredField("vh");
		templateHelperField.setAccessible(true);
		templateHelperField.set(hbmExporterWrapper, templateHelper);
		assertFalse(templateProcessed);
		assertFalse(delegateHasExported);
		hbmExporterWrapper.superExportPOJO(context, pojoClass);
		assertTrue(templateProcessed);
		assertFalse(delegateHasExported);
		// now with a delegate exporter
		templateProcessed = false;
		Object delegateExporter = new Object() {
			@SuppressWarnings("unused")
			public void exportPojo(Map<Object, Object> map, Object object, String string) {
				assertSame(map, context);
				assertSame(object, pojoClass);
				assertEquals(string, pojoClass.getQualifiedDeclarationName());
				delegateHasExported = true;
			}
		};
		hbmExporterWrapper.setDelegate(delegateExporter);
		assertFalse(templateProcessed);
		assertFalse(delegateHasExported);
		hbmExporterWrapper.superExportPOJO(context, pojoClass);
		assertTrue(templateProcessed);
		assertFalse(delegateHasExported);
	}

	@Test
	public void testGetOutputDirectory() {
		assertNull(hbmExporterWrapper.getOutputDirectory());
		File file = new File("testGetOutputDirectory");
		hbmExporterWrapper.getProperties().put(ExporterConstants.DESTINATION_FOLDER, file);
		assertSame(file, hbmExporterWrapper.getOutputDirectory());
	}
	
}
