package org.hibernate.tool.orm.jbt.internal.wrp;

import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;

public abstract class AbstractWrapper implements Wrapper {
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!Wrapper.class.isAssignableFrom(o.getClass())) return false;
		return getWrappedObject().equals(((Wrapper)o).getWrappedObject());
	}

}
