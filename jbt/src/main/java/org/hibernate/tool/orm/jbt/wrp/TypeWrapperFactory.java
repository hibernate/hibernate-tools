package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.tool.orm.jbt.type.IntegerType;
import org.hibernate.type.BasicType;
import org.hibernate.type.Type;

public class TypeWrapperFactory {
	
	public static TypeWrapper createTypeWrapper(Type wrappedType) {
		return (TypeWrapper)Proxy.newProxyInstance(
				TypeWrapperFactory.class.getClassLoader(), 
				new Class[] { TypeWrapper.class }, 
				new TypeWrapperInvocationHandler(wrappedType));
	}
	
	static interface TypeExtension extends Wrapper {
		default public String toString(Object object) { 
			if (BasicType.class.isAssignableFrom(getWrappedObject().getClass())) {
				return ((BasicType)getWrappedObject()).getJavaTypeDescriptor().toString(object);
			} else {
				throw new UnsupportedOperationException(
						"Class '" + 
						getWrappedObject().getClass().getName() + 
						"' does not support 'toString(Object)'." ); 
			}
		}
		default Object fromStringValue(String stringValue) {
			if (BasicType.class.isAssignableFrom(getWrappedObject().getClass())) {
				return ((BasicType<?>)getWrappedObject()).getJavaTypeDescriptor().fromString(stringValue);
			} else {
				throw new UnsupportedOperationException(
						"Class '" + 
						getWrappedObject().getClass().getName() + 
						"' does not support 'fromStringValue(Object)'." ); 
			}
		}
		default boolean isOneToOne() { 
			throw new UnsupportedOperationException(
					"Class '" + 
					getWrappedObject().getClass().getName() + 
					"' does not support 'isOneToOne()'." ); 
		}
		default String getReturnedClassName() {
			Class<?> returnedClass = ((Type)getWrappedObject()).getReturnedClass();
			return returnedClass == null ? null : returnedClass.getName();
		}
		default String getAssociatedEntityName() {
			throw new UnsupportedOperationException(
					"Class '" + 
					getWrappedObject().getClass().getName() + 
					"' does not support 'getAssociatedEntityName()'." ); 
		}
		default boolean isIntegerType() {
			return IntegerType.class.isAssignableFrom(getWrappedObject().getClass());
		}
	}
	
	static interface TypeWrapper extends Type, TypeExtension {}

	private static class TypeWrapperInvocationHandler implements TypeExtension, InvocationHandler {
		
		private Type extendedType = null;
		
		public TypeWrapperInvocationHandler(Type type) {
			extendedType = type;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				Method typeClassMethod = lookupMethodInTypeClass(extendedType, method);
				if (typeClassMethod != null) {
					return typeClassMethod.invoke(extendedType, args);
				} else {
					return method.invoke(this, args);
				}
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		
		@Override
		public Type getWrappedObject() {
			return extendedType;
		}
		
	}
	
	private static Method lookupMethodInTypeClass(Type type, Method method) {
		try {
			return type
				.getClass()
				.getMethod(method.getName(), method.getParameterTypes());
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	
}
