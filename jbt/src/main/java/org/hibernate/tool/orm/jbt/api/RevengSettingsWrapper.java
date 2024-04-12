package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface RevengSettingsWrapper extends Wrapper {

	default void setDefaultPackageName(String s) { ((RevengSettings)getWrappedObject()).setDefaultPackageName(s); }

}
