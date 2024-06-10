package org.hibernate.tool.orm.jbt.api.wrp;

public interface SessionWrapper extends Wrapper {

	String getEntityName(Object o) ;
	SessionFactoryWrapper getSessionFactory();
	QueryWrapper createQuery(String s);
	boolean isOpen();
	void close();
	boolean contains(Object o);
	QueryWrapper createCriteria(Class<?> c);

}
