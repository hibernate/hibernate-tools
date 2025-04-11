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

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.TypeWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;
import org.hibernate.type.Type;

public class PropertyWrapperFactory {

	public static PropertyWrapper createPropertyWrapper() {
		return createPropertyWrapper(new Property());
	}
	
	static PropertyWrapper createPropertyWrapper(Property wrappedProperty) {
		return new PropertyWrapperImpl(wrappedProperty);
	}
	
	private static class PropertyWrapperImpl 
			extends AbstractWrapper
			implements PropertyWrapper {
		
		private Property property = null;
		
		private PropertyWrapperImpl(Property property) {
			this.property = property;
		}
		
		@Override 
		public Property getWrappedObject() { 
			return property; 
		}
		
		@Override 
		public ValueWrapper getValue() { 
			Value v = property.getValue();
			return v == null ? null : ValueWrapperFactory.createValueWrapper(v);
		}
		
		@Override 
		public void setName(String name) { 
			property.setName(name); 
		}
		
		@Override 
		public void setPersistentClass(PersistentClassWrapper pc) { 
			property.setPersistentClass(pc == null ? null : (PersistentClass)pc.getWrappedObject()); 
		}
		
		@Override 
		public PersistentClassWrapper getPersistentClass() { 
			PersistentClass pc = property.getPersistentClass();
			return pc == null ? null : PersistentClassWrapperFactory.createPersistentClassWrapper(pc);
		}
		
		@Override 
		public boolean isComposite() { 
			return property.isComposite(); 
		}
		
		@Override 
		public String getPropertyAccessorName() { 
			return property.getPropertyAccessorName(); 
		}
		
		@Override 
		public String getName() { 
			return property.getName(); 
		}
		
		@Override 
		public TypeWrapper getType() { 
			Type t = null;
			Value v = ((Property)getWrappedObject()).getValue();
			if (v != null) {
				t = v.getType();
			}
			return t == null ? null : TypeWrapperFactory.createTypeWrapper(t);
		}
		
		@Override 
		public void setValue(ValueWrapper value) { 
			property.setValue(value == null ? null : (Value)value.getWrappedObject()); 
		}
		
		@Override 
		public void setPropertyAccessorName(String s) { 
			property.setPropertyAccessorName(s); 
		}
		
		@Override 
		public void setCascade(String s) { 
			property.setCascade(s); 
		}
		
		@Override 
		public boolean isBackRef() { 
			return property.isBackRef(); 
		}
		
		@Override 
		public boolean isSelectable() { 
			return property.isSelectable(); 
		}
		
		@Override 
		public boolean isInsertable() { 
			return property.isInsertable(); 
		}
		
		@Override 
		public boolean isUpdateable() { 
			return property.isUpdateable(); 
		}
		
		@Override 
		public String getCascade() { 
			return property.getCascade(); 
		}
		
		@Override 
		public boolean isLazy() { 
			return property.isLazy(); 
		}
		
		@Override 
		public boolean isOptional() { 
			return property.isOptional(); 
		}
		
		@Override 
		public boolean isNaturalIdentifier() { 
			return property.isNaturalIdentifier(); 
		}
		
		@Override 
		public boolean isOptimisticLocked() { 
			return property.isOptimisticLocked(); 
		}
		

	}

}
