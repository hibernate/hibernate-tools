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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void completionFailure(String errorMessage) {
		// TODO Auto-generated method stub
		
	}

}
