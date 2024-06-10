package org.hibernate.tool.orm.jbt.api.wrp;

public interface Wrapper {
	
	default Object getWrappedObject() { return this; }

}
