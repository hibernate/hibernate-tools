/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.tool.hbm2x.DocExporterTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import javax.xml.parsers.SAXParserFactory;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.DocExporter;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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
	
	@TempDir
	public File outputFolder = new File("output");

	private File srcDir = null;
	private File resourcesDir = null;
	
	private boolean ignoreDot;

	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "src");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
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
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(srcDir);
		exporter.start();
	}
	
	@Test
    public void testExporter() {
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "index.html") );
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "assets/doc-style.css") );
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "assets/hibernate_logo.gif") );
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "tables/default/summary.html") );
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "tables/default/Customer.html") );
    	assertFalse(new File(srcDir, "tables/default/UPerson.html").exists() );
    	JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "tables/CROWN/CROWN_USERS.html") );
    	JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "entities/org/hibernate/tool/hbm2x/Customer.html") );
	    assertTrue(new File(srcDir, "entities/org/hibernate/tool/hbm2x/UPerson.html").exists() );
	    JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "entities/org/hibernate/tool/hbm2x/UUser.html") );
		if (!ignoreDot) {
			JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "entities/entitygraph.dot"));
			JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "entities/entitygraph.png"));
			JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "tables/tablegraph.dot"));
			JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "tables/tablegraph.png"));
		}
		checkHtml(srcDir);
	}
    
	@Test
    public void testCommentIncluded() {
		// A unique customer comment!
		File tableFile = new File(srcDir, "tables/default/Customer.html");
		JUnitUtil.assertIsNonEmptyFile(tableFile );
		assertNotNull(FileUtil.findFirstString("A unique customer comment!", tableFile));
    }
    
    @Test
    public void testGenericsRenderedCorrectly() {
		// A unique customer comment!
		File tableFile = new File(srcDir, "entities/org/hibernate/tool/hbm2x/Customer.html");
		JUnitUtil.assertIsNonEmptyFile(tableFile);	
		assertNull(
				FileUtil.findFirstString("List<", tableFile),
				"Generics syntax should not occur verbatim in html");
		assertNotNull(
				FileUtil.findFirstString("List&lt;", tableFile),
				"Generics syntax occur verbatim in html");
    }
    
    @Test
	public void testInheritedProperties() {
		File entityFile = new File(srcDir, "entities/org/hibernate/tool/hbm2x/UUser.html");
		JUnitUtil.assertIsNonEmptyFile(entityFile);
		assertNotNull(
				FileUtil.findFirstString("firstName", entityFile),
				"Missing inherited property");
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
				assertEquals(0, handler.errors, file + "has errors ");
				assertEquals(0, handler.warnings, file + "has warnings ");
			} catch (Exception e) {
				fail(e.getMessage());
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
