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
package org.hibernate.tool.hbm2x.query.QueryExporterTest;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.hibernate.tools.test.util.ResourceUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TestCase {

	@TempDir
	public File temporaryFolder = new File("temp");

	private File destinationDir = null;
	private File resourcesDir = null;
	private File userGroupHbmXmlFile = null;
	
	@BeforeEach
	public void setUp() throws Exception {
		JdbcUtil.createDatabase(this);
		destinationDir = new File(temporaryFolder, "destination");
		destinationDir.mkdir();
		resourcesDir = new File(temporaryFolder, "resources");
		resourcesDir.mkdir();
		String[] resources = { "UserGroup.hbm.xml" };		
		ResourceUtil.createResources(this, resources, resourcesDir);
		userGroupHbmXmlFile = new File(resourcesDir, "UserGroup.hbm.xml");
		SessionFactory factory = createMetadata().buildSessionFactory();		
		Session s = factory.openSession();	
		Transaction t = s.beginTransaction();
		User user = new User("max", "jboss");
		s.persist( user );		
		user = new User("gavin", "jboss");
		s.persist( user );		
		s.flush();		
		t.commit();
		s.close();
		factory.close();		
	}
	
	@Test
	public void testQueryExporter() throws Exception {		
		QueryExporter exporter = new QueryExporter();
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(
						null, 
						new File[] { userGroupHbmXmlFile }, 
						null);
		exporter.getProperties().put(AvailableSettings.HBM2DDL_AUTO, "update");
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(destinationDir);
		exporter.setFilename("queryresult.txt");
		List<String> queries = new ArrayList<String>();
		queries.add("from java.lang.Object");
		exporter.setQueries( queries );		
		exporter.start();
		JUnitUtil.assertIsNonEmptyFile(new File(destinationDir, "queryresult.txt"));		
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		SchemaExport export = new SchemaExport();
		final EnumSet<TargetType> targetTypes = EnumSet.noneOf( TargetType.class );
		targetTypes.add( TargetType.DATABASE );
		export.drop(targetTypes, createMetadata());		
		if (export.getExceptions() != null && export.getExceptions().size() > 0){
			fail("Schema export failed");
		}		
		JdbcUtil.dropDatabase(this);
	}
	
	private Metadata createMetadata() {
		Properties properties = new Properties();
		properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(
						null, 
						new File[] { userGroupHbmXmlFile }, 
						properties);
		return metadataDescriptor.createMetadata();
	}
	
}
