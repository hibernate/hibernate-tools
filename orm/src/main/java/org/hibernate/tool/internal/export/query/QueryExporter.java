package org.hibernate.tool.internal.export.query;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.tool.internal.export.common.AbstractExporter;

/** 
 * exporter for query execution.
 * 
 **/
public class QueryExporter extends AbstractExporter {

	public void doStart() {
		Session session = null;
		SessionFactory sessionFactory = null;
		Transaction transaction = null;
		try {	
			sessionFactory = buildMetadata().buildSessionFactory();
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			for (Iterator<?> iter = getQueryList().iterator(); iter.hasNext();) {
				String query = (String) iter.next();
				
				List<Object> list = session.createQuery(query, null).getResultList();
				
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
						throw new RuntimeException("Could not write query output",e);
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
			throw new RuntimeException("Error occured while trying to execute query", he);
		} finally {			
			if(session!=null) {
				session.close();				
			}
			if(sessionFactory!=null) {
				sessionFactory.close();
			}
			
		}
	}

	private String getFileName() {
		return (String)getProperties().get(OUTPUT_FILE_NAME);
	}
	
	private List<?> getQueryList() {
		return (List<?>)getProperties().get(QUERY_LIST);
	}

	public void setQueries(List<String> queryStrings) {
		getProperties().put(QUERY_LIST, queryStrings);		
	}

}
