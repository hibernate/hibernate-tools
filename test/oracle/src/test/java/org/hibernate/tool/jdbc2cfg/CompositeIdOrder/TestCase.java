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
package org.hibernate.tool.jdbc2cfg.CompositeIdOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.SQLException;
import java.util.Iterator;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	private Metadata metadata = null;
	
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
		metadata = MetadataDescriptorFactory
				.createJdbcDescriptor(null, null, true)
				.createMetadata();
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testMultiColumnForeignKeys() throws SQLException {

		Table table = HibernateUtil.getTable(metadata, "COURSE");
        assertNotNull(table);
        ForeignKey foreignKey = HibernateUtil.getForeignKey(table, "FK_COURSE__SCHEDULE");     
        assertNotNull(foreignKey);
                
        assertEquals("Schedule", foreignKey.getReferencedEntityName() );
        assertEquals("COURSE", foreignKey.getTable().getName() );
        
        assertEquals(1,foreignKey.getColumnSpan() );
        assertEquals(foreignKey.getColumn(0).getName(), "SCHEDULE_KEY");
        
        assertEquals(table.getPrimaryKey().getColumn(0).getName(), "SCHEDULE_KEY");
        assertEquals(table.getPrimaryKey().getColumn(1).getName(), "REQUEST_KEY");
        
        PersistentClass course = metadata.getEntityBinding("Course");
        
        assertEquals(2,course.getIdentifier().getColumnSpan() );
        Iterator<Selectable> columnIterator = course.getIdentifier().getColumnIterator();
        assertEquals(((Column)(columnIterator.next())).getName(), "SCHEDULE_KEY");
        assertEquals(((Column)(columnIterator.next())).getName(), "REQUEST_KEY");
        
        PersistentClass topic = metadata.getEntityBinding("CourseTopic");
        
        Property property = topic.getProperty("course");
        columnIterator = property.getValue().getColumnIterator();
        assertEquals(((Column)(columnIterator.next())).getName(), "SCHEDULE_KEY");
        assertEquals(((Column)(columnIterator.next())).getName(), "REQUEST_KEY");

	}


}
