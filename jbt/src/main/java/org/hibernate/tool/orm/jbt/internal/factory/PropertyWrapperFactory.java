package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.PropertyWrapper;
import org.hibernate.type.Type;

public class PropertyWrapperFactory {

	public static PropertyWrapper createPropertyWrapper(Property wrappedProperty) {
		return new PropertyWrapperImpl(wrappedProperty);
	}
	
	private static class PropertyWrapperImpl implements PropertyWrapper {
		
		private Property property = null;
		
		private PropertyWrapperImpl(Property property) {
			this.property = property;
		}
		
		@Override 
		public Property getWrappedObject() { 
			return property; 
		}
		
		@Override 
		public Value getValue() { 
			return property.getValue(); 
		}
		
		@Override 
		public void setName(String name) { 
			property.setName(name); 
		}
		
		@Override 
		public void setPersistentClass(PersistentClass pc) { 
			property.setPersistentClass(pc); 
		}
		
		@Override 
		public PersistentClass getPersistentClass() { 
			return property.getPersistentClass(); 
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
		public Type getType() { 
			Value v = ((Property)getWrappedObject()).getValue();
			return v == null ? null : v.getType();
		}
		
		@Override 
		public void setValue(Value value) { 
			property.setValue(value); 
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
