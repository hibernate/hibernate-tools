package org.hibernate.tool.orm.jbt.api;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.export.hbm.HBMTagForValueVisitor;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface Cfg2HbmToolWrapper extends Wrapper {

	default String getTag(PersistentClassWrapper pcw) {
		return ((Cfg2HbmTool)getWrappedObject()).getTag(pcw.getWrappedObject());
	}
	default String getTag(PropertyWrapper pw) {
		PersistentClass persistentClass = pw.getPersistentClass();
		if(persistentClass!=null) {
			Property v = persistentClass.getVersion();
			if (v instanceof Wrapper) {
				v = (Property)((Wrapper)v).getWrappedObject();
			}
			if(v==pw.getWrappedObject()) {
				String typeName = ((SimpleValue)pw.getValue()).getTypeName();
				if("timestamp".equals(typeName) || "dbtimestamp".equals(typeName)) {
					return "timestamp";
				} else {
					return "version";
				}
			}
		}
		String toolTag = (String) pw.getValue().accept(HBMTagForValueVisitor.INSTANCE);
		if ("component".equals(toolTag) && "embedded".equals(pw.getPropertyAccessorName())){
			toolTag = "properties";
		}
		return toolTag;
	}
}
