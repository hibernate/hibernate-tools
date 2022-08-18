package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.DoubleJavaType;
import org.hibernate.type.descriptor.jdbc.DoubleJdbcType;

public class DoubleType extends AbstractSingleColumnStandardBasicType<Double> {
	
	public static final DoubleType INSTANCE = new DoubleType();

	public DoubleType() {
		super(DoubleJdbcType.INSTANCE, DoubleJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "double";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), double.class.getName(), Double.class.getName() };
	}

}
