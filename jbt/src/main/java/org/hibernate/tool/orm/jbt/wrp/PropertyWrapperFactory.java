package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;
import org.hibernate.type.Type;

public class PropertyWrapperFactory {
	
	public static Property createPropertyWrapper(Property wrappedProperty) {
		return new DelegatingPropertyWrapperImpl(wrappedProperty);
	}

	public static interface PropertyWrapper extends Wrapper{		
		@Override Property getWrappedObject();		
		Value getValue();
		Type getType();
		void setName(String name);
		void setPersistentClass(PersistentClass pc);
		PersistentClass getPersistentClass();
		boolean isComposite();
		String getPropertyAccessorName();
		String getName();
		void setValue(Value value);
		void setPropertyAccessorName(String name);
		void setCascade(String c);
		boolean isBackRef();
		boolean isSelectable();
		boolean isUpdateable();
		String getCascade();
		boolean isLazy();
		boolean isOptional();
		boolean isNaturalIdentifier();
		boolean isOptimisticLocked();
		boolean isInsertable();		
	}
	
	static class DelegatingPropertyWrapperImpl extends Property implements PropertyWrapper {
		
		private Property delegate = null;
		
		public DelegatingPropertyWrapperImpl(Property p) {
			delegate = p;
		}
		
		@Override
		public Property getWrappedObject() {
			return delegate;
		}
		
		@Override
		public Type getType() {
			TypeWrapper result = null;
			if (getWrappedObject().getValue() != null) {
				Type t = getWrappedObject().getType();
				result = t == null ? null : TypeWrapperFactory.createTypeWrapper(t);
			}
			return result;
		}

		@Override
		public Value getValue() { 
			Value v = getWrappedObject().getValue();
			return v == null ? null : ValueWrapperFactory.createValueWrapper(v); 
		}
		
		@Override 
		public void setName(String name) {
			getWrappedObject().setName(name);
		}

		@Override
		public void setPersistentClass(PersistentClass pc) {
			getWrappedObject().setPersistentClass(pc);
		}
		
		@Override
		public PersistentClass getPersistentClass() {
			return getWrappedObject().getPersistentClass();
		}

		@Override
		public boolean isComposite() {
			return getWrappedObject().isComposite();
		}

		@Override 
		public String getPropertyAccessorName() {
			return getWrappedObject().getPropertyAccessorName();
		}

		@Override
		public String getName() {
			return getWrappedObject().getName();
		}

		@Override 
		public void setValue(Value value) {
			getWrappedObject().setValue(value);
		}

		@Override
		public void setPropertyAccessorName(String name) {
			getWrappedObject().setPropertyAccessorName(name);
		}

		@Override
		public void setCascade(String c) {
			getWrappedObject().setCascade(c);
		}

		@Override
		public boolean isBackRef() {
			return getWrappedObject().isBackRef();
		}

		@Override
		public boolean isSelectable() {
			return getWrappedObject().isSelectable();
		}

		@Override
		public boolean isUpdateable() {
			return getWrappedObject().isUpdateable();
		}

		@Override
		public String getCascade() {
			return getWrappedObject().getCascade();
		}

		@Override
		public boolean isLazy() {
			return getWrappedObject().isLazy();
		}

		@Override
		public boolean isOptional() {
			return getWrappedObject().isOptional();
		}

		@Override 
		public boolean isNaturalIdentifier() {
			return getWrappedObject().isNaturalIdentifier();
		}

		@Override
		public boolean isOptimisticLocked() {
			return getWrappedObject().isOptimisticLocked();
		}

		@Override
		public boolean isInsertable() {
			return getWrappedObject().isInsertable();
		}

	}
	
}
