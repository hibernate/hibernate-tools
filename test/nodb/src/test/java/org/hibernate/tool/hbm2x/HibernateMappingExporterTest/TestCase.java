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

package org.hibernate.tool.hbm2x.HibernateMappingExporterTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tools.test.util.HibernateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TestCase {
	
	private static final String FOO_HBM_XML = 
			"<hibernate-mapping>              "+
			"	<class name='Foo' table='FOO'>"+
			"		<id type='String'>        "+
			"			<column name='BAR'/>  "+
			"		</id>                     "+
			"	</class>                      "+
			"</hibernate-mapping>             ";

	@TempDir
	public File outputFolder = new File("output");
	
	@Test
	public void testStart() throws Exception {
		File resources = new File(outputFolder, "resources");
		resources.mkdir();
		File fooHbmXmlOrigin = new File(resources, "origin.hbm.xml");
		FileWriter writer = new FileWriter(fooHbmXmlOrigin);
		writer.write(FOO_HBM_XML);
		writer.close();
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", HibernateUtil.Dialect.class.getName());
		MetadataDescriptor metadataDescriptor = MetadataDescriptorFactory
				.createNativeDescriptor(null, new File[] { fooHbmXmlOrigin }, properties); 		
		final File outputDir = new File(outputFolder, "output");
		outputDir.mkdir();
		HibernateMappingExporter exporter = new HibernateMappingExporter();
		exporter.setMetadataDescriptor(metadataDescriptor);
		exporter.setOutputDirectory(outputDir);
		final File fooHbmXml = new File(outputDir, "Foo.hbm.xml");
		assertFalse(fooHbmXml.exists());
		exporter.start();
		assertTrue(fooHbmXml.exists());
		assertTrue(fooHbmXml.delete());
	}

}
