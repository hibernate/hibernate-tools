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
package org.hibernate.tool.jdbc2cfg.TernarySchema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.visitor.DefaultValueVisitor;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	@TempDir
	public File outputFolder = new File("temp");
	
	private MetadataDescriptor metadataDescriptor = null;

	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy() {
			public List<SchemaSelection> getSchemaSelections() {
				List<SchemaSelection> selections = new ArrayList<SchemaSelection>();
				selections.add(new SchemaSelection(null, "HTT"));
				selections.add(new SchemaSelection(null, "OTHERSCHEMA"));
				selections.add(new SchemaSelection(null, "THIRDSCHEMA"));
				return selections;
			}
		};           
	    metadataDescriptor = MetadataDescriptorFactory
	    		.createJdbcDescriptor(c, null, true);
	}

	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	// TODO Investigate the ignored test: HBX-1410
	@Disabled 
	@Test
	public void testTernaryModel() throws SQLException {
		assertMultiSchema(metadataDescriptor.createMetadata());	
	}

	// TODO Investigate the ignored test: HBX-1410
	@Disabled
	@Test
	public void testGeneration() {		
		HibernateMappingExporter hme = new HibernateMappingExporter();
		hme.setMetadataDescriptor(metadataDescriptor);
		hme.setOutputDirectory(outputFolder);
		hme.start();			
		JUnitUtil.assertIsNonEmptyFile( new File(outputFolder, "Role.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile( new File(outputFolder, "User.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile( new File(outputFolder, "Plainrole.hbm.xml") );
		assertEquals(3, outputFolder.listFiles().length);
		File[] files = new File[3];
		files[0] = new File(outputFolder, "Role.hbm.xml");
		files[0] = new File(outputFolder, "User.hbm.xml");
		files[0] = new File(outputFolder, "Plainrole.hbm.xml");
		assertMultiSchema(MetadataDescriptorFactory
				.createNativeDescriptor(null, files, null)
				.createMetadata());
	}
	
	private void assertMultiSchema(Metadata metadata) {
		JUnitUtil.assertIteratorContainsExactly(
				"There should be five tables!", 
				metadata.getEntityBindings().iterator(), 
				5);
		final PersistentClass role = metadata.getEntityBinding("Role");
		assertNotNull(role);
		PersistentClass userroles = metadata.getEntityBinding("Userroles");
		assertNotNull(userroles);
		PersistentClass user = metadata.getEntityBinding("User");
		assertNotNull(user);
		PersistentClass plainRole = metadata.getEntityBinding("Plainrole");
		assertNotNull(plainRole);
		Property property = role.getProperty("users");
		assertEquals(role.getTable().getSchema(), "OTHERSCHEMA");
		assertNotNull(property);
		property.getValue().accept(new DefaultValueVisitor(true) {
			public Object accept(Set o) {
				assertEquals(o.getCollectionTable().getSchema(), "THIRDSCHEMA");
				return null;
			}
		});
		property = plainRole.getProperty("users");
		assertEquals(role.getTable().getSchema(), "OTHERSCHEMA");
		assertNotNull(property);
		property.getValue().accept(new DefaultValueVisitor(true) {
			public Object accept(Set o) {
				assertEquals(o.getCollectionTable().getSchema(), null);
				return null;
			}
		});
	}	
	
}
