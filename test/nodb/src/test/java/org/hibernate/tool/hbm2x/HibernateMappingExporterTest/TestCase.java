package org.hibernate.tool.hbm2x.HibernateMappingExporterTest;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.metadata.MetadataDescriptor;
import org.hibernate.tool.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.HibernateUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCase {
	
	private static final String FOO_HBM_XML = 
			"<hibernate-mapping>              "+
			"	<class name='Foo' table='FOO'>"+
			"		<id type='String'>        "+
			"			<column name='BAR'/>  "+
			"		</id>                     "+
			"	</class>                      "+
			"</hibernate-mapping>             ";

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void testStart() throws Exception {
		File resources = new File(temporaryFolder.getRoot(), "resources");
		resources.mkdir();
		File fooHbmXmlOrigin = new File(resources, "origin.hbm.xml");
		FileWriter writer = new FileWriter(fooHbmXmlOrigin);
		writer.write(FOO_HBM_XML);
		writer.close();
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, new File[] { fooHbmXmlOrigin }, properties); 		
		final File outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		HibernateMappingExporter exporter = new HibernateMappingExporter();
		exporter.setMetadataSources(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		final File fooHbmXml = new File(outputDir, "Foo.hbm.xml");
		Assert.assertFalse(fooHbmXml.exists());
		exporter.start();
		Assert.assertTrue(fooHbmXml.exists());
		Assert.assertTrue(fooHbmXml.delete());
	}

}
