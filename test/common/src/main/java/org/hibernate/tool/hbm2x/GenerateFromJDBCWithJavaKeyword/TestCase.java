package org.hibernate.tool.hbm2x.GenerateFromJDBCWithJavaKeyword;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.metadata.MetadataSourcesFactory;
import org.hibernate.tools.test.util.JavaUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author koen@hibernate.org
 */
public class TestCase {
	
	private static String REVENG_XML =
			"<!DOCTYPE hibernate-reverse-engineering                                            \n"+
			"          SYSTEM                                                                   \n"+
	        "          'http://hibernate.sourceforge.net/hibernate-reverse-engineering-3.0.dtd'>\n"+
			"<hibernate-reverse-engineering>                                                    \n"+
	        "   <table name='MY_RETURN_HISTORY'>                                                \n"+
            "      <foreign-key                                                                 \n"+
            "            constraint-name='FK_MY_RETURN_HISTORY_RETURN_ID'                       \n"+
            "            foreign-table='MY_RETURN'>                                             \n"+
            "          <column-ref local-column='MY_RETURN_REF' foreign-column='RETURN_ID'/>    \n"+
            "          <many-to-one property='return'/>                                         \n"+
            "      </foreign-key>                                                               \n"+
	        "   </table>                                                                        \n"+
			"</hibernate-reverse-engineering>                                                     ";

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private Metadata metadata = null;
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		outputDir = temporaryFolder.getRoot();
		metadata = setUpMetadata();
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testGenerateJava() throws Exception {	
		POJOExporter exporter = new POJOExporter();		
		exporter.setMetadata(metadata);
		exporter.setOutputDirectory(outputDir);
		exporter.start();
		File myReturn = new File(outputDir, "org/reveng/MyReturn.java");
		Assert.assertTrue(myReturn.exists());
		File myReturnHistory = new File(outputDir, "org/reveng/MyReturnHistory.java");
		Assert.assertTrue(myReturnHistory.exists());
		JavaUtil.compile(outputDir);
		URLClassLoader loader = new URLClassLoader(new URL[] { outputDir.toURI().toURL() } );
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
	
	private Metadata setUpMetadata() {
		DefaultReverseEngineeringStrategy configurableNamingStrategy = new DefaultReverseEngineeringStrategy();
		configurableNamingStrategy.setSettings(new ReverseEngineeringSettings(configurableNamingStrategy).setDefaultPackageName("org.reveng").setCreateCollectionForForeignKey(false));
		OverrideRepository overrideRepository = new OverrideRepository();
		InputStream inputStream = new ByteArrayInputStream(REVENG_XML.getBytes());
		overrideRepository.addInputStream(inputStream);
		ReverseEngineeringStrategy res = overrideRepository
				.getReverseEngineeringStrategy(configurableNamingStrategy);
		return MetadataSourcesFactory
				.createJdbcSources(res, null, true)
				.buildMetadata();
	}
	
}
