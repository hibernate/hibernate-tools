package org.hibernate.tool.orm.jbt.internal.factory;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.hibernate.tool.orm.jbt.api.wrp.TypeWrapper;
import org.hibernate.tool.orm.jbt.internal.util.PrimitiveHelper;
import org.hibernate.type.BasicType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.java.CalendarJavaType;
import org.hibernate.type.descriptor.java.JavaType;

public class TypeWrapperFactory {

	public static TypeWrapper createTypeWrapper(Type wrappedType) {
		return new TypeWrapperImpl(wrappedType);
	}
	
	private static class TypeWrapperImpl implements TypeWrapper {
		
		private Type type = null;
		
		private TypeWrapperImpl(Type type) {
			this.type = type;
		}
		
		@Override 
		public Type getWrappedObject() { 
			return type; 
		}
		
		@Override
		public String toString(Object object) { 
			if (BasicType.class.isAssignableFrom(getWrappedObject().getClass())) {
				JavaType<?> javaType = ((BasicType<?>)type).getJavaTypeDescriptor();
				if (javaType instanceof CalendarJavaType && object instanceof Calendar) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
					return simpleDateFormat.format(((Calendar)object).getTime());
				} else {
					return ((JavaType)javaType).toString(object);
				}
			} else {
				throw new UnsupportedOperationException(
						"Class '" + 
						type.getClass().getName() + 
						"' does not support 'toString(Object)'." ); 
			}
		}

		@Override
		public String getName() { 
			return type.getName(); 
		}

		@Override
		public Object fromStringValue(String stringValue) {
			if (BasicType.class.isAssignableFrom(type.getClass())) {
				return ((BasicType<?>)type).getJavaTypeDescriptor().fromString(stringValue);
			} else {
				throw new UnsupportedOperationException(
						"Class '" + 
						type.getClass().getName() + 
						"' does not support 'fromStringValue(Object)'." ); 
			}
		}

		@Override
		public boolean isEntityType() { 
			return type.isEntityType(); 
		}

		@Override
		public boolean isOneToOne() { 
			if (EntityType.class.isAssignableFrom(type.getClass())) {
				return ((EntityType)type).isOneToOne();
			} else {
				throw new UnsupportedOperationException(
						"Class '" + 
						type.getClass().getName() + 
						"' does not support 'isOneToOne()'." ); 
			}
		}

		@Override
		public boolean isAnyType() { 
			return type.isAnyType(); 
		}

		@Override
		public boolean isComponentType() { 
			return type.isComponentType(); 
		}

		@Override
		public boolean isCollectionType() { 
			return type.isCollectionType(); 
		}

		@Override
		public String getAssociatedEntityName() { 
			if (isEntityType()) {
				return ((EntityType)type).getAssociatedEntityName();
			} else {
				return null;
			}
		}

		@Override
		public boolean isIntegerType() { 
			return Integer.class.isAssignableFrom(type.getReturnedClass());
		}

		@Override
		public boolean isArrayType() {
			if (CollectionType.class.isAssignableFrom(type.getClass())) {
				return ((CollectionType)type).isArrayType();
			} else {
				return false;
			}
		}

		@Override
		public boolean isInstanceOfPrimitiveType() {
			if (!(BasicType.class.isAssignableFrom(type.getClass()))) {
				return false;
			}
			return PrimitiveHelper.isPrimitive(((BasicType<?>)type).getJavaType());
		}

		@Override
		public Class<?> getPrimitiveClass() {
			if (!isInstanceOfPrimitiveType()) {
				throw new UnsupportedOperationException(
						"Class '" + 
						type.getClass().getName() + 
						"' does not support 'getPrimitiveClass()'.");
			} else {
				Class<?> javaType = ((BasicType<?>)type).getJavaType();
				if (PrimitiveHelper.isPrimitiveWrapperClass(javaType)) {
					return PrimitiveHelper.getPrimitiveClass(javaType);
				} else {
					return javaType;
				}
			}
		}

		@Override
		public String getRole() { 
			if (!isCollectionType()) {
				throw new UnsupportedOperationException(
						"Class '" + 
						type.getClass().getName() + 
						"' does not support 'getRole()'.");
			} else {
				return ((CollectionType)type).getRole();
			}
		}

		@Override
		public String getReturnedClassName() {
			return type.getReturnedClassName();
		}

	}

}
