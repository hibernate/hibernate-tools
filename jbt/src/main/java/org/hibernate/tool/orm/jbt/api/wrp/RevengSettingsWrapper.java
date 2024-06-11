package org.hibernate.tool.orm.jbt.api.wrp;

public interface RevengSettingsWrapper extends Wrapper {

	RevengSettingsWrapper setDefaultPackageName(String s);
	RevengSettingsWrapper setDetectManyToMany(boolean b);
	RevengSettingsWrapper setDetectOneToOne(boolean b);
	RevengSettingsWrapper setDetectOptimisticLock(boolean b);

}
