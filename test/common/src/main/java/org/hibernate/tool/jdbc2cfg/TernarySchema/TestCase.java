/*
 * Created on 2004-11-23
 *
 */
package org.hibernate.tool.jdbc2cfg.TernarySchema;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.SchemaSelection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.visitor.DefaultValueVisitor;
import org.hibernate.tool.metadata.MetadataSourcesFactory;
import org.hibernate.tool.util.MetadataHelper;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author max
 * @author koen
 */
public class TestCase {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private Metadata metadata = null;

	@Before
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
	    metadata = MetadataSourcesFactory.createJdbcSources(c, null).buildMetadata();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	// TODO Investigate the ignored test: HBX-1410
	@Ignore 
	@Test
	public void testTernaryModel() throws SQLException {
		assertMultiSchema(metadata);	
	}

	// TODO Investigate the ignored test: HBX-1410
	@Ignore
	@Test
	public void testGeneration() {		
		File outputFolder = temporaryFolder.getRoot();
		HibernateMappingExporter hme = new HibernateMappingExporter();
		hme.setMetadata(metadata);
		hme.setOutputDirectory(outputFolder);
		hme.start();			
		JUnitUtil.assertIsNonEmptyFile( new File(outputFolder, "Role.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile( new File(outputFolder, "User.hbm.xml") );
		JUnitUtil.assertIsNonEmptyFile( new File(outputFolder, "Plainrole.hbm.xml") );
		Assert.assertEquals(3, outputFolder.listFiles().length);
		Configuration configuration = new Configuration()
		    .addFile( new File(outputFolder, "Role.hbm.xml") )
		    .addFile( new File(outputFolder, "User.hbm.xml") )
		    .addFile( new File(outputFolder, "Plainrole.hbm.xml"));		
		MetadataHelper.getMetadata(configuration);		
		assertMultiSchema(metadata);
	}
	
	private void assertMultiSchema(Metadata metadata) {
		JUnitUtil.assertIteratorContainsExactly(
				"There should be five tables!", 
				metadata.getEntityBindings().iterator(), 
				5);
		final PersistentClass role = metadata.getEntityBinding("Role");
		Assert.assertNotNull(role);
		PersistentClass userroles = metadata.getEntityBinding("Userroles");
		Assert.assertNotNull(userroles);
		PersistentClass user = metadata.getEntityBinding("User");
		Assert.assertNotNull(user);
		PersistentClass plainRole = metadata.getEntityBinding("Plainrole");
		Assert.assertNotNull(plainRole);
		Property property = role.getProperty("users");
		Assert.assertEquals(role.getTable().getSchema(), "OTHERSCHEMA");
		Assert.assertNotNull(property);
		property.getValue().accept(new DefaultValueVisitor(true) {
			public Object accept(Set o) {
				Assert.assertEquals(o.getCollectionTable().getSchema(), "THIRDSCHEMA");
				return null;
			}
		});
		property = plainRole.getProperty("users");
		Assert.assertEquals(role.getTable().getSchema(), "OTHERSCHEMA");
		Assert.assertNotNull(property);
		property.getValue().accept(new DefaultValueVisitor(true) {
			public Object accept(Set o) {
				Assert.assertEquals(o.getCollectionTable().getSchema(), null);
				return null;
			}
		});
	}	
	
}
