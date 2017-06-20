package org.hibernate.tools.test.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ResourceUtil {

	public static String getResourcesLocation(Object test) {
		return '/' + test.getClass().getPackage().getName().replace('.', '/') + '/';
	}
	
	public static void createResources(Object test, String[] resources, File resourceDir) {
		try {
			String resourcesLocation = getResourcesLocation(test);
			for (String resource : resources) {
				InputStream inputStream = test
						.getClass()
						.getResourceAsStream(resourcesLocation + resource); 
				File resourceFile = new File(resourceDir, resource);
				Files.copy(inputStream, resourceFile.toPath());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
