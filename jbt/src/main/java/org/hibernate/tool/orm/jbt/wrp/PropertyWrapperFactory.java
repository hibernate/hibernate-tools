package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory.TypeWrapper;
import org.hibernate.type.Type;

public class PropertyWrapperFactory {
	
	public static PropertyWrapper createPropertyWrapper(Property wrappedProperty) {
		return (PropertyWrapper)Proxy.newProxyInstance(
				ValueWrapperFactory.class.getClassLoader(), 
				new Class[] { PropertyWrapper.class }, 
				new PropertyWrapperInvocationHandler(wrappedProperty));
	}

	static interface PropertyWrapper extends Wrapper{
		
		@Override Property getWrappedObject();
		
		default Value getValue() { 
			Value v = getWrappedObject().getValue();
			return v == null ? null : ValueWrapperFactory.createValueWrapper(v); 
		}

		default void setName(String name) {
			getWrappedObject().setName(name);
		}

		default void setPersistentClass(PersistentClass pc) {
			getWrappedObject().setPersistentClass(pc);
		}

		default PersistentClassWrapper getPersistentClass() {
			PersistentClassWrapper pc = (PersistentClassWrapper)getWrappedObject().getPersistentClass();
			return pc == null ? null : PersistentClassWrapperFactory.createPersistentClassWrapper(pc);
		}

		default boolean isComposite() {
			return getWrappedObject().isComposite();
		}

		default String getPropertyAccessorName() {
			return getWrappedObject().getPropertyAccessorName();
		}

		default String getName() {
			return getWrappedObject().getName();
		}

		default Type getType() {
			TypeWrapper result = null;
			if (getWrappedObject().getValue() != null) {
				Type t = getWrappedObject().getType();
				result = t == null ? null : TypeWrapperFactory.createTypeWrapper(t);
			}
			return result;
		}

		default void setValue(BasicValue value) {
			getWrappedObject().setValue(value);
		}

		default void setPropertyAccessorName(String name) {
			getWrappedObject().setPropertyAccessorName(name);
		}

		default void setCascade(String c) {
			getWrappedObject().setCascade(c);
		}

		default boolean isBackRef() {
			return getWrappedObject().isBackRef();
		}

		default boolean isSelectable() {
			return getWrappedObject().isSelectable();
		}

		default boolean isUpdateable() {
			return getWrappedObject().isUpdateable();
		}

		default String getCascade() {
			return getWrappedObject().getCascade();
		}

		default boolean isLazy() {
			return getWrappedObject().isLazy();
		}
		
	}
	
	static class PropertyWrapperInvocationHandler implements InvocationHandler, PropertyWrapper {
		
		private Property delegate = null;
		
		public PropertyWrapperInvocationHandler(Property property) {
			delegate = property;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Method m = lookupMethodInPropertyWrapperClass(method);
			if (m != null) {
				return m.invoke(this, args);
			} else {
				return method.invoke(delegate, args);
			}
		}
		
		@Override 
		public Property getWrappedObject() {
			return delegate;
		}
		
		
	}
	
	private static Method lookupMethodInPropertyWrapperClass(Method method) {
		try {
			return PropertyWrapper.class.getMethod(method.getName(), method.getParameterTypes());
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

}
