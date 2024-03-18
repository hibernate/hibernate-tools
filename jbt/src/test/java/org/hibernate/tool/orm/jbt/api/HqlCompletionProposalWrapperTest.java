package org.hibernate.tool.orm.jbt.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.Property;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.orm.jbt.internal.factory.HqlCompletionProposalWrapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HqlCompletionProposalWrapperTest {

	private HqlCompletionProposalWrapper hqlCompletionProposalWrapper = null;
	private HQLCompletionProposal wrappedHqlCompletionProposal = null;
	
	@BeforeEach
	public void beforeEach() {
		wrappedHqlCompletionProposal = new HQLCompletionProposal(
				HQLCompletionProposal.PROPERTY, 
				Integer.MAX_VALUE);
		hqlCompletionProposalWrapper = HqlCompletionProposalWrapperFactory
				.createHqlCompletionProposalWrapper(
						wrappedHqlCompletionProposal);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(wrappedHqlCompletionProposal);
		assertNotNull(hqlCompletionProposalWrapper);
	}
	
	@Test
	public void testGetCompletion() {
		assertNotEquals("foo", hqlCompletionProposalWrapper.getCompletion());
		wrappedHqlCompletionProposal.setCompletion("foo");
		assertEquals("foo", hqlCompletionProposalWrapper.getCompletion());
	}
	
	@Test
	public void testGetReplaceStart() {
		assertNotEquals(Integer.MAX_VALUE, hqlCompletionProposalWrapper.getReplaceStart());
		wrappedHqlCompletionProposal.setReplaceStart(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, hqlCompletionProposalWrapper.getReplaceStart());
	}
	
	@Test
	public void testGetReplaceEnd() {
		assertNotEquals(Integer.MIN_VALUE, hqlCompletionProposalWrapper.getReplaceEnd());
		wrappedHqlCompletionProposal.setReplaceEnd(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, hqlCompletionProposalWrapper.getReplaceEnd());
	}
	
	@Test
	public void testGetSimpleName() {
		assertNotEquals("foo", hqlCompletionProposalWrapper.getSimpleName());
		wrappedHqlCompletionProposal.setSimpleName("foo");
		assertEquals("foo", hqlCompletionProposalWrapper.getSimpleName());
	}
	
	@Test
	public void testGetCompletionKind() {
		assertEquals(HQLCompletionProposal.PROPERTY, hqlCompletionProposalWrapper.getCompletionKind());
		wrappedHqlCompletionProposal.setCompletionKind(HQLCompletionProposal.KEYWORD);
		assertEquals(HQLCompletionProposal.KEYWORD, hqlCompletionProposalWrapper.getCompletionKind());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("foo", hqlCompletionProposalWrapper.getEntityName());
		wrappedHqlCompletionProposal.setEntityName("foo");
		assertEquals("foo", hqlCompletionProposalWrapper.getEntityName());
	}
	
	@Test
	public void testGetShortEntityName() {
		assertNotEquals("foo", hqlCompletionProposalWrapper.getShortEntityName());
		wrappedHqlCompletionProposal.setShortEntityName("foo");
		assertEquals("foo", hqlCompletionProposalWrapper.getShortEntityName());
	}
	
	@Test
	public void testGetProperty() {
		Property propertyTarget = new Property();
		assertNull(hqlCompletionProposalWrapper.getProperty());
		wrappedHqlCompletionProposal.setProperty(propertyTarget);
		assertSame(propertyTarget, hqlCompletionProposalWrapper.getProperty());
	}
	
	@Test
	public void testAliasRefKind() {
		assertSame(HQLCompletionProposal.ALIAS_REF, hqlCompletionProposalWrapper.aliasRefKind());
	}
	
	@Test
	public void testEntityNameKind() {
		assertSame(HQLCompletionProposal.ENTITY_NAME, hqlCompletionProposalWrapper.entityNameKind());
	}
	
	@Test
	public void testPropertyKind() {
		assertSame(HQLCompletionProposal.PROPERTY, hqlCompletionProposalWrapper.propertyKind());
	}
	
	@Test
	public void testKeywordKind() {
		assertSame(HQLCompletionProposal.KEYWORD, hqlCompletionProposalWrapper.keywordKind());
	}
	
}
