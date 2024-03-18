package org.hibernate.tool.orm.jbt.internal.util;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.ide.completion.IHQLCompletionRequestor;

public class HqlCompletionRequestor implements IHQLCompletionRequestor {
	
	private Object handler = null;
	
	public HqlCompletionRequestor(Object handler) {
		this.handler = handler;
	}

	@Override
	public boolean accept(HQLCompletionProposal proposal) {
		try {
			return (boolean)handler
					.getClass()
					.getMethod("accept", new Class[] { Object.class })
					.invoke(handler, proposal);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public void completionFailure(String errorMessage) {
		try {
			handler.getClass()
					.getMethod("completionFailure", new Class[] { String.class })
					.invoke(handler, errorMessage);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
