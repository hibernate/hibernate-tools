package org.hibernate.tool.orm.jbt.type;

import java.util.Currency;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.CurrencyJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class CurrencyType extends AbstractSingleColumnStandardBasicType<Currency> {

	public static final CurrencyType INSTANCE = new CurrencyType();

	public CurrencyType() {
		super(VarcharJdbcType.INSTANCE, CurrencyJavaType.INSTANCE);
	}

	public String getName() {
		return "currency";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

}
