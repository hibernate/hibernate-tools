package org.hibernate.tools.test.util;

import java.util.Iterator;

import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

public class HibernateUtil {
	
	public static ForeignKey getForeignKey(Table table, String fkName) {
		ForeignKey result = null;
		Iterator<?> iter = table.getForeignKeyIterator();
		while (iter.hasNext()) {
			ForeignKey fk = (ForeignKey) iter.next();
			if (fk.getName().equals(fkName)) {
				result = fk;
				break;
			}
		}
		return result;
	}
	 
}
