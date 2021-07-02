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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class JUnitUtilTest {
	
	@Test
	public void testAssertIteratorContainsExactly() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("foo");
		list.add("bar");
		try {
			JUnitUtil.assertIteratorContainsExactly("less", list.iterator(), 1);
			fail();
		} catch (AssertionFailedError e) {
			assertTrue(e.getMessage().contains("less"));
		}
		try {
			JUnitUtil.assertIteratorContainsExactly("more", list.iterator(), 3);
			fail();		
		} catch (AssertionFailedError e) {
			assertTrue(e.getMessage().contains("more"));
		}
		try {
			JUnitUtil.assertIteratorContainsExactly("exact", list.iterator(), 2);
			assertTrue(true);		
		} catch (AssertionFailedError e) {
			fail();
		}
	}
	
	@Test
	public void testAssertIsNonEmptyFile() throws Exception {
		String classResourceName = "/org/hibernate/tools/test/util/JUnitUtilTest.class";
		File exists = new File(getClass().getResource(classResourceName).toURI());
		try {
			JUnitUtil.assertIsNonEmptyFile(exists);
		} catch (Exception e) {
			fail();
		}
		File doesNotExist = new File(exists.getAbsolutePath() + '_');
		try {
			JUnitUtil.assertIsNonEmptyFile(doesNotExist);
			fail();
		} catch (AssertionError e) {
			assertTrue(e.getMessage().contains("does not exist"));
		}		
	}

}
