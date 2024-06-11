package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.orm.jbt.api.wrp.RevengSettingsWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.RevengStrategyWrapper;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;

public class RevengSettingsWrapperFactory {
	
	public static RevengSettingsWrapper createRevengSettingsWrapper(RevengStrategyWrapper revengStrategyWrapper) {
		RevengStrategy revengStrategy = 
				revengStrategyWrapper == null ? 
						null : (RevengStrategy)revengStrategyWrapper.getWrappedObject();
		RevengSettings wrappedRevengSettings = new RevengSettings(revengStrategy);
		return createRevengSettingsWrapper(wrappedRevengSettings);
	}

	static RevengSettingsWrapper createRevengSettingsWrapper(RevengSettings wrappedRevengSettings) {
		return new RevengSettingsWrapperImpl(wrappedRevengSettings);
	}
	
	private static class RevengSettingsWrapperImpl 
			extends AbstractWrapper
			implements RevengSettingsWrapper {
		
		private RevengSettings revengSettings = null;
		
		private RevengSettingsWrapperImpl(RevengSettings revengSettings) {
			this.revengSettings = revengSettings;
		}
		
		@Override 
		public RevengSettings getWrappedObject() { 
			return revengSettings; 
		}
		
		@Override 
		public RevengSettingsWrapper setDefaultPackageName(String s) { 
			revengSettings.setDefaultPackageName(s); 
			return this;
		}
		
		@Override 
		public RevengSettingsWrapper setDetectManyToMany(boolean b) { 
			revengSettings.setDetectManyToMany(b); 
			return this;
		}
		
		@Override 
		public RevengSettingsWrapper setDetectOneToOne(boolean b) { 
			revengSettings.setDetectOneToOne(b); 
			return this;
		}
		
		@Override 
		public RevengSettingsWrapper setDetectOptimisticLock(boolean b) { 
			revengSettings.setDetectOptimisticLock(b); 
			return this;
		}

	}

}
