package org.hibernate.tool.ide.completion;

import java.util.Comparator;

public class HQLCompletionProposalComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		HQLCompletionProposal p1 = (HQLCompletionProposal) o1;
		HQLCompletionProposal p2 = (HQLCompletionProposal) o2;
		return p1.getSimpleName().compareTo( p2.getSimpleName() );
	}

}
