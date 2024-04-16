package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface RevengStrategyWrapper extends Wrapper {

	default void setSettings(RevengSettings revengSettings) { ((RevengStrategy)getWrappedObject()).setSettings(revengSettings); }

}
