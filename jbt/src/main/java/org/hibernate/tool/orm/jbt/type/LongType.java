package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

public class LongType extends AbstractSingleColumnStandardBasicType<Long> {

	public static final LongType INSTANCE = new LongType();

	public LongType() {
		super(BigIntJdbcType.INSTANCE, LongJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "long";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), long.class.getName(), Long.class.getName() };
	}

}
