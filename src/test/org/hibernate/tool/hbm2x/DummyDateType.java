package org.hibernate.tool.hbm2x;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

public class DummyDateType implements UserType {

	public int[] sqlTypes() {
		return new int[]{Types.DATE};
	}

	public Class<?> returnedClass() {
		return java.sql.Date.class;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return false;
	}

	public int hashCode(Object x) throws HibernateException {
		return 0;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return null;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return null;
	}

	public Object assemble(Serializable cached, Object owner) 
			throws HibernateException {
		return null;
	}

	public Object replace(
			Object original, 
			Object target, 
			Object owner) throws HibernateException {
		return null;
	}
	
	//@Override
	public Object nullSafeGet(
			ResultSet rs, 
			String[] names,
			SessionImplementor session, 
			Object owner) throws HibernateException, SQLException {
		return null;
	}

	//@Override
	public void nullSafeSet(
			PreparedStatement st, 
			Object value, 
			int index,
			SessionImplementor session) throws HibernateException, SQLException {}

}
