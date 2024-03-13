package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface ForeignKeyWrapper extends Wrapper {
	
	default Table getReferencedTable() { return ((ForeignKey)getWrappedObject()).getReferencedTable(); }


}
