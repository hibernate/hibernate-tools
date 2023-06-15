package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.Method;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.ide.completion.IHQLCompletionRequestor;

public class HqlCodeAssistWrapper extends HQLCodeAssist {

	public HqlCodeAssistWrapper(Metadata metadata) {
		super(metadata);
	}
	
	public void codeComplete(String query, int position, Object handler) {
		super.codeComplete(query, position, new HqlCompletionRequestorAdapter(handler));;
	}
	
	private static class HqlCompletionRequestorAdapter implements IHQLCompletionRequestor {
		
		private Object handler = null;
		private Method acceptMethod = null;
		private Method completionFailureMethod = null;
		
		private HqlCompletionRequestorAdapter(Object handler) {
			this.handler = handler;
			acceptMethod = lookupMethod("accept", Object.class);
			completionFailureMethod = lookupMethod("completionFailure", String.class);
		}
		
		@Override
		public boolean accept(HQLCompletionProposal proposal) {
			try {
				return (boolean)acceptMethod.invoke(handler, proposal);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}

		@Override
		public void completionFailure(String errorMessage) {
			try {
				completionFailureMethod.invoke(handler, errorMessage);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
		
		private Method lookupMethod(String name, Class<?> argumentClass) {
			Method result = null;
			try {
				result = handler.getClass().getMethod(name, new Class[] { argumentClass });
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
			return result;
		}
		
	}

}
