package org.hibernate.tool.hbm2x;

import java.io.File;

import org.hibernate.cfg.PojoMetaDataConfiguration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.junit.Assert;
import org.junit.Test;

public class HibernateMappingExporterTest {

	private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));

	@Test
	public void testStart() throws Exception {
		PojoMetaDataConfiguration configuration = new PojoMetaDataConfiguration();
		RootClass persistentClass = new RootClass(null);
		Table table = new Table("FOO");
		Column keyColumn = new Column("BAR");
		SimpleValue key = new SimpleValue(configuration.getMetadataImplementor());
		key.setTypeName("String");
		key.addColumn(keyColumn);
		key.setTable(table);
		persistentClass.setClassName("Foo");
		persistentClass.setEntityName("Foo");
		persistentClass.setJpaEntityName("Foo");
		persistentClass.setTable(table);
		persistentClass.setIdentifier(key);
		configuration.addClass(persistentClass);	
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
