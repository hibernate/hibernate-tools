package org.hibernate.tool.orm.jbt.internal.util;

import java.lang.reflect.Method;

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
			Method m = handler
					.getClass()
					.getMethod("accept", new Class[] { Object.class });
			m.setAccessible(true);
			return (boolean)m.invoke(handler, proposal);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public void completionFailure(String errorMessage) {
		try {
			Method m = handler
					.getClass()
					.getMethod("completionFailure", new Class[] { String.class });
			m.setAccessible(true);
			m.invoke(handler, errorMessage);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
