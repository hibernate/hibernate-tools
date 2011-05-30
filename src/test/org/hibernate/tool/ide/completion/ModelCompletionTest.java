/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.hibernate.tool.ide.completion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.cfg.Configuration;

/**
 * @author leon
 */
public class ModelCompletionTest extends TestCase {

    private final class Collector implements IHQLCompletionRequestor {
		private List proposals = new ArrayList();
		
		public void clear() {
			proposals.clear();
		}

		public HQLCompletionProposal[] getCompletionProposals() {
			Collections.sort( proposals, new HQLCompletionProposalComparator() );
			return (HQLCompletionProposal[]) proposals.toArray(new HQLCompletionProposal[proposals.size()]);			
		}

		public boolean accept(HQLCompletionProposal proposal) {
			proposals.add(proposal);
			return true;
		}

		public void completionFailure(String errorMessage) {
			// TODO Auto-generated method stub
			
		}
	}

	private Configuration sf;
	private ConfigurationCompletion cc;
    
    public ModelCompletionTest() throws Exception {
        sf = Model.buildConfiguration();        
    }

    protected void setUp() throws Exception {
    	cc = new ConfigurationCompletion(sf);
    }
    public void testGetMappedClasses() {
    	Collector hcc = new Collector();
    	cc.getMatchingImports("", hcc);
    	assertEquals("Invalid entity names count", 11, hcc.getCompletionProposals().length);
        
    	hcc.clear();
        cc.getMatchingImports( " ", hcc );
        assertTrue("Space prefix should have no classes", hcc.getCompletionProposals().length==0);
        
        hcc.clear();
        cc.getMatchingImports( "pro", hcc );
        assertTrue("Completion should not be case sensitive", hcc.getCompletionProposals().length==2);
        
        hcc.clear();
        cc.getMatchingImports( "StoreC", hcc );
        assertEquals("Invalid entity names count", 1, hcc.getCompletionProposals().length);
        assertEquals("StoreCity should have been found", "StoreCity", hcc.getCompletionProposals()[0].getSimpleName());
      
        hcc.clear();
        cc.getMatchingImports( "NotThere", hcc );        
        assertTrue(hcc.getCompletionProposals().length==0);
        
        hcc.clear();
        cc.getMatchingImports( "Uni", hcc );        
        assertEquals("Universe", hcc.getCompletionProposals()[0].getSimpleName());
        
        
    }

    public void testGetProductFields() {
    	Collector hcc = new Collector();
    	
    	cc.getMatchingProperties( "Product", "", hcc );    	
        doTestFields(hcc.getCompletionProposals(), new String[] {"id", "otherOwners", "owner", "price", "stores", "version", "weight" });
        hcc.clear();
        
        cc.getMatchingProperties( "Product", " ", hcc );
        doTestFields(hcc.getCompletionProposals(), new String[] {});
        hcc.clear();
        
        cc.getMatchingProperties( "Product", "v", hcc );
        doTestFields(hcc.getCompletionProposals(), new String[] {"version"});
        hcc.clear();
        
        cc.getMatchingProperties( "Product", "V", hcc );
        doTestFields(hcc.getCompletionProposals(), new String[] {"version"} );        
        hcc.clear();
        
        cc.getMatchingProperties( "Product", "X", hcc );
        doTestFields(hcc.getCompletionProposals(), new String[0] );
    }

    public void testGetStoreFields() {
    	Collector hcc = new Collector();
    	
    	cc.getMatchingProperties( "Store", "", hcc );    	
        doTestFields(hcc.getCompletionProposals(), new String[] {"city", "id", "name", "name2"});
        hcc.clear();
        cc.getMatchingProperties( "Store", "name", hcc );
        doTestFields(hcc.getCompletionProposals(), new String[] {"name", "name2"});
        hcc.clear();
        cc.getMatchingProperties( "Store", "name2", hcc );
        doTestFields(hcc.getCompletionProposals(), new String[] {"name2"});
        hcc.clear();        
    }
    
