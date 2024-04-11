package org.hibernate.tool.orm.jbt.api;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface QueryWrapper extends Wrapper {

	default List<?> list() { return ((Query<?>)getWrappedObject()).list(); }
	default void setMaxResults(int i) { ((Query<?>)getWrappedObject()).setMaxResults(i); }

}
