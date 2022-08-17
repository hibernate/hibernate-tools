package org.hibernate.tool.orm.jbt.type;

import java.math.BigDecimal;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.BigDecimalJavaType;
import org.hibernate.type.descriptor.jdbc.NumericJdbcType;

public class BigDecimalType extends AbstractSingleColumnStandardBasicType<BigDecimal> {

	public static final BigDecimalType INSTANCE = new BigDecimalType();

	public BigDecimalType() {
		super( NumericJdbcType.INSTANCE, BigDecimalJavaType.INSTANCE );
	}

	@Override
	public String getName() {
		return "big_decimal";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}
	
}
