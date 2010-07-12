//$Id$
package org.hibernate.tool;


import org.dom4j.io.SAXReader;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.util.DTDEntityResolver;

public abstract class NonReflectiveTestCase extends BaseTestCase {

	private static SessionFactory sessions;
	private static Configuration cfg;
	private static Dialect dialect;
	private static Class lastTestClass;
	private org.hibernate.classic.Session session;

	
	
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
				
		/*if ( getCacheConcurrencyStrategy()!=null ) {
			
			Iterator iter = cfg.getClassMappings();
			while ( iter.hasNext() ) {
				PersistentClass clazz = (PersistentClass) iter.next();
				Iterator props = clazz.getPropertyClosureIterator();
				boolean hasLob = false;
				while ( props.hasNext() ) {
					Property prop = (Property) props.next();
					if ( prop.getValue().isSimpleValue() ) {
						String type = ( (SimpleValue) prop.getValue() ).getTypeName();
						if ( "blob".equals(type) || "clob".equals(type) ) hasLob = true;
						if ( Blob.class.getName().equals(type) || Clob.class.getName().equals(type) ) hasLob = true;
					}
				}
				if ( !hasLob && !clazz.isInherited() ) {
					cfg.setCacheConcurrencyStrategy( 
							clazz.getEntityName(), 
							getCacheConcurrencyStrategy() 
					);
				}
			}
			
			iter = cfg.getCollectionMappings();
			while ( iter.hasNext() ) {
				Collection coll = (Collection) iter.next();
				cfg.setCollectionCacheConcurrencyStrategy( 
						coll.getRole(), 
						getCacheConcurrencyStrategy() 
				);
			}
			
		}*/
		
		setDialect( Dialect.getDialect() );
		cfg2.buildMappings();
	}

	public String getCacheConcurrencyStrategy() {
		return "nonstrict-read-write";
	}

	protected void setUp() throws Exception {
		if ( getSessions()==null || lastTestClass!=getClass() ) {
			//buildSessionFactory( getMappings() );
			buildConfiguration( getMappings() );
			lastTestClass = getClass();
			prepareTestData();
		}
		
		super.setUp();
	}
	
	protected void prepareTestData() {
		// by default, nothing to do.  Let subclasses override to
		// run data initialization once per test run...
	}

	protected void runTest() throws Throwable {
		final boolean stats = sessions!=null?( (SessionFactoryImplementor) sessions ).getStatistics().isStatisticsEnabled():false;
		try {
			if (stats) sessions.getStatistics().clear();
			
			super.runTest();
			
			if (stats) sessions.getStatistics().logSummary();
			
			if ( session!=null && session.isOpen() ) {
				if ( session.isConnected() ) session.connection().rollback();
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
					if ( session.isConnected() ) session.connection().rollback();
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

	public org.hibernate.classic.Session openSession() throws HibernateException {
		session = getSessions().openSession();
		return session;
	}

	public org.hibernate.classic.Session openSession(Interceptor interceptor) 
	throws HibernateException {
		session = getSessions().openSession(interceptor);
		return session;
	}

	protected abstract String[] getMappings();

	protected SessionFactory getSessions() {
		return sessions;
	}

	private void setDialect(Dialect dialect) {
		NonReflectiveTestCase.dialect = dialect;
	}

	protected Dialect getDialect() {
		return dialect;
	}

	protected static void setCfg(Configuration cfg) {
		NonReflectiveTestCase.cfg = cfg;
	}

	protected static Configuration getCfg() {
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
    	xmlReader.setEntityResolver(new DTDEntityResolver() );
    	xmlReader.setValidation(true);
    	return xmlReader;
    }
}
