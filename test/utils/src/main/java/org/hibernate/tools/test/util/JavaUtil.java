package org.hibernate.tools.test.util;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaUtil {
	
	public static void compile(File folder) {
		compile(folder, (List<String>)null);
	}
	
	public static void compile(File folder, List<String> classPath) {
		compile(folder, folder, classPath);
	}
	
	public static void compile(File sourceFolder, File destinationFolder) {
		compile(sourceFolder, destinationFolder, null);
	}
	
	public static void compile(
			File sourceFolder, 
			File destinationFolder, 
			List<String> classPath) {
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		ArrayList<String> arguments = new ArrayList<String>();
		arguments.add("-d");
		arguments.add(destinationFolder.getAbsolutePath());
		arguments.add("-sourcepath");
		arguments.add(sourceFolder.getAbsolutePath());
		if (classPath != null && !classPath.isEmpty()) {
			arguments.add("-cp");
			arguments.add(convertClassPath(classPath));
		}
		ArrayList<String> fileNames = new ArrayList<String>();
		collectJavaFiles(sourceFolder, fileNames);
		arguments.addAll(fileNames);
		javaCompiler.run(
				null, 
				null, 
				null, 
				arguments.toArray(new String[arguments.size()]));
	}
	
	public static String resolvePathToJarFileFor(Class<?> clazz) {
		String result = null;
		CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
		if (codeSource != null) {
			URL url = codeSource.getLocation();
			if (url != null) {
				result = url.getPath();
			}
		}
		return result;
	}
	
	private static void collectJavaFiles(File file, ArrayList<String> list) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				collectJavaFiles(child, list);
			}
		} else { 
			if (file.getName().endsWith(".java")) {
				list.add(file.getAbsolutePath());
			}
		}
	}
	
	private static String convertClassPath(List<String> paths) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < paths.size() - 1; i++) {
			sb.append(paths.get(i)).append(File.pathSeparator);
		}
		sb.append(paths.get(paths.size() - 1));
		return sb.toString();
	}

}
