package org.hibernate.tool.hbm2x.DocExporterTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import javax.xml.parsers.SAXParserFactory;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.doc.DocExporter;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Customer.hbm.xml",
			"Order.hbm.xml",
			"LineItem.hbm.xml",
			"Product.hbm.xml",
			"HelloWorld.hbm.xml",
			"UnionSubclass.hbm.xml",
			"DependentValue.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File outputDir = null;
	private File resourcesDir = null;
	
	private boolean ignoreDot;

	@Before
	public void setUp() throws Exception {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		DocExporter exporter = new DocExporter();
		Properties properties = new Properties();
		properties.put( "jdk5", "true"); // test generics
		properties.put(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
		if(File.pathSeparator.equals(";")) { // to work around windows/jvm not seeming to respect executing just "dot"
			properties.put("dot.executable", System.getProperties().getProperty("dot.executable","dot.exe"));
		} else {
			properties.put("dot.executable", System.getProperties().getProperty("dot.executable","dot"));
		}
		// Set to ignore dot error if dot exec not specfically set.
		// done to avoid test failure when no dot available.
		boolean dotSpecified = System.getProperties().containsKey("dot.executable");
		ignoreDot =  !dotSpecified;
		properties.setProperty("dot.ignoreerror", Boolean.toString(ignoreDot));
		exporter.getProperties().putAll(properties);
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.OUTPUT_FOLDER, outputDir);
		exporter.start();
	}
	
	@Test
    public void testExporter() {
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "index.html") );
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "assets/doc-style.css") );
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "assets/hibernate_logo.gif") );
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "tables/default/summary.html") );
		JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "tables/default/Customer.html") );
	    	Assert.assertFalse(new File(outputDir, "tables/default/UPerson.html").exists() );
	    	JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "tables/CROWN/CROWN_USERS.html") );
	    	JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "entities/org/hibernate/tool/hbm2x/Customer.html") );
	    Assert.assertTrue(new File(outputDir, "entities/org/hibernate/tool/hbm2x/UPerson.html").exists() );
	    JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "entities/org/hibernate/tool/hbm2x/UUser.html") );
		if (!ignoreDot) {
			JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "entities/entitygraph.dot"));
			JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "entities/entitygraph.png"));
			JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "tables/tablegraph.dot"));
			JUnitUtil.assertIsNonEmptyFile(new File(outputDir, "tables/tablegraph.png"));
		}
		checkHtml(outputDir);
	}
    
	@Test
    public void testCommentIncluded() {
    		// A unique customer comment!
    		File tableFile = new File(outputDir, "tables/default/Customer.html");
    		JUnitUtil.assertIsNonEmptyFile(tableFile );
		Assert.assertNotNull(FileUtil.findFirstString("A unique customer comment!", tableFile));
    }
    
    @Test
    public void testGenericsRenderedCorrectly() {
    		// A unique customer comment!
    		File tableFile = new File(outputDir, "entities/org/hibernate/tool/hbm2x/Customer.html");
    		JUnitUtil.assertIsNonEmptyFile(tableFile);	
		Assert.assertNull(
				"Generics syntax should not occur verbatim in html",
				FileUtil.findFirstString("List<", tableFile));
		Assert.assertNotNull(
				"Generics syntax occur verbatim in html",
				FileUtil.findFirstString("List&lt;", tableFile));
    }
    
    @Test
	public void testInheritedProperties() {
		File entityFile = new File(outputDir, "entities/org/hibernate/tool/hbm2x/UUser.html");
		JUnitUtil.assertIsNonEmptyFile(entityFile);
		Assert.assertNotNull(
				"Missing inherited property", 
				FileUtil.findFirstString("firstName", entityFile));
	}

	private void checkHtml(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				checkHtml(child);
			}
		} else if (file.getName().endsWith(".html")) {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				XMLReader parser = factory.newSAXParser().getXMLReader();
				TestHandler handler = new TestHandler();
				parser.setErrorHandler(handler);
				parser.setEntityResolver(new TestResolver());
				parser.parse(new InputSource(new FileInputStream(file)));
				Assert.assertEquals(file + "has errors ", 0, handler.errors);
				Assert.assertEquals(file + "has warnings ", 0, handler.warnings);
			} catch (Exception e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	
	private class TestResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			return new InputSource(new StringReader(""));
		}		
	}
	
	private class TestHandler implements ErrorHandler {
		int warnings = 0;
		int errors = 0;
		@Override
		public void warning(SAXParseException exception) throws SAXException {
			warnings++;
		}
		@Override
		public void error(SAXParseException exception) throws SAXException {
			errors++;
		}
		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			errors++;
		}		
	}
	
}
