package org.hibernate.tools.test.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ResourceUtil {

	public static String getResourcesLocation(Object test) {
		return '/' + test.getClass().getPackage().getName().replace('.', '/') + '/';
	}
	
	public static void createResources(Object test, String[] resources, File resourcesDir) {
		try {
			String defaultResourceLocation = getResourcesLocation(test);
			for (String resource : resources) {
				String resourceLocation = 
						(resource.startsWith("/")) 
						? resource : defaultResourceLocation + resource;
				InputStream inputStream = test
						.getClass()
						.getResourceAsStream(resourceLocation); 
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
	
}
