/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.Hbm2JavaInitializationTest;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.internal.export.pojo.Cfg2JavaTool;
import org.hibernate.tool.internal.export.pojo.ImportContextImpl;
import org.hibernate.tools.test.util.HibernateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"Author.hbm.xml",
			"Article.hbm.xml",
			"Train.hbm.xml",
			"Passenger.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Metadata metadata = null;
	
	@Before
	public void setUp() throws Exception {
		metadata = HibernateUtil
				.initializeMetadataDescriptor(
						this, 
						HBM_XML_FILES, 
						temporaryFolder.getRoot())
				.createMetadata();
	}
	
	@Test
	public void testFieldInitializationAndTypeNames() {
		PersistentClass classMapping = metadata.getEntityBinding("org.hibernate.tool.hbm2x.Hbm2JavaInitializationTest.Article");
		Cfg2JavaTool cfg2java = new Cfg2JavaTool();
		POJOClass clazz = cfg2java.getPOJOClass(classMapping);
		Property p = classMapping.getProperty("AMap");
		Assert.assertEquals("all types should be fully qualified when no importcontext","java.util.Map<java.lang.String,org.hibernate.tool.hbm2x.Hbm2JavaInitializationTest.Article>",cfg2java.getJavaTypeName(p, true));
		Assert.assertEquals("Map<String,Article>",cfg2java.getJavaTypeName(p, true, clazz));		
		Assert.assertEquals("new HashMap<String,Article>(0)", clazz.getFieldInitialization(p, true));
		Assert.assertEquals("new HashMap(0)", clazz.getFieldInitialization(p, false));
		p = classMapping.getProperty("aList");
		Assert.assertEquals("lists should not have the index visible in the declaration", "List<Article>",cfg2java.getJavaTypeName(p, true, clazz));
		Assert.assertEquals("all types should be fully qualified when no importcontext","java.util.List<org.hibernate.tool.hbm2x.Hbm2JavaInitializationTest.Article>",cfg2java.getJavaTypeName(p, true));
		Assert.assertEquals("new ArrayList<Article>(0)", clazz.getFieldInitialization(p, true));
		Assert.assertEquals("new ArrayList(0)", clazz.getFieldInitialization(p, false));
		p = classMapping.getProperty("content");
		Assert.assertEquals("\"what can I say\"",clazz.getFieldInitialization(p, false));
		p = classMapping.getProperty("bagarticles");
		Assert.assertEquals("Should be a list via property-type", "java.util.List", cfg2java.getJavaTypeName( p, false ));
		Assert.assertEquals("Should be a a generic'd list when generics=true", "java.util.List<org.hibernate.tool.hbm2x.Hbm2JavaInitializationTest.Article>", cfg2java.getJavaTypeName( p, true ));
		Assert.assertEquals("List<Article>",cfg2java.getJavaTypeName(p, true, clazz));		
		Assert.assertEquals("new ArrayList<Article>(0)", clazz.getFieldInitialization(p, true));
		Assert.assertEquals("new ArrayList(0)", clazz.getFieldInitialization(p, false));
		p = classMapping.getProperty("bagstrings");
		Assert.assertEquals("Bag's are just a collection", "java.util.Collection", cfg2java.getJavaTypeName( p, false ));
		Assert.assertEquals("Should be a a generic'd collection when generics=true", "java.util.Collection<java.lang.String>", cfg2java.getJavaTypeName( p, true ));
		Assert.assertEquals("Collection<String>",cfg2java.getJavaTypeName(p, true, clazz));		
		Assert.assertEquals("new ArrayList<String>(0)", clazz.getFieldInitialization(p, true));
		Assert.assertEquals("new ArrayList(0)", clazz.getFieldInitialization(p, false));
		p = classMapping.getProperty("bagstrings");
		Assert.assertEquals("new ArrayList(0)", clazz.getFieldInitialization(p, false));
		p = classMapping.getProperty("naturalSortedArticlesMap");
		Assert.assertEquals("java.util.SortedMap", cfg2java.getJavaTypeName( p, false));
		Assert.assertEquals("SortedMap<String,Article>", cfg2java.getJavaTypeName( p, true, new ImportContextImpl("") ));
		Assert.assertEquals("new TreeMap<String,Article>()", clazz.getFieldInitialization(p, true));		
		Assert.assertEquals("new TreeMap()", clazz.getFieldInitialization(p, false));
		p = classMapping.getProperty("sortedArticlesMap");
		Assert.assertEquals("java.util.SortedMap", cfg2java.getJavaTypeName( p, false));
		Assert.assertEquals("SortedMap<String,Article>", cfg2java.getJavaTypeName( p, true, new ImportContextImpl("") ));
		Assert.assertFalse(clazz.generateImports().contains("import comparator.NoopComparator;"));
		Assert.assertEquals("new TreeMap(new NoopComparator())", clazz.getFieldInitialization(p, false));
		Assert.assertTrue(clazz.generateImports().contains("import comparator.NoopComparator;"));
		Assert.assertEquals("new TreeMap<String,Article>(new NoopComparator())", clazz.getFieldInitialization(p, true));
		p = classMapping.getProperty("sortedArticlesSet");
		Assert.assertEquals("java.util.SortedSet", cfg2java.getJavaTypeName( p, false));
		Assert.assertEquals("SortedSet<Article>", cfg2java.getJavaTypeName( p, true, new ImportContextImpl("") ));
		Assert.assertEquals("new TreeSet<Article>(new NoopComparator())", clazz.getFieldInitialization(p, true));
	}
	
}
