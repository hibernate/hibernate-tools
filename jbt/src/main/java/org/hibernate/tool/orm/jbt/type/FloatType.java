package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.FloatJavaType;
import org.hibernate.type.descriptor.jdbc.FloatJdbcType;

public class FloatType extends AbstractSingleColumnStandardBasicType<Float> {

	public static final FloatType INSTANCE = new FloatType();

	public FloatType() {
		super( FloatJdbcType.INSTANCE, FloatJavaType.INSTANCE );
	}

	@Override
	public String getName() {
		return "float";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), float.class.getName(), Float.class.getName() };
	}

}
