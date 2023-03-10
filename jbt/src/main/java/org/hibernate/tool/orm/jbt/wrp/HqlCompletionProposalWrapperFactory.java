package org.hibernate.tool.orm.jbt.wrp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;

public class HqlCompletionProposalWrapperFactory {

	public static HqlCompletionProposalWrapper createHqlCompletionProposalWrapper(
			HQLCompletionProposal hqlCompletionProposalTarget) {
		return (HqlCompletionProposalWrapper)Proxy.newProxyInstance(
				HqlCompletionProposalWrapperFactory.class.getClassLoader(), 
				new Class[] { HqlCompletionProposalWrapper.class }, 
				new HqlCompletionProposalWrapperInvocationHandler(hqlCompletionProposalTarget));
	}

	static class HqlCompletionProposalWrapperInvocationHandler implements InvocationHandler {
		
		private HQLCompletionProposal target = null;

		public HqlCompletionProposalWrapperInvocationHandler(HQLCompletionProposal target) {
			this.target = target;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				return method.invoke(target, args);
			} catch (InvocationTargetException t) {
				throw t.getTargetException();
			}
		}	
		
	}
	
}
