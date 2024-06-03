package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface RevengSettingsWrapper extends Wrapper {

	void setDefaultPackageName(String s);
	void setDetectManyToMany(boolean b);
	void setDetectOneToOne(boolean b);
	void setDetectOptimisticLock(boolean b);

}
