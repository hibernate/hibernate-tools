package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.orm.jbt.api.CriteriaWrapper;

import jakarta.persistence.Query;

public class CriteriaWrapperFactory {

	public static CriteriaWrapper createCriteriaWrapper(final Query wrappedCriteria) {
		return new CriteriaWrapper() {
			@Override public Query getWrappedObject() { return wrappedCriteria; }
		};
	}
	
}
