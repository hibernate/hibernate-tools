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
package org.hibernate.tool.jdbc2cfg.Versioning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tools.test.util.JdbcUtil;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.RowVersionType;
import org.hibernate.type.TimestampType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * To be extended by VersioningForJDK50Test for the JPA generation part
 * @author max
 * @author koen
 */
public class TestCase {
	
	private Metadata metadata = null;
	private MetadataDescriptor metadataDescriptor = null;
	
	@TempDir
	public File testFolder = new File("test");

	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		metadataDescriptor = MetadataDescriptorFactory
				.createJdbcDescriptor(null, null, true);
		metadata = metadataDescriptor
				.createMetadata();
	}

	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);;
	}

	@Test
	public void testVersion() {		
		PersistentClass cl = metadata.getEntityBinding("WithVersion");		
		Property version = cl.getVersion();
		assertNotNull(version);
		assertEquals("version", version.getName());		
		cl = metadata.getEntityBinding("NoVersion");
		assertNotNull(cl);
		version = cl.getVersion();
		assertNull(version);		
	}
	
	@Test
	public void testGenerateMappings() {
        Exporter exporter = new HibernateMappingExporter();		
        exporter.setMetadataDescriptor(metadataDescriptor);
        exporter.setOutputDirectory(testFolder);
		exporter.start();		
		File[] files = new File[4];		
		files[0] = new File(testFolder, "WithVersion.hbm.xml");
		files[1] = new File(testFolder, "NoVersion.hbm.xml");
		files[2] = new File(testFolder, "WithRealTimestamp.hbm.xml");
		files[3] = new File(testFolder, "WithFakeTimestamp.hbm.xml");		
		Metadata metadata = MetadataDescriptorFactory
				.createNativeDescriptor(null, files, null)
				.createMetadata();
		PersistentClass cl = metadata.getEntityBinding( "WithVersion" );				
		Property version = cl.getVersion();
		assertNotNull(version);
		assertEquals("version", version.getName());	
		cl = metadata.getEntityBinding( "NoVersion" );
		assertNotNull(cl);
		version = cl.getVersion();
		assertNull(version);
		cl = metadata.getEntityBinding( "WithRealTimestamp" );
		assertNotNull(cl);
		version = cl.getVersion();
		assertNotNull(version);
		assertTrue(
				version.getType() instanceof TimestampType || 
				version.getType() instanceof RowVersionType);	// on MS SQL Server
		cl = metadata.getEntityBinding( "WithFakeTimestamp" );
		assertNotNull(cl);
		version = cl.getVersion();
		assertNotNull(version);
		assertTrue(
				version.getType() instanceof IntegerType ||
				version.getType() instanceof BigIntegerType); // on Oracle
	}
    
}
