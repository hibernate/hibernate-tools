package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.orm.jbt.api.HqlCodeAssistWrapper;

public class HqlCodeAssistWrapperFactory {

	public static HqlCodeAssistWrapper createHqlCodeAssistWrapper(Metadata m) {
		final HqlCodeAssistExtension wrappedHqlCodeAssistExtension = new HqlCodeAssistExtension(m);
		return new HqlCodeAssistWrapper() {
			@Override public HQLCodeAssist getWrappedObject() { return wrappedHqlCodeAssistExtension; }
		};
	}
	
	public static class HqlCodeAssistExtension extends HQLCodeAssist {
		public HqlCodeAssistExtension(Metadata m) {
			super(m);
		}
		
	}
	
}
