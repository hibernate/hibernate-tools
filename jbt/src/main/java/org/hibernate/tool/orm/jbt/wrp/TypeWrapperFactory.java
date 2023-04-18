package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.hibernate.tool.orm.jbt.type.IntegerType;
import org.hibernate.type.BasicType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.java.CalendarJavaType;
import org.hibernate.type.descriptor.java.JavaType;

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
				JavaType javaType = ((BasicType<?>)getWrappedObject()).getJavaTypeDescriptor();
				if (javaType instanceof CalendarJavaType && object instanceof Calendar) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
					return simpleDateFormat.format(((Calendar)object).getTime());
				} else {
					return javaType.toString(object);
				}
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
		default boolean isArrayType() {
			if (CollectionType.class.isAssignableFrom(getWrappedObject().getClass())) {
				return ((CollectionType)getWrappedObject()).isArrayType();
			} else {
				return false;
			}
		}
		default boolean isInstanceOfPrimitiveType() {
			if (!(BasicType.class.isAssignableFrom(getWrappedObject().getClass()))) {
				return false;
			}
			return isPrimitive(((BasicType<?>)getWrappedObject()).getJavaType());
		}
		default Class<?> getPrimitiveClass() {
			if (!isInstanceOfPrimitiveType()) {
				throw new UnsupportedOperationException(
						"Class '" + 
						getWrappedObject().getClass().getName() + 
						"' does not support 'getPrimitiveClass()'.");
			} else {
				Class<?> javaType = ((BasicType<?>)getWrappedObject()).getJavaType();
				if (isPrimitiveWrapperClass(javaType)) {
					return PRIMITIVE_CLASSES_MAP.get(javaType);
				} else {
					return javaType;
				}
			}
		}
		default String getRole() { 
			throw new UnsupportedOperationException(
					"Class '" + 
					getWrappedObject().getClass().getName() + 
					"' does not support 'getRole()'.");
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
	
	private static final Map<Class<?>, Class<?>> PRIMITIVE_CLASSES_MAP = Map.of(
			Integer.class, int.class,
			Short.class, short.class,
			Long.class, long.class,
			Double.class, double.class,
			Float.class, float.class,
			Character.class, char.class,
			Byte.class, byte.class,
			Boolean.class, boolean.class);
	
	private static boolean isPrimitiveWrapperClass(Class<?> candidateClass) {
		return PRIMITIVE_CLASSES_MAP.keySet().contains(candidateClass);
	}
		
	private static boolean isPrimitiveClass(Class<?> candidateClass) {
		return PRIMITIVE_CLASSES_MAP.values().contains(candidateClass);
	}
	
	private static boolean isPrimitive(Class<?> candidateClass) {
		return isPrimitiveWrapperClass(candidateClass) || isPrimitiveClass(candidateClass);
	}
}
