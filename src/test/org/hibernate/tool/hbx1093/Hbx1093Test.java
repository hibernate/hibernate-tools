/*
 * Created on 07-Dec-2004
 *
 */
package org.hibernate.tool.hbx1093;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.test.TestHelper;
import org.junit.Assert;

/**
 * @author koen
 */
public class Hbx1093Test extends JDBCMetaDataBinderTestCase {

	public Hbx1093Test() {
		super("HBX-1093");
	}

	
	protected String[] getCreateSQL() {
		
		return new String[] {
				"CREATE TABLE ET_MANY_TO_MANY_COMP1 (ET_MANY_TO_MANY_COMP1_ID INT, ET_MANY_TO_MANY_COMP11_ID INT, FIELD VARCHAR(10), PRIMARY KEY (ET_MANY_TO_MANY_COMP1_ID, ET_MANY_TO_MANY_COMP11_ID))",
				"CREATE TABLE ET_MANY_TO_MANY_COMP2 ( ET_MANY_TO_MANY_COMP2_ID INT, ET_MANY_TO_MANY_COMP22_ID INT, FIELD VARCHAR(10), PRIMARY KEY (ET_MANY_TO_MANY_COMP2_ID, ET_MANY_TO_MANY_COMP22_ID))",
				"CREATE TABLE ET_MANY_TO_MANY_COMP_MAPPING ( FK_ET_MANY_TO_MANY_COMP1_ID INT, FK_ET_MANY_TO_MANY_COMP11_ID INT, FK_ET_MANY_TO_MANY_COMP2_ID INT, FK_ET_MANY_TO_MANY_COMP22_ID INT, FOREIGN KEY (FK_ET_MANY_TO_MANY_COMP1_ID,FK_ET_MANY_TO_MANY_COMP11_ID) REFERENCES ET_MANY_TO_MANY_COMP1(ET_MANY_TO_MANY_COMP1_ID,ET_MANY_TO_MANY_COMP11_ID), FOREIGN KEY (FK_ET_MANY_TO_MANY_COMP2_ID, FK_ET_MANY_TO_MANY_COMP22_ID) REFERENCES ET_MANY_TO_MANY_COMP2(ET_MANY_TO_MANY_COMP2_ID, ET_MANY_TO_MANY_COMP22_ID), PRIMARY KEY (FK_ET_MANY_TO_MANY_COMP1_ID,FK_ET_MANY_TO_MANY_COMP11_ID,FK_ET_MANY_TO_MANY_COMP2_ID,FK_ET_MANY_TO_MANY_COMP22_ID) )"
		};
	}

	protected String[] getDropSQL() {
		
		return new String[]  {
				"DROP TABLE ET_MANY_TO_MANY_COMP_MAPPING",
				"DROP TABLE ET_MANY_TO_MANY_COMP2",
				"DROP TABLE ET_MANY_TO_MANY_COMP1"
		};
	}
	
	protected void configure(JDBCMetaDataConfiguration cfg2configure) {
		DefaultReverseEngineeringStrategy configurableNamingStrategy = new DefaultReverseEngineeringStrategy();
		configurableNamingStrategy.setSettings(new ReverseEngineeringSettings(configurableNamingStrategy));
		cfg2configure.setReverseEngineeringStrategy(configurableNamingStrategy);
	}
	
	public void testGenerateJava() throws IOException {
		File outputDir = getOutputDir();
		POJOExporter exporter = new POJOExporter(cfg, outputDir);		
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.start();
		File etManyToManyComp1 = new File(outputDir, "EtManyToManyComp1.java");
		String str = new String(Files.readAllBytes(etManyToManyComp1.toPath()));
		Assert.assertTrue(str.contains("@JoinColumn(name=\"FK_ET_MANY_TO_MANY_COMP22_ID\""));
		File etManyToManyComp2 = new File(outputDir, "EtManyToManyComp2.java");
		str = new String(Files.readAllBytes(etManyToManyComp2.toPath()));
		Assert.assertTrue(str.contains("@JoinColumn(name=\"FK_ET_MANY_TO_MANY_COMP11_ID\""));
		TestHelper.deleteDir(getOutputDir());
	}
	
}
