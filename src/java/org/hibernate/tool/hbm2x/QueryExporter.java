package org.hibernate.tool.hbm2x;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

/** 
 * exporter for query execution.
 * 
 **/
public class QueryExporter extends AbstractExporter {

	private String filename;
	private List queryStrings;

	public void doStart() {
		Session session = null;
		SessionFactory sessionFactory = null;
		Transaction transaction = null;
		try {		 
			sessionFactory = getConfiguration().buildSessionFactory();
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			// TODO: this is not the most efficient loop (opening/closing file)
			for (Iterator iter = queryStrings.iterator(); iter.hasNext();) {
				String query = (String) iter.next();
				
				List list = session.createQuery(query).list();
				
				if(getFileName()!=null) {
					PrintWriter pw = null;
					try {
						File file = new File( getOutputDirectory(), getFileName() );
						getTemplateHelper().ensureExistence( file );
						pw = new PrintWriter( new FileWriter( file, true ) );			
						getArtifactCollector().addFile( file, "query-output" );
						
						for (Iterator iter1 = list.iterator(); iter1.hasNext();) {
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
			}
			
			
		}
	}

	private String getFileName() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void setQueries(List queryStrings) {
		this.queryStrings = queryStrings;		
	}

}
