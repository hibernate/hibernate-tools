package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.ShortJavaType;
import org.hibernate.type.descriptor.jdbc.SmallIntJdbcType;

public class ShortType extends AbstractSingleColumnStandardBasicType<Short> {

	public static final ShortType INSTANCE = new ShortType();

	public ShortType() {
		super(SmallIntJdbcType.INSTANCE, ShortJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "short";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), short.class.getName(), Short.class.getName() };
	}

}
