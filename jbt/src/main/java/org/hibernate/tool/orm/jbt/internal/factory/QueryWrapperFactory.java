/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2024-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.orm.jbt.internal.factory;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.api.wrp.QueryWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TypeWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class QueryWrapperFactory {

	public static QueryWrapper createQueryWrapper(Query<?> wrappedQuery) {
		return new QueryWrapperImpl(wrappedQuery);
	}
	
	private static class QueryWrapperImpl 
			extends AbstractWrapper
			implements QueryWrapper {
		
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
