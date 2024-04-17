package org.hibernate.tool.orm.jbt.api;

import org.hibernate.SessionFactory;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface SessionFactoryWrapper extends Wrapper {

	default void close() { ((SessionFactory)getWrappedObject()).close(); }

}
