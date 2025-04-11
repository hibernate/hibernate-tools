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
