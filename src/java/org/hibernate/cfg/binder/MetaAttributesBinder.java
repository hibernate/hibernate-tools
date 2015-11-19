package org.hibernate.cfg.binder;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;

public class MetaAttributesBinder {

    public static Property bindMetaAttributes(
    		Property property, 
    		ReverseEngineeringStrategy revengStrategy,
    		Table table) {
    	Iterator<Selectable> columnIterator = property.getValue().getColumnIterator();
		while(columnIterator.hasNext()) {
			Column col = (Column) columnIterator.next();
			Map<?,?> map = revengStrategy.columnToMetaAttributes( 
					TableIdentifier.create(table), 
					col.getName());
			if(map!=null) { 
				property.setMetaAttributes(map);
			}
		}

		return property;
    }

}
