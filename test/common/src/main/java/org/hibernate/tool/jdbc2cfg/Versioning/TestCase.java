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

import java.io.File;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
	public File outputFolder = new File("output");
	
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		metadataDescriptor = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(null, null);
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
	
	// TODO HBX-2232: Investigate, fix and reenable failing tests after update to 6.0.0.Beta1
	@Disabled
	@Test
	public void testGenerateMappings() {
        Exporter exporter = new HbmExporter();		
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputFolder);
 		exporter.start();		
		File[] files = new File[4];		
		files[0] = new File(outputFolder, "WithVersion.hbm.xml");
		files[1] = new File(outputFolder, "NoVersion.hbm.xml");
		files[2] = new File(outputFolder, "WithRealTimestamp.hbm.xml");
		files[3] = new File(outputFolder, "WithFakeTimestamp.hbm.xml");		
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
// Following line was commented out after updating to 6.0.0.Beta2 because of compilation errors
//		assertTrue(
//				version.getType(). instanceof StandardBasicTypes.TIMESTAMP || 
//				version.getType() instanceof BinaryType);	// (for MS SQL Server) TODO verify: it used to be RowVersionType but since 6.0 BinaryTypee
		cl = metadata.getEntityBinding( "WithFakeTimestamp" );
		assertNotNull(cl);
		version = cl.getVersion();
		assertNotNull(version);
// Following line was commented out after updating to 6.0.0.Beta2 because of compilation errors
//		assertTrue(
//				version.getType() instanceof IntegerType ||
//				version.getType() instanceof BigDecimalType); // on Oracle
	}
    
}
