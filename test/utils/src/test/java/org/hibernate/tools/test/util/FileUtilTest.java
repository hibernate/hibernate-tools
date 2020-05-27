/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2020 Red Hat, Inc.
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
package org.hibernate.tools.test.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileUtilTest {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Test
	public void testFindFirstString() throws Exception {
		String resource = "/org/hibernate/tools/test/util/FileUtilTest.resource";
		File file = new File(getClass().getResource(resource).toURI());
		Assert.assertEquals("or would it be a bar test?", FileUtil.findFirstString("bar", file));
		Assert.assertNull(FileUtil.findFirstString("foobar", file));
		Assert.assertEquals("This is a foo test", FileUtil.findFirstString("test", file));
		Assert.assertEquals("And a third line...", FileUtil.findFirstString("third", file));
	}
	
	@Test
	public void testGenerateNoopComparator() throws Exception {
		FileUtil.generateNoopComparator(temporaryFolder.getRoot());
		File pkg = new File(temporaryFolder.getRoot(), "comparator");
		Assert.assertTrue(pkg.isDirectory() && pkg.exists());
		File comp = new File(pkg, "NoopComparator.java");
		Assert.assertTrue(comp.isFile() && comp.exists());
		Assert.assertEquals(
				"public class NoopComparator implements Comparator {", 
				FileUtil.findFirstString("NoopComparator", comp));
	}

}
