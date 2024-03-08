package org.hibernate.tool.orm.jbt.api;

import java.util.List;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

import jakarta.persistence.Query;

public interface CriteriaWrapper extends Wrapper {
	
	default void setMaxResults(int intValue) { ((Query)getWrappedObject()).setMaxResults(intValue); }
	default List<?> list() { return ((Query)getWrappedObject()).getResultList(); }

}
