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
package org.hibernate.tool.hbm2x.hbx2840;

import java.io.File;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test verifies that a foreign key to a table with a composite ID works.
 */
public class TestCase {

	@TempDir
	public File outputDir = new File( "output" );

	@BeforeEach
	void setUp() {
		JdbcUtil.createDatabase( this );
		Exporter exporter = ExporterFactory.createExporter( ExporterType.JAVA );
		exporter.getProperties().put(
				ExporterConstants.METADATA_DESCRIPTOR,
				MetadataDescriptorFactory.createReverseEngineeringDescriptor( null, null )
		);
		exporter.getProperties().put( ExporterConstants.DESTINATION_FOLDER, outputDir );
		exporter.getProperties().put( ExporterConstants.TEMPLATE_PATH, new String[0] );
		exporter.getProperties().setProperty( "ejb3", "true" );
		exporter.start();
	}

	@AfterEach
	void tearDown() {
		JdbcUtil.dropDatabase( this );
	}

	@Test
	void testFileExistence() {
		JUnitUtil.assertIsNonEmptyFile( new File( outputDir.getAbsolutePath() + "/Parent.java" ) );
		JUnitUtil.assertIsNonEmptyFile( new File( outputDir.getAbsolutePath() + "/Child.java" ) );
		JUnitUtil.assertIsNonEmptyFile( new File( outputDir.getAbsolutePath() + "/ParentId.java" ) );
		JUnitUtil.assertIsNonEmptyFile( new File( outputDir.getAbsolutePath() + "/ChildId.java" ) );
	}
}
