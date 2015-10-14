package org.hibernate.tool.hbm2x;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;

/** 
 * exporter for query execution.
 * 
 **/
public class QueryExporter extends AbstractExporter {

	private String filename;
	private List<String> queryStrings;

	@SuppressWarnings({ "serial", "unchecked" })
	public void doStart() {
		Session session = null;
		SessionFactory sessionFactory = null;
		Transaction transaction = null;
		ServiceRegistry serviceRegistry = null;
		try {	
			Configuration configuration = getConfiguration();
			Properties properties = configuration.getProperties();
			Environment.verifyProperties( getConfiguration().getProperties() );
			ConfigurationHelper.resolvePlaceHolders( properties );
			serviceRegistry =  new StandardServiceRegistryBuilder()
					.applySettings( properties )
					.build();
			configuration.setSessionFactoryObserver(
				new SessionFactoryObserver() {
					public void sessionFactoryCreated(SessionFactory factory) {
					}
					public void sessionFactoryClosed(SessionFactory factory) {
					}
				}
			);
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			for (Iterator<String> iter = queryStrings.iterator(); iter.hasNext();) {
				String query = (String) iter.next();
				
				List<Object> list = session.createQuery(query).list();
				
				if(getFileName()!=null) {
					PrintWriter pw = null;
					try {
						File file = new File( getOutputDirectory(), getFileName() );
						getTemplateHelper().ensureExistence( file );
						pw = new PrintWriter( new FileWriter( file, true ) );			
						getArtifactCollector().addFile( file, "query-output" );
						
						for (Iterator<Object> iter1 = list.iterator(); iter1.hasNext();) {
							Object element = iter1.next();
							pw.println(element);
						}
						
					}
					catch (IOException e) {
						throw new ExporterException("Could not write query output",e);
					} finally {
						if(pw!=null) {
							pw.flush();
							pw.close();
						}
					}
				}
			}
			transaction.commit();
		} catch(HibernateException he) {
			if(transaction!=null) {
				transaction.rollback();
			}
			throw new ExporterException("Error occured while trying to execute query", he);
		} finally {			
			if(session!=null) {
				session.close();				
			}
			if(sessionFactory!=null) {
				sessionFactory.close();
				if (serviceRegistry != null) {
					( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
				}
			}
			
		}
	}

	private String getFileName() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void setQueries(List<String> queryStrings) {
		this.queryStrings = queryStrings;		
	}

}
