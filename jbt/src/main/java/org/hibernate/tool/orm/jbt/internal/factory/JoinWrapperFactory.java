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

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.Property;
import org.hibernate.tool.orm.jbt.api.wrp.JoinWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class JoinWrapperFactory {

	public static JoinWrapper createJoinWrapper(Join wrappedJoin) {
		return new JoinWrapperImpl(wrappedJoin);
	}
	
	private static class JoinWrapperImpl 
			extends AbstractWrapper
			implements JoinWrapper {
		
		private Join join = null;
		
		private JoinWrapperImpl(Join join) {
			this.join = join;
		}
		
		@Override 
		public Join getWrappedObject() { 
			return join; 
		}
		
		@Override
		public Iterator<PropertyWrapper> getPropertyIterator() {
			Iterator<Property> propertyIterator = join.getProperties().iterator();
			return new Iterator<PropertyWrapper>() {
				@Override
				public boolean hasNext() {
					return propertyIterator.hasNext();
				}

				@Override
				public PropertyWrapper next() {
					return PropertyWrapperFactory.createPropertyWrapper(propertyIterator.next());
				}
				
			};
		}
		
	}
	
}
