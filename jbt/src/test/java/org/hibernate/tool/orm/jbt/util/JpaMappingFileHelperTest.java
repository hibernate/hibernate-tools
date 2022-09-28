package org.hibernate.tool.orm.jbt.util;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class JpaMappingFileHelperTest {

	private static final String PERSISTENCE_XML = 
			"<persistence version='2.2'" +
	        "  xmlns='http://xmlns.jcp.org/xml/ns/persistence'" +
		    "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
	        "  xsi:schemaLocation='http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd'>" +
	        "  <persistence-unit name='foo'>" +
	        "    <mapping-file>bar</mapping-file>" +
	        "  </persistence-unit>" +
			"</persistence>";
	
	private ClassLoader original = null;
	
	@TempDir
	public File tempRoot;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		tempRoot = Files.createTempDirectory("temp").toFile();
		File metaInf = new File(tempRoot, "META-INF");
		metaInf.mkdirs();
		File persistenceXml = new File(metaInf, "persistence.xml");
		persistenceXml.createNewFile();
		FileWriter fileWriter = new FileWriter(persistenceXml);
		fileWriter.write(PERSISTENCE_XML);
		fileWriter.close();
		original = Thread.currentThread().getContextClassLoader();
		ClassLoader urlCl = URLClassLoader.newInstance(
				new URL[] { new URL(tempRoot.toURI().toURL().toString())} , 
				original);
		Thread.currentThread().setContextClassLoader(urlCl);
	}
	
	@AfterEach
	public void afterEach() {
		Thread.currentThread().setContextClassLoader(original);
	}
	
	@Test
	public void testFindMappingFiles() {
		assertTrue(JpaMappingFileHelper.findMappingFiles("bar").isEmpty());
		assertTrue(JpaMappingFileHelper.findMappingFiles("foo").contains("bar"));
	}
}
