/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2020 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.tool.ide.completion.ModelCompletion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.ide.completion.ConfigurationCompletion;
import org.hibernate.tool.ide.completion.EntityNameReference;
import org.hibernate.tool.ide.completion.HQLAnalyzer;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.ide.completion.IHQLCodeAssist;
import org.hibernate.tool.ide.completion.IHQLCompletionRequestor;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author leon
 * @author koen
 */
public class TestCase {

    private final class Collector implements IHQLCompletionRequestor {
		private List<HQLCompletionProposal> proposals = new ArrayList<HQLCompletionProposal>();
		
		public void clear() {
			proposals.clear();
		}

		public HQLCompletionProposal[] getCompletionProposals() {
			Collections.sort( proposals, new Comparator<HQLCompletionProposal>() {
				public int compare(HQLCompletionProposal o1, HQLCompletionProposal o2) {
					return o1.getSimpleName().compareTo( o2.getSimpleName() );
				}
			});
			return (HQLCompletionProposal[]) proposals.toArray(new HQLCompletionProposal[proposals.size()]);			
		}

		public boolean accept(HQLCompletionProposal proposal) {
			proposals.add(proposal);
			return true;
		}

		public void completionFailure(String errorMessage) {}
	}
    
	@TempDir
	public static File outputFolder = new File("output");
	

	private Metadata metadata;
	private ConfigurationCompletion cc;
	
	private ClassLoader originalClassLoader = null;
	
	@BeforeAll
	public static void beforeClass() throws Exception {
		File originFolder = 
			new File(TestCase.class
				.getClassLoader()
				.getResource("org/hibernate/tool/ide/completion/ModelCompletion/resources")
				.toURI())
				.getParentFile();
		File destinationFolder = new File(
				outputFolder, 
				"org/hibernate/tool/ide/completion/ModelCompletion");
		destinationFolder.mkdirs();
		for (File f : originFolder.listFiles()) {
			String fileName = f.getName();
			if (fileName.endsWith(".java") || fileName.endsWith(".hbm.xml")) {
				Files.copy(
						f.toPath(), 
						new File(destinationFolder, f.getName()).toPath());
			}
		}
		JavaUtil.compile(outputFolder);
	}
	
    @BeforeEach
    public void setUp() throws Exception {
    		originalClassLoader = Thread.currentThread().getContextClassLoader();
    		Thread.currentThread().setContextClassLoader(
    			new URLClassLoader(
    					new URL[] { outputFolder.toURI().toURL() }, 
    					originalClassLoader));
        metadata = buildMetadata();        
        cc = new ConfigurationCompletion(metadata);
    }
    
    @AfterEach
    public void tearDown() throws Exception {
    		Thread.currentThread().setContextClassLoader(originalClassLoader);
    }
    
    @Test
    public void testGetMappedClasses() {
    		Collector hcc = new Collector();
    		cc.getMatchingImports("", hcc);
    		assertEquals(11, hcc.getCompletionProposals().length, "Invalid entity names count");
        
    		hcc.clear();
        cc.getMatchingImports( " ", hcc );
        assertTrue(hcc.getCompletionProposals().length==0, "Space prefix should have no classes");
        
        hcc.clear();
        cc.getMatchingImports( "pro", hcc );
        assertTrue(hcc.getCompletionProposals().length==2, "Completion should not be case sensitive");
        
        hcc.clear();
        cc.getMatchingImports( "StoreC", hcc );
        assertEquals(1, hcc.getCompletionProposals().length, "Invalid entity names count");
        assertEquals("StoreCity", hcc.getCompletionProposals()[0].getSimpleName(), "StoreCity should have been found");
      
        hcc.clear();
        cc.getMatchingImports( "NotThere", hcc );        
        assertTrue(hcc.getCompletionProposals().length==0);
        
        hcc.clear();
        cc.getMatchingImports( "Uni", hcc );        
        assertEquals(hcc.getCompletionProposals()[0].getSimpleName(), "Universe");
        
        
    }

    @Test
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

    @Test
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
    
    @Test
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

    @Test
    public void testUnmappedClassFields() {
    		Collector hcc = new Collector();
    	
    		cc.getMatchingProperties( "UnmappedClass", "", hcc );    	
        doTestFields(hcc.getCompletionProposals(), new String[0]);
    }

    private void doTestFields(HQLCompletionProposal[] proposals, String[] fields) {
        if (fields == null || fields.length==0) {
        	assertTrue(proposals.length==0, "No fields should have been found");
            return;
        }
        
        assertEquals(fields.length, proposals.length, "Invalid field count");
        for (int j = 0; j < fields.length; j++) {
			String f = fields[j];
			HQLCompletionProposal proposal = proposals[j];
			assertEquals(f, proposal.getSimpleName(), "Invalid field name at " + j);
			assertEquals(proposal.getCompletionKind(), HQLCompletionProposal.PROPERTY, "Invalid kind at " + j);
			
        }
    }
    
