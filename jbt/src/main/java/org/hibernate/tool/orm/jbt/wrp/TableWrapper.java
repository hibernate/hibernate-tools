package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.Table;

public class TableWrapper extends Table implements Wrapper {
	
	public TableWrapper(String name) {
		super("HibernateTools", name);
	}
	
	@Override
	public KeyValue getIdentifierValue() {
		KeyValue result = super.getIdentifierValue();
		return result == null ? null : (KeyValue)ValueWrapperFactory.createValueWrapper(result);
	}

}
