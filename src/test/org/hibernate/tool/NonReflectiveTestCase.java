//$Id$
package org.hibernate.tool;


import org.dom4j.io.SAXReader;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionImpl;
import org.hibernate.tool.xml.XMLHelper;

public abstract class NonReflectiveTestCase extends BaseTestCase {

	private SessionFactory sessions;
	private Configuration cfg;
	private Dialect dialect;
	private Session session;
//	private MetadataSources mds;
	private Metadata md;

	protected Metadata getMetadata() {
		return md;
	}
	
	public NonReflectiveTestCase(String name, String outputdir) {
		super(name, outputdir);		
	}
	
	public NonReflectiveTestCase(String name) {
		super(name);
	}
	
	protected boolean recreateSchema() {
		return true;
	}

	/**
	 * @param files
	 */
	private void buildConfiguration(String[] files) {
		MetadataSources mds = new MetadataSources();
		addMappings(files, mds);
		cfg = new Configuration(mds);
		md = mds.buildMetadata();		
		setDialect( Dialect.getDialect() );
	}
	
	public String getCacheConcurrencyStrategy() {
		return "nonstrict-read-write";
	}

	protected void setUp() throws Exception {
		if ( getSessions()==null) {
			buildConfiguration( getMappings() );
			prepareTestData();
		}
		assertNoTables();		
		if(getOutputDir()!=null) {
			getOutputDir().mkdirs();
		}		
	}
	
	protected void prepareTestData() {}

	protected void runTest() throws Throwable {
		final boolean stats = sessions!=null?( (SessionFactoryImplementor) sessions ).getStatistics().isStatisticsEnabled():false;
		try {
			if (stats) sessions.getStatistics().clear();
			
			super.runTest();
			
			if (stats) sessions.getStatistics().logSummary();
			
			if ( session!=null && session.isOpen() ) {
				if ( session.isConnected() ) ((SessionImpl)session).connection().rollback();
				session.close();
				session = null;
				fail("unclosed session");
			}
			else {
				session=null;
			}
		}
		catch (Throwable e) {
			try {
				if ( session!=null && session.isOpen() ) {
					if ( session.isConnected() ) ((SessionImpl)session).connection().rollback();
					session.close();
				}
			}
			catch (Exception ignore) {}
			try {
				if (sessions!=null) {
					sessions.close();
					sessions=null;
				}
			}
			catch (Exception ignore) {}
			throw e;
		}
	}

	public Session openSession() throws HibernateException {
		session = getSessions().openSession();
		return session;
	}

	public Session openSession(Interceptor interceptor) 
	throws HibernateException {
		session = getSessions().withOptions().interceptor(interceptor).openSession();
		return session;
	}

	protected abstract String[] getMappings();

	protected SessionFactory getSessions() {
		return sessions;
	}

	private void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	protected Dialect getDialect() {
		return dialect;
	}

	protected void setCfg(Configuration cfg) {
		this.cfg = cfg;
	}

	protected Configuration getCfg() {
		return cfg;
	}

	
	public Configuration getConfiguration() {
		return getCfg();
	}
	
	protected void buildSessionFactory() {
		sessions = getCfg().buildSessionFactory();
	}
	
	public SAXReader getSAXReader() {
    	SAXReader xmlReader = new SAXReader();
    	xmlReader.setEntityResolver(XMLHelper.DEFAULT_DTD_RESOLVER);
    	xmlReader.setValidation(true);
    	return xmlReader;
    }
	
	protected void addMappings(String[] files, MetadataSources mds) {
		for (int i=0; i<files.length; i++) {						
			if ( !files[i].startsWith("net/") ) {
				files[i] = getBaseForMappings() + files[i];
			}
			mds.addResource( files[i]);
		}
	}
		
}
