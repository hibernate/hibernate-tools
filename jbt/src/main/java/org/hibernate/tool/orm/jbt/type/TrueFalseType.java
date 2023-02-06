package org.hibernate.tool.orm.jbt.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.ConvertedBasicType;
import org.hibernate.type.TrueFalseConverter;
import org.hibernate.type.descriptor.converter.spi.BasicValueConverter;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.jdbc.CharJdbcType;

public class TrueFalseType extends AbstractSingleColumnStandardBasicType<Boolean>
		implements ConvertedBasicType<Boolean> {

	public static final TrueFalseType INSTANCE = new TrueFalseType();

	public TrueFalseType() {
		super(CharJdbcType.INSTANCE, new BooleanJavaType('T', 'F'));
	}

	@Override
	public String getName() {
		return "true_false";
	}

	@Override
	public BasicValueConverter<Boolean, ?> getValueConverter() {
		return TrueFalseConverter.INSTANCE;
	}

}
