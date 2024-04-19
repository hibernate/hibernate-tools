package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface RevengSettingsWrapper extends Wrapper {

	default void setDefaultPackageName(String s) { ((RevengSettings)getWrappedObject()).setDefaultPackageName(s); }
	default void setDetectManyToMany(boolean b) { ((RevengSettings)getWrappedObject()).setDetectManyToMany(b); }
	default void setDetectOneToOne(boolean b) { ((RevengSettings)getWrappedObject()).setDetectOneToOne(b); }
	default void setDetectOptimisticLock(boolean b) { ((RevengSettings)getWrappedObject()).setDetectOptimisticLock(b); }

}