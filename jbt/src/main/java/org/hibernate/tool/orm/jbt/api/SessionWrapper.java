package org.hibernate.tool.orm.jbt.api;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface SessionWrapper extends Wrapper {

	String getEntityName(Object o) ;
	SessionFactory getSessionFactory();
	Query<?> createQuery(String s);
	boolean isOpen();
	void close();
	boolean contains(Object o);
	jakarta.persistence.Query createCriteria(Class<?> c);

}
