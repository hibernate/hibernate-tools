package org.hibernate.tool.orm.jbt.wrp;

public interface Wrapper {
	
	default Object getWrappedObject() { return this; }

}
