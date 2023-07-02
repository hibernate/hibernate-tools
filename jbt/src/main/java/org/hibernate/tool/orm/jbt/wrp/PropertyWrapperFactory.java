package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.mapping.Property;

public class PropertyWrapperFactory {
	
	public static PropertyWrapper createPropertyWrapper(Property wrappedProperty) {
		return (PropertyWrapper)Proxy.newProxyInstance(
				ValueWrapperFactory.class.getClassLoader(), 
				new Class[] { PropertyWrapper.class }, 
				new PropertyWrapperInvocationHandler(wrappedProperty));
	}

	static interface PropertyWrapper extends Wrapper{
		@Override Property getWrappedObject();	
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