    public void testKeywordFunction() {
    	Collector hcc = new Collector();
    	cc.getMatchingKeywords( "f", 2, hcc );
    	
    	HQLCompletionProposal[] completionProposals = hcc.getCompletionProposals();
    	
    	assertEquals(4, completionProposals.length);
    	assertEquals("alse", completionProposals[0].getCompletion());
    	
    	hcc.clear();
    	cc.getMatchingFunctions( "ma", 2, hcc );
    	
    	completionProposals = hcc.getCompletionProposals();
    	
    	assertEquals(1, completionProposals.length);
    	assertEquals("x", completionProposals[0].getCompletion());
    	
    	hcc.clear();
    	cc.getMatchingKeywords("FR", 3, hcc);
    	completionProposals = hcc.getCompletionProposals();
    	assertEquals(1, completionProposals.length);
    	
    	hcc.clear();
    	cc.getMatchingFunctions( "MA", 2, hcc );
    	completionProposals = hcc.getCompletionProposals();
    	assertEquals(1, completionProposals.length);
    	
    }

    public void testUnmappedClassFields() {
    	Collector hcc = new Collector();
    	
    	cc.getMatchingProperties( "UnmappedClass", "", hcc );    	
        doTestFields(hcc.getCompletionProposals(), new String[0]);
    }

    private void doTestFields(HQLCompletionProposal[] proposals, String[] fields) {
        if (fields == null || fields.length==0) {
            assertTrue("No fields should have been found", proposals.length==0);
            return;
        }
        
        assertEquals("Invalid field count", fields.length, proposals.length);
        for (int j = 0; j < fields.length; j++) {
			String f = fields[j];
			HQLCompletionProposal proposal = proposals[j];
			assertEquals("Invalid field name at " + j, f, proposal.getSimpleName());
			assertEquals("Invalid kind at " + j, proposal.getCompletionKind(), HQLCompletionProposal.PROPERTY);
			
        }
    }
    