    @Test
    public void testProductOwnerAddress() {
        String query = "select p from Product p where p.owner.";
        List<EntityNameReference> visible = getVisibleEntityNames(query.toCharArray());
        
        Collector hcc = new Collector();
    	
    		cc.getMatchingProperties( cc.getCanonicalPath(visible, "p.owner"), "", hcc );    	
        doTestFields(hcc.getCompletionProposals(), new String[] {"address", "firstName", "lastName"});
        
        
        hcc.clear();
        query = "select p from Product p where p.owner.address.";
        visible = getVisibleEntityNames(query.toCharArray());
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "p.owner.address"), "", hcc );
        doTestFields(hcc.getCompletionProposals(), new String[] {"id", "number", "street"});
    }
    
    private List<EntityNameReference> getVisibleEntityNames(char[] cs) {
    		return new HQLAnalyzer().getVisibleEntityNames( cs, cs.length);	
	}

    @Test
	public void testStoreCity() {
        String query = "select p from Product p join p.stores store where store";
        List<EntityNameReference> visible = getVisibleEntityNames(query.toCharArray());
        Collector hcc = new Collector();
    	
        String canonicalPath = cc.getCanonicalPath(visible, "store.city");
		cc.getMatchingProperties( canonicalPath, "", hcc );    	
        doTestFields(hcc.getCompletionProposals(), new String[] {"id", "name", "number"});
    }
    
    @Test
    public void testUnaliasedProductQuery() {
        doTestUnaliasedProductQuery("delete Product where owner.");
        doTestUnaliasedProductQuery("update Product where owner.");
        doTestUnaliasedProductQuery("select from Product where owner.");
    }

    private void doTestUnaliasedProductQuery(final String query) {
        Collector hcc = new Collector();

    	List<EntityNameReference> visible = getVisibleEntityNames(query.toCharArray());
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "f", hcc );

    		HQLCompletionProposal[] completionProposals = hcc.getCompletionProposals();
    		assertEquals(1, completionProposals.length);
    		assertEquals("firstName", completionProposals[0].getSimpleName());
        
        hcc.clear();
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "l", hcc );
        completionProposals = hcc.getCompletionProposals();
        assertEquals(1, completionProposals.length);
        assertEquals("lastName", completionProposals[0].getSimpleName());
        
        hcc.clear();
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "", hcc );
 
        assertEquals(3, hcc.getCompletionProposals().length);

        hcc.clear();
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "g", hcc );
        assertEquals(0, hcc.getCompletionProposals().length);
    }

	// TODO HBX-2063: Investigate and reenable
	@Disabled
    @Test
    public void testBasicFrom() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
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
    
	// TODO HBX-2063: Investigate and reenable
	@Disabled
    @Test
    public void testFromNonWhitespace() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
    		String query = null;
    		int caretPosition = -1;
    		HQLCompletionProposal[] completionProposals = null;
    	
    		c.clear();
    		query = "from Store,| ";
    		caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);    
		completionProposals = c.getCompletionProposals();    	
		assertEquals(11, completionProposals.length, "should get results after a nonwhitespace separator");
    	
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
    
	// TODO HBX-2063: Investigate and reenable
	@Disabled
    @Test
    public void testFromWithTabs() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
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
    
	// TODO HBX-2063: Investigate and reenable
	@Disabled
    @Test
    public void testBasicFromPartialEntityName() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
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
    
	// TODO HBX-2063: Investigate and reenable
	@Disabled
    @Test
    public void testBasicFromPartialDifferentCaseEntityName() {
		Collector c = new Collector();
	
		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
	
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
    
	// TODO HBX-2063: Investigate and reenable
	@Disabled
    @Test
    public void testDottedFromPartialEntityName() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
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
    
    @Test
    public void testBadInputBeforeCursor() {
		Collector c = new Collector();
	
		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
	
		String query = "from org.;hibernate.t| ";
		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);

		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
	
		assertEquals(0, completionProposals.length);
    	    	    	    	
    }
    
	// TODO HBX-2063: Investigate and reenable
	@Disabled
    @Test
    public void testBadInputAfterCursor() {
		Collector c = new Collector();
	
		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
	
		String query = "from org.hibernate.t| ;";
		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
		assertEquals(5, completionProposals.length);
    	    	    	    	
    }
    
    @Test
    public void testAliasRef() {
		Collector c = new Collector();
	
		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
	
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
		assertEquals("org.hibernate.tool.ide.completion.ModelCompletion.Product", proposal.getEntityName());
			
    }
    
    private String getCleanQuery(String query) {
    		return query.replaceAll("\\|", "");
	}

    @Test
	public void testBasicPropertyNames() {
		Collector c = new Collector();
	
		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
	
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
		assertEquals("org.hibernate.tool.ide.completion.ModelCompletion.Product", proposal.getEntityName());
		assertEquals("version", proposal.getPropertyName());
		assertNotNull(proposal.getProperty());
		assertEquals(proposal.getPropertyName(), proposal.getProperty().getName());
	
    		
    }
    
	@Test
	public void testComponentPropertyNames() {
		Collector c = new Collector();
    	
		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
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
    
	// TODO HBX-2063: Investigate and reenable
	@Disabled
	@Test
	public void testInFromAfterEntityAlias() {
		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
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
	
	@Test
	public void testKeywordsFunctions() {
		Collector c = new Collector();

		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);

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
    
    private Metadata buildMetadata() {
     	StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
    		ssrb.applySetting(AvailableSettings.DIALECT, HibernateUtil.Dialect.class.getName());
       	MetadataSources metadataSources = new MetadataSources()
       		.addInputStream(getClass().getResourceAsStream("Product.hbm.xml"))
       		.addInputStream(getClass().getResourceAsStream("Store.hbm.xml"))
       		.addInputStream(getClass().getResourceAsStream("ProductOwnerAddress.hbm.xml"))
       		.addInputStream(getClass().getResourceAsStream("City.hbm.xml"))
       		.addInputStream(getClass().getResourceAsStream("StoreCity.hbm.xml"));
        return metadataSources.buildMetadata(ssrb.build());
    }
    

    
}
