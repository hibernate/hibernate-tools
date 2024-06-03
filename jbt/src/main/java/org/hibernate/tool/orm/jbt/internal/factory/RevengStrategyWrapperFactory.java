package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.orm.jbt.api.RevengSettingsWrapper;
import org.hibernate.tool.orm.jbt.api.RevengStrategyWrapper;

public class RevengStrategyWrapperFactory {

	public static RevengStrategyWrapper createRevengStrategyWrapper(RevengStrategy wrappedRevengStrategy) {
		return new RevengStrategyWrapperImpl(wrappedRevengStrategy);
	}
	
	private static class RevengStrategyWrapperImpl implements RevengStrategyWrapper {
		
		private RevengStrategy revengStrategy = null;
		
		private RevengStrategyWrapperImpl(RevengStrategy revengStrategy) {
			this.revengStrategy = revengStrategy;
		}
		
		@Override 
		public RevengStrategy getWrappedObject() { 
			return revengStrategy; 
		}
		
		@Override
		public void setSettings(RevengSettingsWrapper revengSettings) { 
			revengStrategy.setSettings((RevengSettings)revengSettings.getWrappedObject()); 
		}
		
	}

}
