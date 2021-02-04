/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2021 Red Hat, Inc.
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
package org.hibernate.tool.hbmlint.SchemaAnalyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author koen
 */
public class TestCase {

	@TempDir
	public File outputDir = new File("output");
	
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
	}

	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	// TODO HBX-2061: Investigate and reenable failing test below
	@Disabled
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
				assertEquals(mc.problems.size(),1);
				Issue ap = (Issue) mc.problems.get( 0 );
				assertTrue(ap.getDescription().indexOf( "Missing table" ) >=0);
			} else if(table.getName().equalsIgnoreCase( "CATEGORY" )) {
				analyzer.visit(table, mc );
				assertEquals(mc.problems.size(),1);
				Issue ap = (Issue) mc.problems.get( 0 );
				assertTrue(ap.getDescription().indexOf( "missing column: name" ) >=0);							
			} else if(table.getName().equalsIgnoreCase( "BAD_TYPE" )) {
				analyzer.visit(table, mc );
				assertEquals(mc.problems.size(),1);
				Issue ap = (Issue) mc.problems.get( 0 );
				assertTrue(ap.getDescription().indexOf( "wrong column type for name" ) >=0);
			}
		}
		
		MockCollector mc = new MockCollector();
		analyzer.visitGenerators(mc);
		assertEquals(1,mc.problems.size());
		Issue issue = (Issue) mc.problems.get( 0 );
		assertTrue(issue.getDescription().indexOf( "does_not_exist" ) >=0);		

	}
			
	static class MockCollector implements IssueCollector {
		List<Issue> problems = new ArrayList<Issue>();		
		public void reportIssue(Issue analyze) {			
			problems.add(analyze);
		}		
	}		

}
