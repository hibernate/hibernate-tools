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
package org.hibernate.tool.hbx1093;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.internal.reveng.strategy.AbstractStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author koen
 */
public class TestCase {
	
	@TempDir
	public File outputDir = new File("output");
	
	private MetadataDescriptor metadataDescriptor = null;
	
	@BeforeEach
	public void setUp() {
		JdbcUtil.createDatabase(this);
        AbstractStrategy c = new DefaultStrategy();
        c.setSettings(new RevengSettings(c).setDetectManyToMany(true)); 
		metadataDescriptor = MetadataDescriptorFactory
				.createReverseEngineeringDescriptor(c, null);
	}
	
	@AfterEach
	public void tearDown() {
		JdbcUtil.dropDatabase(this);
	}

	// TODO Reenable this test and investigate the failure, see HBX-2472
	@Disabled
	@Test
	public void testGenerateJava() throws IOException {
		Exporter exporter = ExporterFactory.createExporter(ExporterType.JAVA);	
		exporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, metadataDescriptor);
		exporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		exporter.getProperties().setProperty("ejb3", "true");
		exporter.start();
		File etManyToManyComp1 = new File(outputDir, "EtManyToManyComp1.java");
		String str = new String(Files.readAllBytes(etManyToManyComp1.toPath()));
		assertTrue(str.contains("@JoinColumn(name=\"FK_ET_MANY_TO_MANY_COMP22_ID\""));
		File etManyToManyComp2 = new File(outputDir, "EtManyToManyComp2.java");
		str = new String(Files.readAllBytes(etManyToManyComp2.toPath()));
		assertTrue(str.contains("@JoinColumn(name=\"FK_ET_MANY_TO_MANY_COMP11_ID\""));
	}
	
}
