package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.api.QueryWrapper;
import org.hibernate.tool.orm.jbt.api.TypeWrapper;

public class QueryWrapperFactory {

	public static QueryWrapper createQueryWrapper(Query<?> wrappedQuery) {
		return new QueryWrapperImpl(wrappedQuery);
	}
	
	private static class QueryWrapperImpl implements QueryWrapper {
		
		private Query<?> query = null;
		
		private QueryWrapperImpl(Query<?> query) {
			this.query = query;
		}
		
		@Override 
		public Query<?> getWrappedObject() { 
			return query; 
		}
		
		@Override 
		public List<?> list() { 
			return query.list(); 
		}
		
		@Override 
		public void setMaxResults(int i) { 
			query.setMaxResults(i); 
		}
		
		@Override 
		public void setParameterList(String parameter, List<?> list, Object anything) {
			query.setParameterList(parameter, list);
		}
		
		@Override 
		public void setParameter(String parameter, Object value, Object anything) {
			query.setParameter(parameter, value);
		}
		
		@Override 
		public void setParameter(int position, Object value, Object anything) {
			query.setParameter(position, value);
		}
		
		@Override 
		public String[] getReturnAliases() { 
			return new String[0]; 
		}
		
		@Override 
		public TypeWrapper[] getReturnTypes() {
			return new TypeWrapper[0]; 
		}
		
	}

}
