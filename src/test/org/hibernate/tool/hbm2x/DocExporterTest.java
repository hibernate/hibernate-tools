package org.hibernate.tool.hbm2x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Properties;

import org.hibernate.tool.NonReflectiveTestCase;
import org.w3c.tidy.Tidy;

public class DocExporterTest extends NonReflectiveTestCase {

	private boolean ignoreDot;

	public DocExporterTest(String name) {
		super( name, "docoutput" );
	}


	protected String[] getMappings() {
		return new String[] { 
				"Customer.hbm.xml",
				"Order.hbm.xml",
				"LineItem.hbm.xml",
				"Product.hbm.xml",
				"HelloWorld.hbm.xml",
				"UnionSubclass.hbm.xml",
				"DependentValue.hbm.xml"
		};
	}
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/";
	}
	protected void setUp() throws Exception {
		super.setUp();
		DocExporter exporter = new DocExporter(getCfg(), getOutputDir() );
		Properties properties = new Properties();
		properties.put( "jdk5", "true"); // test generics
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
		
		exporter.setProperties( properties );
		exporter.start();
	}
	
	
    public void testExporter() {
    	
    	assertFileAndExists(new File(getOutputDir(), "index.html") );
	 
    	assertFileAndExists(new File(getOutputDir(), "assets/doc-style.css") );
    	assertFileAndExists(new File(getOutputDir(), "assets/hibernate_logo.gif") );
    	
    	assertFileAndExists(new File(getOutputDir(), "tables/PUBLIC/summary.html") );
    	assertFileAndExists(new File(getOutputDir(), "tables/PUBLIC/Customer.html") );
    	assertFalse(new File(getOutputDir(), "tables/PUBLIC/UPerson.html").exists() );
    	assertFileAndExists(new File(getOutputDir(), "tables/CROWN/CROWN_USERS.html") );
    	
    	assertFileAndExists(new File(getOutputDir(), "entities/org/hibernate/tool/hbm2x/Customer.html") );
    	assertTrue(new File(getOutputDir(), "entities/org/hibernate/tool/hbm2x/UPerson.html").exists() );
    	assertFileAndExists(new File(getOutputDir(), "entities/org/hibernate/tool/hbm2x/UUser.html") );
    	
		if (!ignoreDot) {
			assertFileAndExists(new File(getOutputDir(), "entities/entitygraph.dot"));
			assertFileAndExists(new File(getOutputDir(), "entities/entitygraph.png"));
			assertFileAndExists(new File(getOutputDir(), "tables/tablegraph.dot"));
	    	assertFileAndExists(new File(getOutputDir(), "tables/tablegraph.png"));
	    	
		}
		
    	    	new FileVisitor() {
    			protected void process(File dir) {
    				final Tidy tidy = new Tidy();
    				if(dir.isFile() && dir.getName().endsWith( ".html" )) {
    					testHtml( tidy, dir );
    				}
    				
    			}
    	}.visit( getOutputDir() );
    	
    	
	}
    
    public void testCommentIncluded() {
    	//A unique customer comment!
    	File tableFile = new File(getOutputDir(), "tables/PUBLIC/Customer.html");
		assertFileAndExists(tableFile );
		
		assertNotNull(findFirstString("A unique customer comment!", tableFile));
    }
    
    public void testGenericsRenderedCorrectly() {
//    	A unique customer comment!
    	File tableFile = new File(getOutputDir(), "entities/org/hibernate/tool/hbm2x/Customer.html");
		assertFileAndExists(tableFile );
		
		assertEquals("Generics syntax should not occur verbatim in html",null,findFirstString("List<", tableFile));
		assertNotNull("Generics syntax occur verbatim in html",findFirstString("List&lt;", tableFile));
    }
    
	public void testInheritedProperties()
	{
		File entityFile = new File(getOutputDir(), "entities/org/hibernate/tool/hbm2x/UUser.html");
		assertFileAndExists(entityFile);

		assertNotNull("Missing inherited property", findFirstString("firstName", entityFile));
	}

	private void testHtml(final Tidy tidy, File dir) {
		try {
			tidy.parse( new FileInputStream(dir), (OutputStream)null );
			assertEquals(dir + "has errors ", 0, tidy.getParseErrors());
			assertEquals(dir + "has warnings ", 0, tidy.getParseWarnings());
		}
		catch (FileNotFoundException e) {
			fail();
		}
	}

}
