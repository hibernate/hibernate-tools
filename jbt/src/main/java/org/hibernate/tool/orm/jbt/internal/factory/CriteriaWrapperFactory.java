package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.List;

import org.hibernate.tool.orm.jbt.api.wrp.CriteriaWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

import jakarta.persistence.Query;

public class CriteriaWrapperFactory {

	public static CriteriaWrapper createCriteriaWrapper(final Query wrappedCriteria) {
		return new CriteriaWrapperImpl(wrappedCriteria);
	}
	
	private static class CriteriaWrapperImpl
			extends AbstractWrapper
			implements CriteriaWrapper {
		
		private Query query = null;
		
		private CriteriaWrapperImpl(Query query) {
			this.query = query;
		}
		
		@Override 
		public Query getWrappedObject() { 
			return query; 
		}
		
		@Override 
		public void setMaxResults(int intValue) { 
			((Query)getWrappedObject()).setMaxResults(intValue); 
		}
		
		@Override
		public List<?> list() { 
			return ((Query)getWrappedObject()).getResultList(); 
		}
		
	}
	
}
