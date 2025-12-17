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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author max
 * @author koen
 */
public class TestCase {

    @TempDir
    public File outputFolder = new File("output");

    private MetadataDescriptor metadataDescriptor = null;

    @BeforeEach
    public void setUp() {
        JdbcUtil.createDatabase(this);
        DefaultReverseEngineeringStrategy c = new DefaultReverseEngineeringStrategy() {
            public List<SchemaSelection> getSchemaSelections() {
                List<SchemaSelection> selections = new ArrayList<>();
                selections.add(createSchemaSelection("PUBLIC"));
                selections.add(createSchemaSelection("OTHERSCHEMA"));
                selections.add(createSchemaSelection("THIRDSCHEMA"));
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

    @Test
    public void testTernaryModel() {
        assertMultiSchema(metadataDescriptor.createMetadata());
    }

    @Test
    public void testGeneration() {
        HibernateMappingExporter hme = new HibernateMappingExporter();
        hme.setMetadataDescriptor(metadataDescriptor);
        hme.setOutputDirectory(outputFolder);
        hme.start();
        JUnitUtil.assertIsNonEmptyFile( new File(outputFolder, "Role.hbm.xml") );
        JUnitUtil.assertIsNonEmptyFile( new File(outputFolder, "Member.hbm.xml") );
        JUnitUtil.assertIsNonEmptyFile( new File(outputFolder, "Plainrole.hbm.xml") );
        assertEquals(3, Objects.requireNonNull( outputFolder.listFiles() ).length);
        File[] files = new File[3];
        files[0] = new File(outputFolder, "Role.hbm.xml");
        files[1] = new File(outputFolder, "Member.hbm.xml");
        files[2] = new File(outputFolder, "Plainrole.hbm.xml");
        assertMultiSchema(MetadataDescriptorFactory
                .createNativeDescriptor(null, files, null)
                .createMetadata());
    }

    private void assertMultiSchema(Metadata metadata) {
        JUnitUtil.assertIteratorContainsExactly(
                "There should be three entities!",
                metadata.getEntityBindings().iterator(),
                3);
        final PersistentClass role = metadata.getEntityBinding("Role");
        assertNotNull(role);
        PersistentClass member = metadata.getEntityBinding("Member");
        assertNotNull(member);
        PersistentClass plainRole = metadata.getEntityBinding("Plainrole");
        assertNotNull(plainRole);
        Property property = role.getProperty("members");
        assertEquals( "OTHERSCHEMA", role.getTable().getSchema() );
        assertNotNull(property);
        property.getValue().accept(new DefaultValueVisitor(true) {
            public Object accept(Set o) {
                assertEquals( "THIRDSCHEMA", o.getCollectionTable().getSchema() );
                return null;
            }
        });
        property = plainRole.getProperty("members");
        assertEquals( "OTHERSCHEMA", role.getTable().getSchema() );
        assertNotNull(property);
        property.getValue().accept(new DefaultValueVisitor(true) {
            public Object accept(Set o) {
                assertNull(o.getCollectionTable().getSchema() );
                return null;
            }
        });
    }

    private SchemaSelection createSchemaSelection(String matchSchema) {
        return new SchemaSelection() {
            @Override
            public String getMatchCatalog() {
                return null;
            }
            @Override
            public String getMatchSchema() {
                return matchSchema;
            }
            @Override
            public String getMatchTable() {
                return null;
            }
        };
    }
}
