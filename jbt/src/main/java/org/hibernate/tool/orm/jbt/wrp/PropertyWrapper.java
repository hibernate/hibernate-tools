package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;

public class PropertyWrapper extends Property implements Wrapper {
	
	@Override
	public Value getValue() {
		Value v = super.getValue();
		return v == null ? null : ValueWrapperFactory.createValueWrapper(v);
	}

}
