package org.hibernate.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final public class Version {

	public static final String VERSION = "3.6.0";
	
	private static final Version instance = new Version();
	
	private static Log log = LogFactory.getLog( Version.class );

	static {
		log.info( "Hibernate Tools " + VERSION );
	}

	private Version() {
		// dont instantiate me
	}
	
	public String getVersion() {
		return VERSION;
	}
	
	public static Version getDefault() {
		return instance;
	}
	
	public String toString() {
		return getVersion();
	}
	
	public static void touch() {}
	
	public static void main(String[] args) {
		System.out.println(new Version());
	}
}
