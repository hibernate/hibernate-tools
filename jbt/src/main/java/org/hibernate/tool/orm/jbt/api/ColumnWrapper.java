package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.Column;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ColumnWrapper extends Wrapper {
	
	default String getName() { return ((Column)getWrappedObject()).getName(); }
	default Integer getSqlTypeCode() { return ((Column)getWrappedObject()).getSqlTypeCode(); }

}
