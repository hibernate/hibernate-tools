package org.hibernate.tool.orm.jbt.api;

import org.hibernate.Session;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface SessionWrapper extends Wrapper {

	default String getEntityName(Object o) { return ((Session)getWrappedObject()).getEntityName(o); }

}
