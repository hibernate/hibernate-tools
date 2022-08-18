package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AdjustableBasicType;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.jdbc.BooleanJdbcType;

public class BooleanType extends AbstractSingleColumnStandardBasicType<Boolean>
		implements AdjustableBasicType<Boolean> {
	
	public static final BooleanType INSTANCE = new BooleanType();

	public BooleanType() {
		super(BooleanJdbcType.INSTANCE, BooleanJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "boolean";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), boolean.class.getName(), Boolean.class.getName() };
	}

}
