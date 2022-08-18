package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.ByteJavaType;
import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

public class ByteType extends AbstractSingleColumnStandardBasicType<Byte> {

	public static final ByteType INSTANCE = new ByteType();

	public ByteType() {
		super(TinyIntJdbcType.INSTANCE, ByteJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "byte";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), byte.class.getName(), Byte.class.getName() };
	}

}
