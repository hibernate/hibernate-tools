package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface RevengStrategyWrapper extends Wrapper {

	void setSettings(RevengSettings revengSettings);

}
