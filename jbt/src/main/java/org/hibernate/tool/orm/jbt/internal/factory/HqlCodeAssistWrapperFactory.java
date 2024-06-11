package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.orm.jbt.api.wrp.ConfigurationWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.HqlCodeAssistWrapper;
import org.hibernate.tool.orm.jbt.internal.util.HqlCompletionRequestor;
import org.hibernate.tool.orm.jbt.internal.wrp.AbstractWrapper;
import org.hibernate.tool.orm.jbt.util.MetadataHelper;

public class HqlCodeAssistWrapperFactory {

	public static HqlCodeAssistWrapper createHqlCodeAssistWrapper(ConfigurationWrapper configurationWrapper) {
		return createHqlCodeAssistWrapper(
				MetadataHelper.getMetadata((Configuration)configurationWrapper.getWrappedObject()));
	}
	
	private static HqlCodeAssistWrapper createHqlCodeAssistWrapper(Metadata m) {
		return new HqlCodeAssistWrapperImpl(m);
	}
	
	private static class HqlCodeAssistWrapperImpl 
			extends AbstractWrapper
			implements HqlCodeAssistWrapper {
		
		private HQLCodeAssist hqlCodeAssist = null;
		
		private HqlCodeAssistWrapperImpl(Metadata metadata) {
			this.hqlCodeAssist = new HQLCodeAssist(metadata);
		}
		
		@Override public HQLCodeAssist getWrappedObject() {
			return hqlCodeAssist; 
		}
		
		@Override
		public void codeComplete(String query, int position, Object handler) {
			hqlCodeAssist.codeComplete(
					query, 
					position, 
					new HqlCompletionRequestor(handler));
		}

	}

}
