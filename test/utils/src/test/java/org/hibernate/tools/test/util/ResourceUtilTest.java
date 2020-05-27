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

public class ResourceUtilTest {
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Test
	public void testGetResourcesLocation() {
		Assert.assertEquals(
				"/org/hibernate/tools/test/util/", 
				ResourceUtil.getResourcesLocation(this));
	}
	
	@Test
	public void testCreateResources() {
		String[] resources = new String[] { "HelloWorld.hbm.xml" };
		File outputFolder = temporaryFolder.getRoot();
		File helloWorldFile = new File(outputFolder, "HelloWorld.hbm.xml");
		Assert.assertFalse(helloWorldFile.exists());
		ResourceUtil.createResources(this, resources, outputFolder);
		Assert.assertTrue(helloWorldFile.exists());
		Assert.assertTrue(FileUtil
				.findFirstString("class", helloWorldFile)
				.contains("HelloWorld"));
	}

}
