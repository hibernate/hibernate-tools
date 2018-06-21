/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.hbmlint.SchemaAnalyzer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.mapping.Table;
import org.hibernate.tool.internal.export.lint.Issue;
import org.hibernate.tool.internal.export.lint.IssueCollector;
import org.hibernate.tool.internal.export.lint.SchemaByMetaDataDetector;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author koen
 */
public class TestCase {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	@Test
	public void testSchemaAnalyzer() {

		MetadataSources metadataSources = new MetadataSources();
		metadataSources.addResource("org/hibernate/tool/hbmlint/SchemaAnalyzer/SchemaIssues.hbm.xml");
		Metadata metadata = metadataSources.buildMetadata();
		SchemaByMetaDataDetector analyzer = new SchemaByMetaDataDetector();
		analyzer.initialize( metadata );
		
		
		Iterator<Table> tableMappings = metadata.collectTableMappings().iterator();
		
		while ( tableMappings.hasNext() ) {
			Table table = tableMappings.next();
		
			MockCollector mc = new MockCollector();
			
			if(table.getName().equalsIgnoreCase( "MISSING_TABLE" )) {
				analyzer.visit(table, mc );				
				Assert.assertEquals(mc.problems.size(),1);
				Issue ap = (Issue) mc.problems.get( 0 );
				Assert.assertTrue(ap.getDescription().indexOf( "Missing table" ) >=0);
			} else if(table.getName().equalsIgnoreCase( "CATEGORY" )) {
				analyzer.visit(table, mc );
				Assert.assertEquals(mc.problems.size(),1);
				Issue ap = (Issue) mc.problems.get( 0 );
				Assert.assertTrue(ap.getDescription().indexOf( "missing column: name" ) >=0);							
			} else if(table.getName().equalsIgnoreCase( "BAD_TYPE" )) {
				analyzer.visit(table, mc );
				Assert.assertEquals(mc.problems.size(),1);
				Issue ap = (Issue) mc.problems.get( 0 );
				Assert.assertTrue(ap.getDescription().indexOf( "wrong column type for name" ) >=0);
			}
		}
		
		MockCollector mc = new MockCollector();
		analyzer.visitGenerators(mc);
		Assert.assertEquals(1,mc.problems.size());
		Issue issue = (Issue) mc.problems.get( 0 );
		Assert.assertTrue(issue.getDescription().indexOf( "does_not_exist" ) >=0);		

	}
			
	static class MockCollector implements IssueCollector {
		List<Issue> problems = new ArrayList<Issue>();		
		public void reportIssue(Issue analyze) {			
			problems.add(analyze);
		}		
	}		

}
