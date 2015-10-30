package org.hibernate.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.test.TestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public abstract class BaseTestCase extends TestCase {

	public static abstract class ExecuteContext {
	
		private final File sourceDir;
		private final File outputDir;
		private final List<?> jars;
		private URLClassLoader ucl;
	
		public ExecuteContext(File sourceDir, File outputDir, List<?> jars) {
			this.sourceDir = sourceDir;
			this.outputDir = outputDir;
			this.jars = jars;
		}
		
		public void run() throws Exception {
			
			TestHelper.compile(
					sourceDir, 
					outputDir, 
					TestHelper.visitAllFiles( sourceDir, Collections.emptyList() ), 
					"1.5",
					TestHelper.buildClasspath( jars )
			);

			URL[] urls = TestHelper.buildClasspathURLS(jars, outputDir);
			
			Thread currentThread = null;
			ClassLoader contextClassLoader = null;
			
			try {
				currentThread = Thread.currentThread();
				contextClassLoader = currentThread.getContextClassLoader();
				ucl = new URLClassLoader( urls, contextClassLoader);
				currentThread.setContextClassLoader( ucl );
		
				execute();
			
			} finally {
				currentThread.setContextClassLoader( contextClassLoader );
				TestHelper.deleteDir( outputDir );
			}
		}
	
		public URLClassLoader getUcl() {
			return ucl;
		}
		
		abstract protected void execute() throws Exception;
		
	}

	protected static final Logger SKIP_LOG = LoggerFactory.getLogger("org.hibernate.tool.test.SKIPPED");
	
	private File outputdir;
	
	protected File createBaseFile(String relative) {
		String root = System.getProperty("hibernatetool.test.supportdir", ".");
		return new File( root , relative);
	}
	
	public BaseTestCase(String name) {
		super(name);		
		this.outputdir = new File("toolstestoutput", getClass().getName());
	}
	
	public BaseTestCase(String name, String out) {
		super(name);		
		this.outputdir = new File("toolstestoutput", out);
	}

	protected void setUp() throws Exception {
		assertNoTables();
		
		if(getOutputDir()!=null) {
			getOutputDir().mkdirs();
		}
		
	}
	
	protected void tearDown() throws Exception {
		
		cleanupOutputDir();
		
		assertNoTables();
	}

	protected void cleanupOutputDir() {
		if (getOutputDir()!=null) {
			TestHelper.deleteDir(getOutputDir());
		}
	}

	
	protected String findFirstString(String string, File file) {
		return TestHelper.findFirstString(string, file);
	}	
	
	protected void assertFileAndExists(File file) {
		assertTrue(file + " does not exist", file.exists() );
		assertTrue(file + " not a file", file.isFile() );		
		assertTrue(file + " does not have any contents", file.length()>0);
	}

	protected File getOutputDir() {
		return outputdir;
	}

	public void assertNoTables() throws SQLException {
		Configuration configuration = new Configuration();
		Properties properties = configuration.getProperties();
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(properties);
		ServiceRegistry serviceRegistry = builder.build();
		
		JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
		ConnectionProvider connectionProvider = serviceRegistry.getService(ConnectionProvider.class);
		Connection con = null;
        try {		
        	con = connectionProvider.getConnection();
        	JDBCMetaDataDialect dialect = new JDBCMetaDataDialect();		
        	dialect.configure(
        			ReverseEngineeringRuntimeInfo.createInstance(
        					connectionProvider, jdbcServices.getSqlExceptionHelper().getSqlExceptionConverter(), new DefaultDatabaseCollector(dialect)));
		Iterator<?> tables = dialect.getTables( 
				properties.getProperty(AvailableSettings.DEFAULT_CATALOG),
				properties.getProperty(AvailableSettings.DEFAULT_SCHEMA),
				null);
		
		assertHasNext( 0, tables );
        } finally {
        	connectionProvider.closeConnection(con);	
        }
		
	}

	protected void assertHasNext(int expected, Iterator<?> iterator) {
		assertHasNext(null, expected, iterator);
	}

	/**
	 * @param i
	 * @param iterator
	 */
	protected void assertHasNext(String reason, int expected, Iterator<?> iterator) {
		int actual = 0;
		Object last = null;
		while(iterator.hasNext() && actual <= expected) {
			last = iterator.next();
			actual ++;
		}
		
		if(actual < expected) {
			throw new ComparisonFailure(reason==null?"Expected were less":reason, ""+expected, ""+actual);
		}
		
		if(actual > expected) {
			throw new ComparisonFailure((reason==null?"Expected were higher":reason)+", Last: " + last, ""+expected, ""+actual);
		}		
	}
	
	/**
	 * Intended to indicate that this test class as a whole is intended for
	 * a dialect or series of dialects.  Skips here (appliesTo = false), therefore
	 * simply indicate that the given tests target a particular feature of the
	 * current database...
	 *
	 * @param dialect
	 */
	public boolean appliesTo(Dialect dialect) {
		return true;
	}

	/**
	 * @return
	 */
	protected String getBaseForMappings() {
		return "org/hibernate/tool/";
	}


	protected void addMappings(String[] files, Configuration cfg) {
		for (int i=0; i<files.length; i++) {						
			if ( !files[i].startsWith("net/") ) {
				files[i] = getBaseForMappings() + files[i];
			}
			//System.out.println("bc in " + this.getClass() + " " + getBaseForMappings() + " " + files[i]);
			cfg.addResource( files[i], this.getClass().getClassLoader() );
		}
	}
	
	/** parse the url, fails if not valid xml. Does not validate against the DTD because they are remote */
	public Document assertValidXML(File url) {
        SAXReader reader = new SAXReader();
        reader.setValidation(false);
        Document document = null;
        try {
			document = reader.read(url);
		}
		catch (DocumentException e) {
			fail("Could not parse " + url + ":" + e); 
		}
		assertNotNull(document);
        return document;
    }
	
	protected void generateComparator() throws IOException {
		File file = new File(getOutputDir().getAbsolutePath() + "/comparator/NoopComparator.java");
		file.getParentFile().mkdirs();
		
		FileWriter fileWriter = new FileWriter(file);
		PrintWriter pw = new PrintWriter(fileWriter);
		
		pw.println("package comparator;");

		pw.println("import java.util.Comparator;");

		pw.println("public class NoopComparator implements Comparator {\n" + 
				"\n" + 
				"			public int compare(Object o1, Object o2) {\n" + 
				"				return 0;\n" + 
				"			}\n" + 
				"\n" + 
				"		}\n" + 
				"");
		
		pw.flush();
		pw.close();
	}
}

