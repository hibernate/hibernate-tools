package org.hibernate.tool.orm.jbt.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationMetadataDescriptorTest {
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.hibernate.tool.orm.jbt.internal.util'>" +
			"  <class name='ConfigurationMetadataDescriptorTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String id;
	}
	
	private ConfigurationMetadataDescriptor configurationMetadataDescriptor = null;
	private Configuration configuration = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		File testFile = File.createTempFile("test", "hbm.xml");
		PrintWriter printWriter = new PrintWriter(testFile);
		printWriter.write(TEST_HBM_XML_STRING);
		printWriter.close();
		testFile.deleteOnExit();
		configuration = new Configuration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		configuration.addFile(testFile);
		configurationMetadataDescriptor = new ConfigurationMetadataDescriptor(configuration);
	}
	
	@Test
	public void testCreation() {
		assertNotNull(configurationMetadataDescriptor);
	}
	
	@Test 
	public void testGetProperties() {
		Properties properties = new Properties();
		configuration.setProperties(properties);
		assertSame(properties, configurationMetadataDescriptor.getProperties());
	}
	
	@Test
	public void testCreateMetadata() throws Exception {
		Metadata metadata = configurationMetadataDescriptor.createMetadata();
		assertNotNull(metadata.getEntityBinding(Foo.class.getName()));
	}
	
}
