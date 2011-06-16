/*
 * Created on Jun 16, 2011
 */
package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Mappings;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.hibernate.tool.test.TestHelper;

/**
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class CommentEscapeCharactersTest extends TestCase {
	
	
	private static final String TABLE_COMMENT = "This table coumment < should be escaped > !";
	private static final String ESCAPED_TABLE_COMMENT = "This table coumment &lt; should be escaped &gt; !";
	
	private static final String COLUMN_COMMENT = "This column coumment < should be escaped > !";
	private static final String ESCAPED_COLUMN_COMMENT = "This column coumment &lt; should be escaped &gt; !";
	
	private static final String ID_COMMENT = "This id coumment &lt;already escaped&gt;!";
	
	private File outputDir;
	
	public CommentEscapeCharactersTest() {
		outputDir = new File("toolstestoutput", getClass().getName());
	}
	
	protected void setUp() throws Exception {
		if(getOutputDir()!=null) {
			getOutputDir().mkdirs();
		}
	}
	
	protected void tearDown() throws Exception {
		if (getOutputDir()!=null) {
			//TestHelper.deleteDir(getOutputDir());
		}
	}
	
	public void testHbmExporterComment(){
		HibernateMappingGlobalSettings hmgs = new HibernateMappingGlobalSettings();
		HibernateMappingExporter hbmexporter = new HibernateMappingExporter(getCfg(), getOutputDir() );
		hbmexporter.setGlobalSettings(hmgs);

		hbmexporter.start();
		
		File file = new File(getOutputDir(), "test/CommentedTable.hbm.xml");
		assertTrue(file.exists());
		assertNull(TestHelper.findFirstString(TABLE_COMMENT , file ));
		assertNotNull(TestHelper.findFirstString(ESCAPED_TABLE_COMMENT, file ));
		assertNull(TestHelper.findFirstString(COLUMN_COMMENT, file ));
		assertNotNull(TestHelper.findFirstString(ESCAPED_COLUMN_COMMENT, file ));
	}
	
	protected Configuration getCfg(){
		Configuration cfg = new Configuration();
		Table t1 = new Table("CommentedTable");
		t1.setComment(TABLE_COMMENT);
		t1.setSchema("test_schema");
		
		Column c1 = new Column("id");
		SimpleValue value1 = new SimpleValue(t1);
		value1.setTypeName("int");
		value1.addColumn(c1);
		c1.setComment(ID_COMMENT);
		t1.addColumn(c1);
		
		Column c2 = new Column("name");
		SimpleValue value2 = new SimpleValue(t1);
		value2.setTypeName("string");
		value2.addColumn(c2);

		c2.setComment(COLUMN_COMMENT);
		t1.addColumn(c2);
		
		Property p1 = new Property();
		p1.setValue(value1);
		p1.setName(c1.getName());
		p1.setPropertyAccessorName("property");
		
		Property p2 = new Property();
		p2.setValue(value2);
		p2.setName(c2.getName());
		p2.setPropertyAccessorName("property");
		
		RootClass rootClass = new RootClass();
		rootClass.setClassName( "test.CommentedTable" );
		rootClass.setEntityName( "test.CommentedTable" );
		rootClass.setProxyInterfaceName("test.CommentedTableProxy");
		rootClass.setLazy(true);
		rootClass.setTable(t1);
		rootClass.setIdentifierProperty(p1);
		rootClass.addProperty(p2);
		
		Mappings mappings = cfg.createMappings();
		mappings.addClass(rootClass);
		
		return cfg;
	}

	/**
	 * @return the outputdir
	 */
	public File getOutputDir() {
		return outputDir;
	}

	public static Test suite() {
		return new CommentEscapeCharactersTest();
	}

}
