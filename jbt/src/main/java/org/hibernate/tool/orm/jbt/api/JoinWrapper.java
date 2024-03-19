package org.hibernate.tool.orm.jbt.api;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.Property;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface JoinWrapper extends Wrapper {

	default Iterator<Property> getPropertyIterator() {
		return ((Join)getWrappedObject()).getProperties().iterator();
	}
	
}
