package org.hibernate.tool.orm.jbt.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.type.BasicType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.java.CalendarJavaType;
import org.hibernate.type.descriptor.java.JavaType;

public interface TypeWrapper extends Wrapper {

	default public String toString(Object object) { 
		if (BasicType.class.isAssignableFrom(getWrappedObject().getClass())) {
			JavaType<?> javaType = ((BasicType<?>)getWrappedObject()).getJavaTypeDescriptor();
			if (javaType instanceof CalendarJavaType && object instanceof Calendar) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				return simpleDateFormat.format(((Calendar)object).getTime());
			} else {
				return ((JavaType)javaType).toString(object);
			}
		} else {
			throw new UnsupportedOperationException(
					"Class '" + 
					getWrappedObject().getClass().getName() + 
					"' does not support 'toString(Object)'." ); 
		}
	}

	default public String getName() { return ((Type)getWrappedObject()).getName(); }

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

	default public boolean isEntityType() { return ((Type)getWrappedObject()).isEntityType(); }

}
