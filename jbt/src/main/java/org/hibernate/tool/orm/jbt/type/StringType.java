package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AdjustableBasicType;
import org.hibernate.type.descriptor.java.StringJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class StringType extends AbstractSingleColumnStandardBasicType<String> implements AdjustableBasicType<String> {

	public static final StringType INSTANCE = new StringType();

	public StringType() {
		super(VarcharJdbcType.INSTANCE, StringJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "string";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

}
