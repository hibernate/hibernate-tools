package org.hibernate.tool.hbm2x;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.test.TestHelper;
import org.junit.Assert;

/**
 * @author koen@hibernate.org
 */
public class GenerateFromJDBCWithJavaKeywordTest extends JDBCMetaDataBinderTestCase {
	
	private static String REVENG_XML =
			"<!DOCTYPE hibernate-reverse-engineering                                            \n"+
			"          SYSTEM                                                                   \n"+
	        "          'http://hibernate.org/dtd/hibernate-reverse-engineering-3.0.dtd'>\n"+
			"<hibernate-reverse-engineering>                                                    \n"+
	        "   <table name='my_return_history'>                                                \n"+
            "      <foreign-key                                                                 \n"+
            "            constraint-name='FK_MY_RETURN_HISTORY_RETURN_ID'                       \n"+
            "            foreign-table='my_return'>                                             \n"+
            "          <column-ref local-column='my_return_ref' foreign-column='return_id'/>        \n"+
            "          <many-to-one property=\"return\"/>                                       \n"+
            "      </foreign-key>                                                               \n"+
	        "   </table>                                                                        \n"+
			"</hibernate-reverse-engineering>                                                     ";

	public GenerateFromJDBCWithJavaKeywordTest() {
		super("genfromjdbcjavakeyword");
	}

	
	protected String[] getCreateSQL() {		
		return new String[] {
				"create table my_return (                            "+
				"   return_id varchar(20) not null,                  "+
				"   constraint pk_my_return primary key (return_id) )",
				
				"create table my_return_history ( "+
				"   id varchar(20) not null, "+
				"   my_return_ref varchar(20), "+
				"   constraint pk_my_return_history primary key (id),"+
				"   constraint fk_my_return_history_return_id foreign key (my_return_ref) references my_return(return_id) )"			
		};
	}

	protected String[] getDropSQL() {		
		return new String[]  {
				"drop table my_return_history",
				"drop table my_return",				
		};
	}
	
	protected void configure(JDBCMetaDataConfiguration cfg2configure) {
		DefaultReverseEngineeringStrategy configurableNamingStrategy = new DefaultReverseEngineeringStrategy();
		configurableNamingStrategy.setSettings(new ReverseEngineeringSettings(configurableNamingStrategy).setDefaultPackageName("org.reveng").setCreateCollectionForForeignKey(false));
		cfg2configure.setReverseEngineeringStrategy(configurableNamingStrategy);
		OverrideRepository overrideRepository = new OverrideRepository();
		InputStream inputStream = new ByteArrayInputStream(REVENG_XML.getBytes());
		overrideRepository.addInputStream(inputStream);
		cfg2configure.setReverseEngineeringStrategy(overrideRepository.getReverseEngineeringStrategy(configurableNamingStrategy));
	}
	
	public void testGenerateJava() throws Exception {	
		POJOExporter exporter = new POJOExporter(cfg,getOutputDir());		
		exporter.start();
		File myReturn = new File(getOutputDir(), "org/reveng/MyReturn.java");
		Assert.assertTrue(myReturn.exists());
		File myReturnHistory = new File(getOutputDir(), "org/reveng/MyReturnHistory.java");
		Assert.assertTrue(myReturnHistory.exists());
		TestHelper.compile(getOutputDir(), getOutputDir());
		URLClassLoader loader = new URLClassLoader(new URL[] { getOutputDir().toURI().toURL() } );
		Class<?> returnClass = loader.loadClass("org.reveng.MyReturn");
		Assert.assertNotNull(returnClass);
		Class<?> returnHistoryClass = loader.loadClass("org.reveng.MyReturnHistory");
		Assert.assertNotNull(returnHistoryClass);
		Field returnField = returnHistoryClass.getDeclaredField("return_");
		Assert.assertNotNull(returnField);
		Method returnSetter = returnHistoryClass.getMethod("setReturn", new Class[] { returnClass });
		Assert.assertNotNull(returnSetter);
		loader.close();
	}
	
}
