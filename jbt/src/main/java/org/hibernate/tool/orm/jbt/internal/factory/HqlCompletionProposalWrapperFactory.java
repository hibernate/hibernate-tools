package org.hibernate.tool.orm.jbt.internal.factory;

import org.hibernate.mapping.Property;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.orm.jbt.api.HqlCompletionProposalWrapper;

public class HqlCompletionProposalWrapperFactory {

	public static HqlCompletionProposalWrapper createHqlCompletionProposalWrapper(
			final HQLCompletionProposal wrappedCompletionProposal) {
		return new HqlCompletionProposalWrapperImpl(wrappedCompletionProposal);
	}
	
	private static class HqlCompletionProposalWrapperImpl implements HqlCompletionProposalWrapper {
		
		private HQLCompletionProposal hqlCompletionProposal = null;
		
		private HqlCompletionProposalWrapperImpl(HQLCompletionProposal hqlCompletionProposal) {
			this.hqlCompletionProposal = hqlCompletionProposal;
		}
		
		@Override 
		public HQLCompletionProposal getWrappedObject() { return hqlCompletionProposal; }
		
		@Override 
		public String getCompletion() { return hqlCompletionProposal.getCompletion(); }

		@Override 
		public int getReplaceStart() { return hqlCompletionProposal.getReplaceStart(); }

		@Override 
		public int getReplaceEnd() { return hqlCompletionProposal.getReplaceEnd(); }

		@Override 
		public String getSimpleName() { return hqlCompletionProposal.getSimpleName(); }

		@Override 
		public int getCompletionKind() { return hqlCompletionProposal.getCompletionKind(); }

		@Override 
		public String getEntityName() { return hqlCompletionProposal.getEntityName(); }

		@Override 
		public String getShortEntityName() { return hqlCompletionProposal.getShortEntityName(); }

		@Override 
		public Property getProperty() { return hqlCompletionProposal.getProperty(); }

		@Override 
		public int aliasRefKind() { return HQLCompletionProposal.ALIAS_REF; }

		@Override 
		public int entityNameKind() { return HQLCompletionProposal.ENTITY_NAME; }

		@Override 
		public int propertyKind() { return HQLCompletionProposal.PROPERTY; }

		@Override 
		public int keywordKind() { return HQLCompletionProposal.KEYWORD; }

		@Override 
		public int functionKind() { return HQLCompletionProposal.FUNCTION; }

	}

}
