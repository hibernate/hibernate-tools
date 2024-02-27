package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.orm.jbt.internal.factory.ArtifactCollectorWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ArtifactCollectorWrapperTest {
	
	private static final String FOO_XML = "<foo><bar/></foo>";
	
	private Map<String, List<File>> filesMap = null;
	private List<File> xmlFiles = new ArrayList<File>();
	private ArtifactCollectorWrapper wrapper = null;
	private File fooFile = null;
	
	@TempDir
	private File tempDir;
	
	@SuppressWarnings("unchecked")
	@BeforeEach
	public void beforeEach() throws Exception {
		wrapper = ArtifactCollectorWrapperFactory.createArtifactCollectorWrapper();
		ArtifactCollector wrapped = (ArtifactCollector)wrapper.getWrappedObject();
		Field filesField = wrapped.getClass().getDeclaredField("files");
		filesField.setAccessible(true);
		filesMap = (Map<String, List<File>>)filesField.get(wrapped);
		tempDir = Files.createTempDirectory("temp").toFile();
		fooFile = new File(tempDir, "foo.xml");
		Files.write(fooFile.toPath(), FOO_XML.getBytes());
		xmlFiles.add(fooFile);
	}
	
	@Test
	public void testGetFileTypes() {
		assertTrue(wrapper.getFileTypes().isEmpty());
		filesMap.put("xml", xmlFiles);
		filesMap.put("foo", new ArrayList<File>());
		Set<String> fileTypes = wrapper.getFileTypes();
		assertTrue(fileTypes.size() == 2);
		assertTrue(fileTypes.contains("xml"));
		assertTrue(fileTypes.contains("foo"));
	}
	
	@Test
	public void testGetFiles() {
		assertTrue(wrapper.getFiles("xml").length == 0);
		filesMap.put("xml", xmlFiles);
		File[] files = wrapper.getFiles("xml");
		assertTrue(files.length == 1);
		assertSame(fooFile, files[0]);
	}
	
	@Test
	public void testFormatFiles() throws IOException {
		wrapper.formatFiles();
		// <foo><bar/></foo>
		assertEquals(FOO_XML, new String(Files.readAllBytes(fooFile.toPath())));
		filesMap.put("xml", xmlFiles);
		wrapper.formatFiles();
		// <?xml version="1.0" encoding="UTF-8" standalone="no"?>
		// <foo>
		//     <bar/>
		// </foo>
		assertNotEquals(FOO_XML, new String(Files.readAllBytes(fooFile.toPath())));
	}

}
