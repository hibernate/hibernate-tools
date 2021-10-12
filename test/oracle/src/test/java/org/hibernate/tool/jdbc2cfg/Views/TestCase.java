/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.jdbc2cfg.Views;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
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
	
	private Metadata metadata;
	
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
	public void testViewAndSynonyms() throws SQLException {
		
		PersistentClass classMapping = metadata.getEntityBinding("Basicview");
		assertNotNull(classMapping);
	
		classMapping = metadata.getEntityBinding("Weirdname");
		assertTrue(classMapping==null, "If this is not-null synonyms apparently work!");

		// get comments
		Table table = HibernateUtil.getTable(metadata, "BASIC");
		assertEquals("a basic comment", table.getComment());
		assertEquals("a solid key", table.getPrimaryKey().getColumn(0).getComment());
		
		table = HibernateUtil.getTable(metadata, "MULTIKEYED");
		assertNull(table.getComment());
		assertNull(table.getColumn(0).getComment());
		
	}


}
