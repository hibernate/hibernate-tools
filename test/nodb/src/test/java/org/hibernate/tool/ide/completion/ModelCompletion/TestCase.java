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

package org.hibernate.tool.ide.completion.ModelCompletion;

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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
    
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Metadata metadata;
	private ConfigurationCompletion cc;
	
	private ClassLoader originalClassLoader = null;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		File folder = temporaryFolder.getRoot();
		File originFolder = 
			new File(TestCase.class
				.getClassLoader()
				.getResource("org/hibernate/tool/ide/completion/ModelCompletion/resources")
				.toURI())
				.getParentFile();
		File destinationFolder = new File(
				folder, 
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
		JavaUtil.compile(temporaryFolder.getRoot());
	}
	
    @Before
    public void setUp() throws Exception {
    		originalClassLoader = Thread.currentThread().getContextClassLoader();
    		Thread.currentThread().setContextClassLoader(
    			new URLClassLoader(
    					new URL[] { temporaryFolder.getRoot().toURI().toURL() }, 
    					originalClassLoader));
        metadata = buildMetadata();        
        cc = new ConfigurationCompletion(metadata);
    }
    
    @After
    public void tearDown() throws Exception {
    		Thread.currentThread().setContextClassLoader(originalClassLoader);
    }
    
    @Test
    public void testGetMappedClasses() {
    		Collector hcc = new Collector();
    		cc.getMatchingImports("", hcc);
    		Assert.assertEquals("Invalid entity names count", 11, hcc.getCompletionProposals().length);
        
    		hcc.clear();
        cc.getMatchingImports( " ", hcc );
        Assert.assertTrue("Space prefix should have no classes", hcc.getCompletionProposals().length==0);
        
        hcc.clear();
        cc.getMatchingImports( "pro", hcc );
        Assert.assertTrue("Completion should not be case sensitive", hcc.getCompletionProposals().length==2);
        
        hcc.clear();
        cc.getMatchingImports( "StoreC", hcc );
        Assert.assertEquals("Invalid entity names count", 1, hcc.getCompletionProposals().length);
        Assert.assertEquals("StoreCity should have been found", "StoreCity", hcc.getCompletionProposals()[0].getSimpleName());
      
        hcc.clear();
        cc.getMatchingImports( "NotThere", hcc );        
        Assert.assertTrue(hcc.getCompletionProposals().length==0);
        
        hcc.clear();
        cc.getMatchingImports( "Uni", hcc );        
        Assert.assertEquals("Universe", hcc.getCompletionProposals()[0].getSimpleName());
        
        
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
    	
    		Assert.assertEquals(4, completionProposals.length);
    		Assert.assertEquals("alse", completionProposals[0].getCompletion());
    	
    		hcc.clear();
    		cc.getMatchingFunctions( "ma", 2, hcc );
    	
    		completionProposals = hcc.getCompletionProposals();
    	
    		Assert.assertEquals(1, completionProposals.length);
    		Assert.assertEquals("x", completionProposals[0].getCompletion());
    	
    		hcc.clear();
    		cc.getMatchingKeywords("FR", 3, hcc);
    		completionProposals = hcc.getCompletionProposals();
    		Assert.assertEquals(1, completionProposals.length);
    	
    		hcc.clear();
    		cc.getMatchingFunctions( "MA", 2, hcc );
    		completionProposals = hcc.getCompletionProposals();
    		Assert.assertEquals(1, completionProposals.length);
    	
    }

    @Test
    public void testUnmappedClassFields() {
    		Collector hcc = new Collector();
    	
    		cc.getMatchingProperties( "UnmappedClass", "", hcc );    	
        doTestFields(hcc.getCompletionProposals(), new String[0]);
    }

    private void doTestFields(HQLCompletionProposal[] proposals, String[] fields) {
        if (fields == null || fields.length==0) {
        		Assert.assertTrue("No fields should have been found", proposals.length==0);
            return;
        }
        
        Assert.assertEquals("Invalid field count", fields.length, proposals.length);
        for (int j = 0; j < fields.length; j++) {
			String f = fields[j];
			HQLCompletionProposal proposal = proposals[j];
			Assert.assertEquals("Invalid field name at " + j, f, proposal.getSimpleName());
			Assert.assertEquals("Invalid kind at " + j, proposal.getCompletionKind(), HQLCompletionProposal.PROPERTY);
			
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
    		Assert.assertEquals(1, completionProposals.length);
    		Assert.assertEquals("firstName", completionProposals[0].getSimpleName());
        
        hcc.clear();
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "l", hcc );
        completionProposals = hcc.getCompletionProposals();
        Assert.assertEquals(1, completionProposals.length);
        Assert.assertEquals("lastName", completionProposals[0].getSimpleName());
        
        hcc.clear();
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "", hcc );
 
        Assert.assertEquals(3, hcc.getCompletionProposals().length);

        hcc.clear();
        cc.getMatchingProperties( cc.getCanonicalPath(visible, "owner"), "g", hcc );
        Assert.assertEquals(0, hcc.getCompletionProposals().length);
    }

    @Test
    public void testBasicFrom() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
    		String query = "from | ";
    		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
		Assert.assertEquals(11, completionProposals.length);
    		for (int i = 0; i < completionProposals.length; i++) {
			HQLCompletionProposal proposal = completionProposals[i];
			Assert.assertEquals(HQLCompletionProposal.ENTITY_NAME, proposal.getCompletionKind());
			Assert.assertEquals(caretPosition, proposal.getCompletionLocation());
			Assert.assertEquals(caretPosition, proposal.getReplaceStart());
			Assert.assertEquals(proposal.getReplaceStart(), proposal.getReplaceEnd()); // nothing to replace
			Assert.assertNotNull(proposal.getShortEntityName());
			Assert.assertNotNull(proposal.getEntityName());
			//assertNotNull(proposal.getShortEntityName());
		}
    	
    		c.clear();
    		query = "from Store, | ";
    		caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
		completionProposals = c.getCompletionProposals();
    	
		Assert.assertEquals(11, completionProposals.length);
    	       	
    }
    
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
		Assert.assertEquals("should get results after a nonwhitespace separator", 11, completionProposals.length);
    	
		c.clear();
		query = "from Store s where ";
		caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);    
		completionProposals = c.getCompletionProposals();    	
		Assert.assertTrue(completionProposals.length > 0);
    	
		c.clear();
		query = "from Store s where (";
		caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);    
		completionProposals = c.getCompletionProposals();    	
		Assert.assertTrue(completionProposals.length > 0);
 	
    }
    
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
		Assert.assertTrue(completionProposals.length == 0);

		c.clear();
		query = query.replace('\t', ' ');
		hqlEval.codeComplete(query, caretPosition, c);    
		completionProposals = c.getCompletionProposals();    	
		Assert.assertTrue(completionProposals.length > 0);
    }
    
    @Test
    public void testBasicFromPartialEntityName() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
    		String query = "from Pro| ";
    		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
		Assert.assertEquals(2, completionProposals.length);
		Assert.assertEquals("Product", completionProposals[0].getSimpleName());
		Assert.assertEquals("duct", completionProposals[0].getCompletion());
		Assert.assertEquals("ProductOwnerAddress", completionProposals[1].getSimpleName());
		Assert.assertEquals("ductOwnerAddress", completionProposals[1].getCompletion());
		for (int i = 0; i < completionProposals.length; i++) {
			HQLCompletionProposal proposal = completionProposals[i];
			Assert.assertEquals(HQLCompletionProposal.ENTITY_NAME, proposal.getCompletionKind());
			Assert.assertEquals(caretPosition, proposal.getCompletionLocation());
			Assert.assertEquals(caretPosition, proposal.getReplaceStart());
			Assert.assertEquals(caretPosition, proposal.getReplaceEnd());
		}    	    	    	
    }
    
    @Test
    public void testBasicFromPartialDifferentCaseEntityName() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
    		String query = "from pro| ";
    		int caretPosition = getCaretPosition(query);
    		hqlEval.codeComplete(query, caretPosition, c);
    
    		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    		Assert.assertEquals(2, completionProposals.length);
    		Assert.assertEquals("Product", completionProposals[0].getSimpleName());
    		Assert.assertEquals("Product", completionProposals[0].getCompletion());
    		Assert.assertEquals("ProductOwnerAddress", completionProposals[1].getSimpleName());
    		Assert.assertEquals("ProductOwnerAddress", completionProposals[1].getCompletion());
    		for (int i = 0; i < completionProposals.length; i++) {
			HQLCompletionProposal proposal = completionProposals[i];
			Assert.assertEquals(HQLCompletionProposal.ENTITY_NAME, proposal.getCompletionKind());
			Assert.assertEquals(caretPosition, proposal.getCompletionLocation());
			Assert.assertEquals(caretPosition-3, proposal.getReplaceStart());
			Assert.assertEquals(caretPosition, proposal.getReplaceEnd());
		}    	    	    	
    }
    
    @Test
    public void testDottedFromPartialEntityName() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
    		String query = "from org.hibernate.t| ";
    		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
		Assert.assertEquals(5, completionProposals.length);
		for (int i = 0; i < completionProposals.length; i++) {
			HQLCompletionProposal proposal = completionProposals[i];
			Assert.assertEquals(HQLCompletionProposal.ENTITY_NAME, proposal.getCompletionKind());
			Assert.assertEquals(caretPosition, proposal.getCompletionLocation());
			Assert.assertEquals(caretPosition, proposal.getReplaceStart());
			Assert.assertEquals(caretPosition, proposal.getReplaceEnd());
			Assert.assertTrue(proposal.getCompletion().startsWith( "ool.ide.completion" ));
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
    	
    		Assert.assertEquals(0, completionProposals.length);
    	    	    	    	
    }
    
    @Test
    public void testBadInputAfterCursor() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
    		String query = "from org.hibernate.t| ;";
    		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(query, caretPosition, c);
    
		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
    		Assert.assertEquals(5, completionProposals.length);
    	    	    	    	
    }
    
    @Test
    public void testAliasRef() {
    		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
    		String query = "from Product as pr_od where pr_|";
    		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
		Assert.assertEquals(1, completionProposals.length);
		HQLCompletionProposal proposal = completionProposals[0];
		Assert.assertEquals( "od", proposal.getCompletion());
		Assert.assertEquals(HQLCompletionProposal.ALIAS_REF, proposal.getCompletionKind());
		Assert.assertEquals(caretPosition, proposal.getCompletionLocation());
		Assert.assertEquals(caretPosition, proposal.getReplaceStart());
		Assert.assertEquals(caretPosition, proposal.getReplaceEnd());
		Assert.assertEquals("org.hibernate.tool.ide.completion.ModelCompletion.Product", proposal.getEntityName());
			
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
    	
		Assert.assertEquals(1, completionProposals.length);
		HQLCompletionProposal proposal = completionProposals[0];
		Assert.assertEquals( "ersion", proposal.getCompletion());
		Assert.assertEquals(HQLCompletionProposal.PROPERTY, proposal.getCompletionKind());
		Assert.assertEquals(caretPosition, proposal.getCompletionLocation());
		Assert.assertEquals(caretPosition, proposal.getReplaceStart());
		Assert.assertEquals(caretPosition, proposal.getReplaceEnd());
		//TODO: keep a path/context assertEquals("Product", proposal.getShortEntityName());
		Assert.assertEquals("org.hibernate.tool.ide.completion.ModelCompletion.Product", proposal.getEntityName());
		Assert.assertEquals("version", proposal.getPropertyName());
		Assert.assertNotNull(proposal.getProperty());
		Assert.assertEquals(proposal.getPropertyName(), proposal.getProperty().getName());
	
    		
    }
    
	@Test
	public void testComponentPropertyNames() {
		Collector c = new Collector();
    	
		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
		String query = "from Product as p where p.owner.|"; 
		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
		Assert.assertEquals(3, completionProposals.length);
		Assert.assertNotNull(completionProposals[0]);
		
		c.clear();
    	
		query = "from Product as p where p.owner.address."; 
		caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
		completionProposals = c.getCompletionProposals();
    	
		Assert.assertEquals(3, completionProposals.length);
    	
		c.clear();
    	
    		query = "from Product as p join p.otherOwners o where o."; 
    		caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
		completionProposals = c.getCompletionProposals();
    	
    		Assert.assertEquals(3, completionProposals.length);
    
    	
    }
    
	@Test
	public void testInFromAfterEntityAlias() {
		Collector c = new Collector();
    	
    		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);
    	
    		String query = "from Product as p, | where p.v"; 
    		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);
    
		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();
    	
		Assert.assertEquals(11, completionProposals.length);
		HQLCompletionProposal proposal = completionProposals[0];
		Assert.assertEquals(HQLCompletionProposal.ENTITY_NAME, proposal.getCompletionKind());
    		
		Assert.assertEquals(caretPosition, proposal.getCompletionLocation());
		Assert.assertEquals(caretPosition, proposal.getReplaceStart());
    		Assert.assertEquals(caretPosition, proposal.getReplaceEnd());
	
	}
	
	@Test
	public void testKeywordsFunctions() {
		Collector c = new Collector();

		IHQLCodeAssist hqlEval = new HQLCodeAssist(metadata);

		String query = ""; 
		int caretPosition = getCaretPosition(query);
		hqlEval.codeComplete(getCleanQuery(query), caretPosition, c);

		HQLCompletionProposal[] completionProposals = c.getCompletionProposals();

		Assert.assertTrue(completionProposals.length>0);
		for (int i = 0; i < completionProposals.length; i++) {
			HQLCompletionProposal proposal = completionProposals[i];
			Assert.assertTrue(HQLCompletionProposal.KEYWORD==proposal.getCompletionKind() || HQLCompletionProposal.FUNCTION==proposal.getCompletionKind());
			Assert.assertEquals(caretPosition, proposal.getCompletionLocation());
			Assert.assertEquals(caretPosition, proposal.getReplaceStart());
			Assert.assertEquals(caretPosition, proposal.getReplaceEnd());
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
