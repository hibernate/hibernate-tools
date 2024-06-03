package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

import jakarta.persistence.Query;

public interface SessionWrapper extends Wrapper {

	String getEntityName(Object o) ;
	SessionFactoryWrapper getSessionFactory();
	QueryWrapper createQuery(String s);
	boolean isOpen();
	void close();
	boolean contains(Object o);
	Query createCriteria(Class<?> c);

}
