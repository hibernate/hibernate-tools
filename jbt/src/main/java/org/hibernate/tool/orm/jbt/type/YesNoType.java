package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.ConvertedBasicType;
import org.hibernate.type.YesNoConverter;
import org.hibernate.type.descriptor.converter.spi.BasicValueConverter;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.jdbc.CharJdbcType;

public class YesNoType extends AbstractSingleColumnStandardBasicType<Boolean> implements ConvertedBasicType<Boolean> {

	public static final YesNoType INSTANCE = new YesNoType();

	public YesNoType() {
		super(CharJdbcType.INSTANCE, BooleanJavaType.INSTANCE);
	}

	@Override
	public String getName() {
		return "yes_no";
	}

	@Override
	public BasicValueConverter<Boolean, ?> getValueConverter() {
		return YesNoConverter.INSTANCE;
	}

}
