package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.orm.jbt.api.RevengSettingsWrapper;

public class RevengSettingsWrapperFactory {

	public static RevengSettingsWrapper createRevengSettingsWrapper(RevengSettings wrappedRevengSettings) {
		return new RevengSettingsWrapper() {
			@Override public RevengSettings getWrappedObject() { return wrappedRevengSettings; }
		};
	}

}
