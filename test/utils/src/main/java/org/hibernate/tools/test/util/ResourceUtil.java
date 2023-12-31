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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class ResourceUtil {

	public static void createResources(Object test, String[] resources, File resourcesDir) {
		try {
			for (String resource : resources) {
				InputStream inputStream = resolveResourceLocation(test.getClass(), resource);
				File resourceFile = new File(resourcesDir, resource);
				File parent = resourceFile.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				Files.copy(inputStream, resourceFile.toPath());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static InputStream resolveResourceLocation(Class<?> testClass, String resourceName) {
		InputStream result = null;
		if (resourceName.startsWith("/")) {
			result = testClass.getResourceAsStream(resourceName);
		} else {
			result = resolveRelativeResourceLocation(testClass, resourceName);
		}
		return result;
	}

  public static File resolveResourceFile(Class<?> testClass, String resourceName) {
    String path = testClass.getPackage().getName().replace('.', File.separatorChar);
    URL resourceUrl = testClass.getClassLoader().getResource(path + File.separatorChar
        + resourceName);
      File resourceFile = new File(resourceUrl.getFile());
      return resourceFile;
    }
	
	private static String getRelativeResourcesRoot(Class<?> testClass) {
		return '/' + testClass.getPackage().getName().replace('.', '/') + '/';
	}
	
	private static InputStream resolveRelativeResourceLocation(Class<?> testClass, String resourceName) {
		InputStream result = testClass.getResourceAsStream(getRelativeResourcesRoot(testClass) + resourceName);
		if (result == null && testClass.getSuperclass() != Object.class) {
			result = resolveRelativeResourceLocation(testClass.getSuperclass(), resourceName);
		}
		return result;
	}
	
}
