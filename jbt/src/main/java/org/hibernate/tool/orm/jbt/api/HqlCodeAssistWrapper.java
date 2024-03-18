package org.hibernate.tool.orm.jbt.api;

import org.hibernate.tool.orm.jbt.internal.factory.HqlCodeAssistWrapperFactory.HqlCodeAssistExtension;
import org.hibernate.tool.orm.jbt.internal.util.HqlCompletionRequestor;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface HqlCodeAssistWrapper extends Wrapper {
	
	default void codeComplete(String query, int position, Object handler) {
		((HqlCodeAssistExtension)getWrappedObject()).codeComplete(
				query, 
				position, 
				new HqlCompletionRequestor(handler));
	}

}
