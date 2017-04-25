package org.hibernate.tools.test.util;

import java.io.File;
import java.util.ArrayList;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaUtil {
	
	public static void compile(File folder) {
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		ArrayList<String> arguments = new ArrayList<String>();
		arguments.add("-d");
		arguments.add(folder.getAbsolutePath());
		arguments.add("-sourcepath");
		arguments.add(folder.getAbsolutePath());
		ArrayList<String> fileNames = new ArrayList<String>();
		collectJavaFiles(folder, fileNames);
		arguments.addAll(fileNames);
		javaCompiler.run(
				null, 
				null, 
				null, 
				arguments.toArray(new String[arguments.size()]));
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

}
