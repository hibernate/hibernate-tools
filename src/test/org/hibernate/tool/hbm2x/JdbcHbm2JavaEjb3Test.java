/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.test.TestHelper;

/**
 * @author max
 *
 *
 */
public class JdbcHbm2JavaEjb3Test extends JDBCMetaDataBinderTestCase {


	protected void setUp() throws Exception {
		super.setUp();

		POJOExporter exporter = new POJOExporter(getConfiguration(), getOutputDir() );
		exporter.setTemplatePath(new String[0]);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.getProperties().setProperty("jdk5", "true");

		exporter.start();
	}

	public void testFileExistence() {
		assertFileAndExists( new File(getOutputDir().getAbsolutePath() + "/Master.java") );
	}

	public void testUniqueConstraints() {
		assertEquals(null, findFirstString( "uniqueConstraints", new File(getOutputDir(),"Master.java") ));
		assertNotNull(findFirstString( "uniqueConstraints", new File(getOutputDir(),"Uniquemaster.java") ));
	}
	public void testCompile() {

		File file = new File("ejb3compilable");
		file.mkdir();

		ArrayList list = new ArrayList();
		List jars = new ArrayList();
		jars.add("hibernate-jpa-2.0-api-1.0.1.Final.jar");
		TestHelper.compile(getOutputDir(), file, TestHelper.visitAllFiles(getOutputDir(), list), "1.5", TestHelper.buildClasspath(jars));

		TestHelper.deleteDir(file);
	}
	
	
	
	
	protected String getBaseForMappings() {
		return "org/hibernate/tool/hbm2x/";
	}

	protected String[] getCreateSQL() {
		
		return new String[] {
				"create table master ( id char not null, name varchar(20), othername varchar(20), primary key (id) )",							
				"create table uniquemaster ( id char not null, name varchar(20), othername varchar(20), primary key (id), constraint o1 unique (name), constraint o2 unique (othername) )",
		};
	}

	protected String[] getDropSQL() {
		
		return new String[]  {
				"drop table master",			
				"drop table uniquemaster"
		};
	}
}
