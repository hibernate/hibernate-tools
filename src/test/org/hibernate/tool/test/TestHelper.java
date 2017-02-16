/*
 * Created on 16-Feb-2005
 *
 */
package org.hibernate.tool.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.internal.compiler.batch.Main;
import org.hibernate.internal.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * @author max
 * 
 */
public final class TestHelper {

	private static final Logger log = LoggerFactory.getLogger(TestHelper.class);

	private TestHelper() {
		// noop
	}

	/**
	 * @param srcdir
	 * @param outputdir
	 * @return
	 */
	public static boolean compile(File srcdir, File outputdir) {

		List<String> files = visitAllFiles( srcdir, new ArrayList<String>() );

		return compile( srcdir, outputdir, files );

	}

	/**
	 * @param srcdir
	 * @param outputdir
	 * @param srcFiles
	 * @return
	 */
	public static boolean compile(File srcdir, File outputdir, List<String> srcFiles) {
		return compile( srcdir, outputdir, srcFiles, "1.4", "" );
	}

	/** 
	 * 
	 * http://dev.eclipse.org/viewcvs/index.cgi/jdt-core-home/howto/batch%20compile/batchCompile.html?rev=1.4
	 * 
	 * @param srcdir
	 * @param outputdir
	 * @param srcFiles
	 * @param jdktarget 1.5 or 1.4 
	 * @param classPath
	 * @return
	 */
	public static boolean compile(File srcdir, File outputdir, List<String> srcFiles, String jdktarget, String classPath) {
		List<String> togglesList = new ArrayList<String>();
		togglesList.add( "-" + jdktarget ); // put this here so DAOs compile
		togglesList.add( "-noExit" );
		togglesList.add( "-warn:unusedImport,noEffectAssign,fieldHiding,localHiding,semicolon" ); 
		togglesList.add( "-sourcepath" );
		togglesList.add( srcdir.getAbsolutePath() + File.separatorChar );
		togglesList.add( "-d" );
		togglesList.add( outputdir.getAbsolutePath() + File.separatorChar );
		if ( StringHelper.isNotEmpty(classPath) ) {
			togglesList.add( "-classpath" );
			togglesList.add( classPath );
		}

		String[] toggles = togglesList.toArray( new String[togglesList.size()] );
		String[] strings = srcFiles.toArray( new String[srcFiles.size()] );
		String[] arguments = new String[toggles.length + strings.length];
		System.arraycopy( toggles, 0, arguments, 0, toggles.length );
		System
				.arraycopy( strings, 0, arguments, toggles.length,
						strings.length );

		StringWriter out = new StringWriter();
		StringWriter err = new StringWriter();

		Main main = new Main( 
				new PrintWriter( out ), 
				new PrintWriter( err ),
				false, 
				null, 
				null);
		main.compile( arguments );
		if ( main.globalErrorsCount > 0 ) {
			throw new RuntimeException( out.toString() + err.toString() );
		}
		
		if ( main.globalWarningsCount > 0 ) {
			throw new RuntimeException( out.toString() + err.toString() );
		}
		return true;

		// return javaCompile( arguments );
	}

	/* Uses the JDK javac tools.
	private static boolean javaCompile(String[] arguments) {
		StringWriter sw = new StringWriter();
		int result = com.sun.tools.javac.Main.compile( arguments,
				new PrintWriter( sw ) );
		if ( result != 0 ) {
			throw new RuntimeException( sw.toString() );
		}
		else {
			return true;
		}
	}*/

	/**
	 * @param ext
	 *            the file extension, don't forget the dot .
	 * @return
	 */

	public static List<String> visitAllFiles(File dir, List<String> files, String ext) {
		if ( dir.isDirectory() ) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				visitAllFiles( new File( dir, children[i] ), files, ext );
			}
		}
		else {
			if ( dir.getName().endsWith( ext ) ) {
				files.add( dir.getAbsolutePath() );
			}
		}

		return files;
	}

	public static List<String> visitAllFiles(File dir, List<String> file) {
		return visitAllFiles( dir, file, ".java" );
	}

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns
	// false.
	public static boolean deleteDir(File dir) {
		if ( dir.isDirectory() ) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				File childFile = new File( dir, children[i] );
				boolean success = deleteDir( childFile );
				if ( !success ) {
					throw new RuntimeException("Could not delete " + childFile);
					//return false;
				}
			}
		}

		// The directory is now empty so delete it
		log.debug("deleting: " + dir);
		return dir.delete();
	}

	public static boolean isWellFormed(String path) {
		boolean wellFormed = false;

		try {
			getDocumentBuilder().parse( path );
			wellFormed = true;
		}
		catch (SAXException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return wellFormed;
	}

	private static DocumentBuilder getDocumentBuilder() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException pce) {
			return null;
		}
		return db;
	}

	public static String buildClasspathFromFileList(List<File> jars) {
		StringBuffer classpath = new StringBuffer();		
		Iterator<File> iterator = jars.iterator();
		while (iterator.hasNext()) {
			File f = iterator.next();
			classpath.append(f);
			if(iterator.hasNext()) {
				classpath.append(File.pathSeparatorChar);
			}
		}		
		return classpath.toString();
	}
	
	static public String findFirstString(String string, File file) {
		String str;
		try {
	        BufferedReader in = new BufferedReader(new FileReader(file) );
	        while ( (str = in.readLine() ) != null ) {
	            if(str.indexOf(string)>=0) {
					break;
	            }
	        }
	        in.close();	        
	    } 
		catch (IOException e) {
			throw new RuntimeException("trouble with searching in " + file,e);
	    }
		return str;
	}

	static public File findJarFileFor(Class<?> clazz) {
		File result = null;
		CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
		if (codeSource != null) {
			URL url = codeSource.getLocation();
			if (url != null) {
				result = new File(url.getPath());
			}
		}
		return result;
	}

}
