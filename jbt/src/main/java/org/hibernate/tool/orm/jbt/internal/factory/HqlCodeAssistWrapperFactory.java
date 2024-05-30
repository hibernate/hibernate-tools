package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.orm.jbt.api.HqlCodeAssistWrapper;
import org.hibernate.tool.orm.jbt.internal.util.HqlCompletionRequestor;

public class HqlCodeAssistWrapperFactory {

	public static HqlCodeAssistWrapper createHqlCodeAssistWrapper(Metadata m) {
		return new HqlCodeAssistWrapperImpl(m);
	}
	
	private static class HqlCodeAssistWrapperImpl implements HqlCodeAssistWrapper {
		
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
