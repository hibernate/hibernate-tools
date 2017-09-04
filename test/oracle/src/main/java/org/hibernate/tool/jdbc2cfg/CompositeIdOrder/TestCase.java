/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.jdbc2cfg.CompositeIdOrder;

import java.sql.SQLException;
import java.util.Iterator;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.tool.metadata.MetadataSourcesFactory;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	private Metadata metadata = null;
	
	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);
		metadata = MetadataSourcesFactory.createJdbcSources(null, null).buildMetadata();
	}
	
	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}
	
	@Test
	public void testMultiColumnForeignKeys() throws SQLException {

		Table table = HibernateUtil.getTable(metadata, "COURSE");
        Assert.assertNotNull(table);
        ForeignKey foreignKey = HibernateUtil.getForeignKey(table, "FK_COURSE__SCHEDULE");     
        Assert.assertNotNull(foreignKey);
                
        Assert.assertEquals("Schedule", foreignKey.getReferencedEntityName() );
        Assert.assertEquals("COURSE", foreignKey.getTable().getName() );
        
        Assert.assertEquals(1,foreignKey.getColumnSpan() );
        Assert.assertEquals(foreignKey.getColumn(0).getName(), "SCHEDULE_KEY");
        
        Assert.assertEquals(table.getPrimaryKey().getColumn(0).getName(), "SCHEDULE_KEY");
        Assert.assertEquals(table.getPrimaryKey().getColumn(1).getName(), "REQUEST_KEY");
        
        PersistentClass course = metadata.getEntityBinding("Course");
        
        Assert.assertEquals(2,course.getIdentifier().getColumnSpan() );
        Iterator<Selectable> columnIterator = course.getIdentifier().getColumnIterator();
        Assert.assertEquals(((Column)(columnIterator.next())).getName(), "SCHEDULE_KEY");
        Assert.assertEquals(((Column)(columnIterator.next())).getName(), "REQUEST_KEY");
        
        PersistentClass topic = metadata.getEntityBinding("CourseTopic");
        
        Property property = topic.getProperty("course");
        columnIterator = property.getValue().getColumnIterator();
        Assert. assertEquals(((Column)(columnIterator.next())).getName(), "SCHEDULE_KEY");
        Assert.assertEquals(((Column)(columnIterator.next())).getName(), "REQUEST_KEY");

	}


}
