package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.export.hbm.HBMTagForValueVisitor;
import org.hibernate.tool.orm.jbt.api.wrp.Cfg2HbmToolWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public class Cfg2HbmToolWrapperFactory {

	public static Cfg2HbmToolWrapper createCfg2HbmToolWrapper() {
		return new Cfg2HbmToolWrapperImpl();
	}
	
	private static class Cfg2HbmToolWrapperImpl implements Cfg2HbmToolWrapper {
		
		private Cfg2HbmTool wrappedCfg2HbmTool = new Cfg2HbmTool();
		
		@Override 
		public Cfg2HbmTool getWrappedObject() { 
			return wrappedCfg2HbmTool; 
		}

		public String getTag(PersistentClassWrapper pcw) {
			return wrappedCfg2HbmTool.getTag((PersistentClass)pcw.getWrappedObject());
		}
		
		public String getTag(PropertyWrapper pw) {
			PersistentClassWrapper persistentClassWrapper = pw.getPersistentClass();
			if(persistentClassWrapper!=null) {
				Property v = (Property)persistentClassWrapper.getVersion().getWrappedObject();
				if(v==pw.getWrappedObject()) {
					Value pwv = (Value)pw.getValue().getWrappedObject();
					if (pwv instanceof Wrapper) {
						pwv = (Value)((Wrapper)pwv).getWrappedObject();
					}
					String typeName = ((SimpleValue)pwv).getTypeName();
					if("timestamp".equals(typeName) || "dbtimestamp".equals(typeName)) {
						return "timestamp";
					} else {
						return "version";
					}
				}
			}
			String toolTag = (String)((Value)pw.getValue().getWrappedObject()).accept(HBMTagForValueVisitor.INSTANCE);
			if ("component".equals(toolTag) && "embedded".equals(pw.getPropertyAccessorName())){
				toolTag = "properties";
			}
			return toolTag;
		}
		
	}

}
