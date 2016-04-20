/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.cfg;

import java.io.ObjectStreamClass;

import org.hibernate.HibernateException;

/**
 * @author max
 *
 */
public class JDBCBinderException extends HibernateException {

	private static final long serialVersionUID = 
			ObjectStreamClass.lookup(JDBCBinderException.class).getSerialVersionUID();
	
	/**
	 * @param string
	 * @param root
	 */
	public JDBCBinderException(String string, Throwable root) {
		super(string, root);
	}
	/**
	 * @param root
	 */
	public JDBCBinderException(Throwable root) {
		super(root);
	}
	/**
	 * @param s
	 */
	public JDBCBinderException(String s) {
		super(s);
	}

}
