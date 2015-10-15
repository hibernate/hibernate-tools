//$Id$
package org.hibernate.tool;


import java.util.Properties;

import org.dom4j.io.SAXReader;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionImpl;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.internal.util.xml.DTDEntityResolver;
import org.hibernate.service.ServiceRegistry;

public abstract class NonReflectiveTestCase extends BaseTestCase {

	private SessionFactory sessions;
	private Configuration cfg;
	private Dialect dialect;
	private Session session;

	
	
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
		setCfg( new Configuration() );		
		if( recreateSchema() ) {
			cfg.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
		}		
		Configuration cfg2 = getCfg();
		addMappings( files, cfg2 );				
		setDialect( Dialect.getDialect() );
		cfg2.buildMappings();
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
		Properties properties = getCfg().getProperties();
		Environment.verifyProperties( properties );
		ConfigurationHelper.resolvePlaceHolders( properties );
		ServiceRegistry serviceRegistry =  new StandardServiceRegistryBuilder()
				.applySettings( properties )
				.build();
		sessions = getCfg().buildSessionFactory(serviceRegistry);
	}
	
	public SAXReader getSAXReader() {
    	SAXReader xmlReader = new SAXReader();
    	xmlReader.setEntityResolver(new DTDEntityResolver() );
    	xmlReader.setValidation(true);
    	return xmlReader;
    }
}
