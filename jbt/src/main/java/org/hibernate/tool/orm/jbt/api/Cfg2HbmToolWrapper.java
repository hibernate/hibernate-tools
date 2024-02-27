package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.export.hbm.HBMTagForValueVisitor;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface Cfg2HbmToolWrapper extends Wrapper {

	default String getTag(PersistentClass pc) {
		return ((Cfg2HbmTool)getWrappedObject()).getTag(pc);
	}
	default String getTag(Property p) {
		if (p instanceof Wrapper) {
			p = (Property)((Wrapper)p).getWrappedObject();
		}
		PersistentClass persistentClass = p.getPersistentClass();
		if(persistentClass!=null) {
			Property v = persistentClass.getVersion();
			if (v instanceof Wrapper) {
				v = (Property)((Wrapper)v).getWrappedObject();
			}
			if(v==p) {
				String typeName = ((SimpleValue)p.getValue()).getTypeName();
				if("timestamp".equals(typeName) || "dbtimestamp".equals(typeName)) {
					return "timestamp";
				} else {
					return "version";
				}
			}
		}
		String toolTag = (String) p.getValue().accept(HBMTagForValueVisitor.INSTANCE);
		if ("component".equals(toolTag) && "embedded".equals(p.getPropertyAccessorName())){
			toolTag = "properties";
		}
		return toolTag;
	}
}
