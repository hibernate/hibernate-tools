/*
 * Created on 17-Dec-2004
 *
 */
package org.hibernate.tool.hbm2x;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.tidy.Tidy;

/**
 * @author max
 * 
 */
public final class XMLPrettyPrinter {

	private static final Log log = LogFactory.getLog( XMLPrettyPrinter.class );

	private XMLPrettyPrinter() {
		// noop
	}

	public static void prettyPrint(InputStream in, OutputStream writer)
			throws IOException {
		Tidy tidy = getDefaultTidy();

		tidy.parse( in, writer );

	}

	static Tidy getDefaultTidy() throws IOException {
		Tidy tidy = new Tidy();

		// no output please!
		tidy.setErrout( new PrintWriter( new Writer() {
			public void close() throws IOException {
			}

			public void flush() throws IOException {
			}

			public void write(char[] cbuf, int off, int len) throws IOException {
				
			}
		} ) );

		Properties properties = new Properties();

		properties.load( XMLPrettyPrinter.class
				.getResourceAsStream( "jtidy.properties" ) );

		tidy.setConfigurationFromProps( properties );

		return tidy;
	}

	public static void prettyPrintFiles(Tidy tidy, File[] inputfiles,
			File[] outputfiles, boolean silent) throws IOException {

		if ( (inputfiles == null || outputfiles == null )
				|| (inputfiles.length != outputfiles.length ) ) {
			throw new IllegalArgumentException(
					"inputfiles and outputfiles must be not null and have equal length." );
		}

		for (int i = 0; i < outputfiles.length; i++) {
			prettyPrintFile( tidy, inputfiles[i], outputfiles[i], silent );
		}
	}

	public static void prettyPrintFile(Tidy tidy, File inputFile,
			File outputFile, boolean silent) throws IOException {
		log.debug( "XMLPrettyPrinting " + inputFile.getAbsolutePath() );

		InputStream is;
		OutputStream os;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		is = new BufferedInputStream( new FileInputStream( inputFile ) );

		outputFile.getParentFile().mkdirs();
		outputFile.createNewFile();
		os = new BufferedOutputStream( bos );

		tidy.parse( is, os );
		byte[] bs = bos.toByteArray();
		try {
			is.close();
		}
		catch (IOException e1) {
			// ignore
		}
		try {
			os.flush();
			os.close();
		}
		catch (IOException e1) {
			// ignore
		}

		// generate output file
		if ( tidy.getParseErrors() == 0 ) {
			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream( outputFile ) );
			InputStream in = new ByteArrayInputStream( bs );
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ( (len = in.read( buf ) ) > 0 ) {
				out.write( buf, 0, len );
			}
			in.close();
			out.close();
		}

		if ( tidy.getParseErrors() > 0 ) {
			if(silent) {				
				log.warn("Tidy was unable to process file " + inputFile + ", " + tidy.getParseErrors() + " errors found." );
			} else {
				throw new ExporterException( "Tidy was unable to process file "
						+ inputFile + ", " + tidy.getParseErrors() + " errors found." );
			}
		} else {
			log.debug("XMLPrettyPrinting completed");
		}
	}

	/**
	 * @param outputdir
	 * @throws IOException
	 */
	public static void prettyPrintDirectory(File outputdir, final String prefix, boolean silent)
			throws IOException {
		File[] files = outputdir.listFiles( new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith( prefix );
			}
		} );

		Tidy tidy = getDefaultTidy();
		prettyPrintFiles( tidy, files, files, silent );
	}
}
