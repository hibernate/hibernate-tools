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

package org.hibernate.tool.hbm2x.Hbm2CfgTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author max
 * @author koen
 */
public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"HelloWorld.hbm.xml"
	};
	
	@TempDir
	public File outputFolder = new File("output");
	
	private File srcDir = null;
	private File resourcesDir = null;
	
	private HibernateConfigurationExporter cfgexporter;

	@BeforeEach
	public void setUp() throws Exception {
		srcDir = new File(outputFolder, "src");
		srcDir.mkdir();
		resourcesDir = new File(outputFolder, "resources");
		resourcesDir.mkdir();
		MetadataDescriptor metadataDescriptor = HibernateUtil
				.initializeMetadataDescriptor(this, HBM_XML_FILES, resourcesDir);
		cfgexporter = new HibernateConfigurationExporter();
		cfgexporter.setMetadataDescriptor(metadataDescriptor);
		cfgexporter.setOutputDirectory(srcDir);
		cfgexporter.start();
	}
	
	@Test
	public void testMagicPropertyHandling() {
	   HibernateConfigurationExporter exporter = new HibernateConfigurationExporter();
	   Properties properties = exporter.getProperties();
	   properties.setProperty( "hibernate.basic", "aValue" );
	   properties.setProperty( Environment.SESSION_FACTORY_NAME, "shouldNotShowUp");
	   properties.setProperty( Environment.HBM2DDL_AUTO, "false");
	   properties.setProperty( "hibernate.temp.use_jdbc_metadata_defaults", "false");	   
	   properties.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
	   exporter.setMetadataDescriptor(MetadataDescriptorFactory
			   .createNativeDescriptor(null, null, properties));
	   exporter.setOutputDirectory(srcDir);
	   exporter.start();
	   File file = new File(srcDir, "hibernate.cfg.xml");
	   assertNull(
			   FileUtil.findFirstString(
					   Environment.SESSION_FACTORY_NAME, file ));
	   assertNotNull(
			   FileUtil.findFirstString( "hibernate.basic\">aValue<", file ));
	   assertNull(
			   FileUtil.findFirstString( Environment.HBM2DDL_AUTO, file ));
	   assertNull(
			   FileUtil.findFirstString("hibernate.temp.use_jdbc_metadata_defaults", file ));
	   exporter = new HibernateConfigurationExporter();
	   properties = exporter.getProperties();
	   properties.setProperty( Environment.HBM2DDL_AUTO, "validator");   
	   properties.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
	   exporter.setMetadataDescriptor(MetadataDescriptorFactory
			   .createNativeDescriptor(null, null, properties));
	   exporter.setOutputDirectory(srcDir);
	   exporter.start();
	   assertNotNull(
			   FileUtil.findFirstString( Environment.HBM2DDL_AUTO, file ));
	   exporter = new HibernateConfigurationExporter();
	   properties = exporter.getProperties();
	   properties.setProperty( AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "org.hibernate.console.FakeTransactionManagerLookup"); // Hack for seam-gen console configurations
	   properties.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
	   exporter.setMetadataDescriptor(MetadataDescriptorFactory
			   .createNativeDescriptor(null, null, properties));
	   exporter.setOutputDirectory(srcDir);
	   exporter.start();
	   assertNull(
			   FileUtil.findFirstString( AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, file ));
	}
	
	@Test
	public void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile(new File(srcDir, "hibernate.cfg.xml") );		
	}

	@Test
	public void testArtifactCollection() {
		assertEquals(1, cfgexporter.getArtifactCollector().getFileCount("cfg.xml"));
	}
	
	@Test
	public void testNoVelocityLeftOvers() {
        assertNull(FileUtil.findFirstString("${",new File(srcDir, "hibernate.cfg.xml")));
	}

}