    public void testProductOwnerAddress() {
        String query = "select p from Product p where p.owner.";
        List visible = getVisibleEntityNames(query.toCharArray());
        
    	Collector hcc = new Collector();
    	
    	cc.getMatchingProperties( cc.getCanonicalPath(visible, "p.owner"), "", hcc );    	
        doTestFields(hcc.getCompletionProposals(), new String[] {"address", "firstName", "lastName"});
        
        
        hcc.clear();
        query = "select p from Product p where p.owner.address.";
        visible = getVisibleEntityNames(query.toCharArray());
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "p.owner.address"), "", hcc );
        doTestFields(hcc.getCompletionProposals(), new String[] {"id", "number", "street"});
    }
    
    private List getVisibleEntityNames(char[] cs) {
    	return new HQLAnalyzer().getVisibleEntityNames( cs, cs.length);	
	}

	public void testStoreCity() {
        String query = "select p from Product p join p.stores store where store";
        List visible = getVisibleEntityNames(query.toCharArray());
        Collector hcc = new Collector();
    	
    	String canonicalPath = cc.getCanonicalPath(visible, "store.city");
		cc.getMatchingProperties( canonicalPath, "", hcc );    	
        doTestFields(hcc.getCompletionProposals(), new String[] {"id", "name", "number"});
    }
    
    public void testUnaliasedProductQuery() {
        doTestUnaliasedProductQuery("delete Product where owner.");
        doTestUnaliasedProductQuery("update Product where owner.");
        doTestUnaliasedProductQuery("select from Product where owner.");
    }

    private void doTestUnaliasedProductQuery(final String query) {
        Collector hcc = new Collector();

    	List visible = getVisibleEntityNames(query.toCharArray());
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "f", hcc );

    	HQLCompletionProposal[] completionProposals = hcc.getCompletionProposals();
		//
        assertEquals(1, completionProposals.length);
        assertEquals("firstName", completionProposals[0].getSimpleName());
        //
        hcc.clear();
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "l", hcc );
        completionProposals = hcc.getCompletionProposals();
        assertEquals(1, completionProposals.length);
        assertEquals("lastName", completionProposals[0].getSimpleName());
        //
        hcc.clear();
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "", hcc );
        // firstname, lastname, owner
        assertEquals(3, hcc.getCompletionProposals().length);
        //
        hcc.clear();
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "g", hcc );
        assertEquals(0, hcc.getCompletionProposals().length);
    }

    public void testBasicFrom() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = "from | ";
    	int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
    	HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    	assertEquals(11, completionProposals.length);
    	for (int i = 0; i < completionProposals.length; i++) {
			HQLCompletionProposal proposal = completionProposals[i];
			assertEquals(HQLCompletionProposal.ENTITY_NAME, proposal.getCompletionKind());
			assertEquals(caretPosition, proposal.getCompletionLocation());
			assertEquals(caretPosition, proposal.getReplaceStart());
			assertEquals(proposal.getReplaceStart(), proposal.getReplaceEnd()); // nothing to replace
			assertNotNull(proposal.getShortEntityName());
			assertNotNull(proposal.getEntityName());
			//assertNotNull(proposal.getShortEntityName());
		}
    	
    	c.clear();
    	query = "from Store, | ";
    	caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
    	completionProposals = c.getCompletionProposals();
    	
    	assertEquals(11, completionProposals.length);
    	       	
    }
    
    public void testFromNonWhitespace() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = null;
    	int caretPosition = -1;
    	HQLCompletionProposal[] completionProposals = null;
    	
    	c.clear();
    	query = "from Store,| ";
    	caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);    
    	completionProposals = c.getCompletionProposals();    	
    	assertEquals("should get results after a nonwhitespace separator", 11, completionProposals.length);
    	
    	c.clear();
    	query = "from Store s where ";
    	caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);    
    	completionProposals = c.getCompletionProposals();    	
    	assertTrue(completionProposals.length > 0);
    	
    	c.clear();
    	query = "from Store s where (";
    	caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);    
    	completionProposals = c.getCompletionProposals();    	
    	assertTrue(completionProposals.length > 0);
 	
    }
    
    public void testFromWithTabs() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = null;
    	int caretPosition = -1;
    	HQLCompletionProposal[] completionProposals = null;
    	
    	c.clear();
		final String codeCompletionPlaceMarker = " from ";
		query = "select\t \tt1." + codeCompletionPlaceMarker + "Product t1";
		caretPosition = query.indexOf(codeCompletionPlaceMarker);
		hqlEval.codeComplete(query, caretPosition, c);    
    	completionProposals = c.getCompletionProposals();    	
    	assertTrue(completionProposals.length == 0);

    	c.clear();
		query = query.replace('\t', ' ');
		hqlEval.codeComplete(query, caretPosition, c);    
    	completionProposals = c.getCompletionProposals();    	
    	assertTrue(completionProposals.length > 0);
    }
    
    public void testBasicFromPartialEntityName() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = "from Pro| ";
    	int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
    	HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    	assertEquals(2, completionProposals.length);
    	assertEquals("Product", completionProposals[0].getSimpleName());
    	assertEquals("duct", completionProposals[0].getCompletion());
    	assertEquals("ProductOwnerAddress", completionProposals[1].getSimpleName());
    	assertEquals("ductOwnerAddress", completionProposals[1].getCompletion());
    	for (int i = 0; i < completionProposals.length; i++) {
			HQLCompletionProposal proposal = completionProposals[i];
			assertEquals(HQLCompletionProposal.ENTITY_NAME, proposal.getCompletionKind());
			assertEquals(caretPosition, proposal.getCompletionLocation());
			assertEquals(caretPosition, proposal.getReplaceStart());
			assertEquals(caretPosition, proposal.getReplaceEnd());
		}    	    	    	
    }
    
    public void testBasicFromPartialDifferentCaseEntityName() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = "from pro| ";
    	int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
    	HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    	assertEquals(2, completionProposals.length);
    	assertEquals("Product", completionProposals[0].getSimpleName());
    	assertEquals("Product", completionProposals[0].getCompletion());
    	assertEquals("ProductOwnerAddress", completionProposals[1].getSimpleName());
    	assertEquals("ProductOwnerAddress", completionProposals[1].getCompletion());
    	for (int i = 0; i < completionProposals.length; i++) {
			HQLCompletionProposal proposal = completionProposals[i];
			assertEquals(HQLCompletionProposal.ENTITY_NAME, proposal.getCompletionKind());
			assertEquals(caretPosition, proposal.getCompletionLocation());
			assertEquals(caretPosition-3, proposal.getReplaceStart());
			assertEquals(caretPosition, proposal.getReplaceEnd());
		}    	    	    	
    }
    
    public void testDottedFromPartialEntityName() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = "from org.hibernate.t| ";
    	int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
    	HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    	assertEquals(5, completionProposals.length);
    	for (int i = 0; i < completionProposals.length; i++) {
			HQLCompletionProposal proposal = completionProposals[i];
			assertEquals(HQLCompletionProposal.ENTITY_NAME, proposal.getCompletionKind());
			assertEquals(caretPosition, proposal.getCompletionLocation());
			assertEquals(caretPosition, proposal.getReplaceStart());
			assertEquals(caretPosition, proposal.getReplaceEnd());
			assertTrue(proposal.getCompletion().startsWith( "ool.ide.completion" ));
		}    	    	    	
    }
    
    public void testBadInputBeforeCursor() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = "from org.;hibernate.t| ";
    	int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
    	HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    	assertEquals(0, completionProposals.length);
    	    	    	    	
    }
    
    public void testBadInputAfterCursor() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = "from org.hibernate.t| ;";
    	int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
    	HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    	assertEquals(5, completionProposals.length);
    	    	    	    	
    }
    
    public void testAliasRef() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = "from Product as pr_od where pr_|";
    	int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
    	HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    	assertEquals(1, completionProposals.length);
    	HQLCompletionProposal proposal = completionProposals[0];
		assertEquals( "od", proposal.getCompletion());
    	assertEquals(HQLCompletionProposal.ALIAS_REF, proposal.getCompletionKind());
		assertEquals(caretPosition, proposal.getCompletionLocation());
		assertEquals(caretPosition, proposal.getReplaceStart());
		assertEquals(caretPosition, proposal.getReplaceEnd());
		assertEquals("org.hibernate.tool.ide.completion.Product", proposal.getEntityName());
			
    }
    
    private String getCleanQuery(String query) {
    	return query.replaceAll("\\|", "");
	}

	public void testBasicPropertyNames() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = "from Product as p where p.v|"; //TODO: is non-aliased references allowed ?
    	int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
    	HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    	assertEquals(1, completionProposals.length);
    	HQLCompletionProposal proposal = completionProposals[0];
		assertEquals( "ersion", proposal.getCompletion());
    	assertEquals(HQLCompletionProposal.PROPERTY, proposal.getCompletionKind());
		assertEquals(caretPosition, proposal.getCompletionLocation());
		assertEquals(caretPosition, proposal.getReplaceStart());
		assertEquals(caretPosition, proposal.getReplaceEnd());
		//TODO: keep a path/context assertEquals("Product", proposal.getShortEntityName());
		assertEquals("org.hibernate.tool.ide.completion.Product", proposal.getEntityName());
		assertEquals("version", proposal.getPropertyName());
		assertNotNull(proposal.getProperty());
		assertEquals(proposal.getPropertyName(), proposal.getProperty().getName());
	
    		
    }
    
	public void testComponentPropertyNames() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = "from Product as p where p.owner.|"; 
    	int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
    	HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    	assertEquals(3, completionProposals.length);
    	assertNotNull(completionProposals[0]);
		
    	c.clear();
    	
    	query = "from Product as p where p.owner.address."; 
    	caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
    	completionProposals = c.getCompletionProposals();
    	
    	assertEquals(3, completionProposals.length);
    	
    	c.clear();
    	
    	query = "from Product as p join p.otherOwners o where o."; 
    	caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
    	completionProposals = c.getCompletionProposals();
    	
    	assertEquals(3, completionProposals.length);
    
    	
    }
    
	
	public void testInFromAfterEntityAlias() {
    	Collector c = new Collector();
    	
    	IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);
    	
    	String query = "from Product as p, | where p.v"; 
    	int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
    	HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    	assertEquals(11, completionProposals.length);
    	HQLCompletionProposal proposal = completionProposals[0];
    	assertEquals(HQLCompletionProposal.ENTITY_NAME, proposal.getCompletionKind());
    	
		assertEquals(caretPosition, proposal.getCompletionLocation());
		assertEquals(caretPosition, proposal.getReplaceStart());
		assertEquals(caretPosition, proposal.getReplaceEnd());
	
	}
	
	public void testKeywordsFunctions() {
		Collector c = new Collector();

		IHQLCodeAssist hqlEval = new HQLCodeAssist(sf);

		String query = ""; 
		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);

		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();

		assertTrue(completionProposals.length>0);
		for (int i = 0; i < completionProposals.length; i++) {
			HQLCompletionProposal proposal = completionProposals[i];
			assertTrue(HQLCompletionProposal.KEYWORD==proposal.getCompletionKind() || HQLCompletionProposal.FUNCTION==proposal.getCompletionKind());
			assertEquals(caretPosition, proposal.getCompletionLocation());
			assertEquals(caretPosition, proposal.getReplaceStart());
			assertEquals(caretPosition, proposal.getReplaceEnd());
		}
	}
    
    protected int getCaretPosition(String str) {
        int indexOf = str.indexOf("|");
		return indexOf!=-1?indexOf:str.length();
    }
    
}
