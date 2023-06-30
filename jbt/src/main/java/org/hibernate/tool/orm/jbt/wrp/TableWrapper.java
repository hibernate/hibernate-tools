package org.hibernate.tool.orm.jbt.wrp;

import org.hibernate.mapping.Table;

public class TableWrapper extends Table implements Wrapper {
	
	public TableWrapper(String name) {
		super("HibernateTools", name);
	}

}
