package org.hibernate.tool.hbm2x;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Mappings;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.junit.Assert;
import org.junit.Test;

public class HibernateMappingExporterTest {
	
	private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));

	@Test
	public void testStart() {
		Configuration configuration = new Configuration();
		RootClass persistentClass = new RootClass();
		Table table = new Table("FOO");
		persistentClass.setClassName("Foo");
		persistentClass.setEntityName("Foo");
		persistentClass.setTable(table);
		Mappings mappings = configuration.createMappings();
		mappings.addClass(persistentClass);	
		final File outputDir = new File(TMP_DIR, "HibernateMappingExporterTest.testStart");
		outputDir.mkdir();
		HibernateMappingExporter exporter = new HibernateMappingExporter(configuration, outputDir);
		final File fooHbmXml = new File(outputDir, "Foo.hbm.xml");
		Assert.assertFalse(fooHbmXml.exists());
		exporter.start();
		Assert.assertTrue(fooHbmXml.exists());
		Assert.assertTrue(fooHbmXml.delete());
	}

}
