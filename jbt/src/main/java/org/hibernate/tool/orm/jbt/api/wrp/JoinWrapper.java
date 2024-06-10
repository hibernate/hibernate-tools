package org.hibernate.tool.orm.jbt.api.wrp;

import java.util.Iterator;

public interface JoinWrapper extends Wrapper {

	Iterator<PropertyWrapper> getPropertyIterator();
	
}
