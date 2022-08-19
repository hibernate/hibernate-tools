package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AdjustableBasicType;
import org.hibernate.type.descriptor.java.StringJavaType;
import org.hibernate.type.descriptor.jdbc.LongVarcharJdbcType;

public class TextType extends AbstractSingleColumnStandardBasicType<String> implements AdjustableBasicType<String> {

	public static final TextType INSTANCE = new TextType();

	public TextType() {
		super(LongVarcharJdbcType.INSTANCE, StringJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "text";
	}

}
