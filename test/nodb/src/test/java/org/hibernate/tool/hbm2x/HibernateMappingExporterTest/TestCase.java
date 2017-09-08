package org.hibernate.tool.hbm2x.HibernateMappingExporterTest;

import java.io.File;

import org.hibernate.cfg.PojoMetaDataConfiguration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.util.MetadataHelper;
import org.hibernate.tools.test.util.HibernateUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCase {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void testStart() throws Exception {
		PojoMetaDataConfiguration configuration = new PojoMetaDataConfiguration();
		configuration.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
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
		final File outputDir = new File(temporaryFolder.getRoot(), "HibernateMappingExporterTest.testStart");
		outputDir.mkdir();
		HibernateMappingExporter exporter = new HibernateMappingExporter();
		exporter.setMetadataSources(configuration);
		exporter.setOutputDirectory(outputDir);
		final File fooHbmXml = new File(outputDir, "Foo.hbm.xml");
		Assert.assertFalse(fooHbmXml.exists());
		exporter.start();
		Assert.assertTrue(fooHbmXml.exists());
		Assert.assertTrue(fooHbmXml.delete());
	}

}
