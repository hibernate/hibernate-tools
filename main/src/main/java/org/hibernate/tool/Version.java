package org.hibernate.tool;

import java.util.logging.Logger;

final public class Version {

	private static final Version instance = new Version();
	
	public static final String VERSION = instance.versionString();
	
	private static Logger log = Logger.getLogger( Version.class.getName() );

	static {
		log.info( "Hibernate Tools " + VERSION );
	}

	private Version() {
		// dont instantiate me
	}
	
	public String getVersion() {
		return VERSION;
	}
	
	public String versionString() {
		// This implementation is replaced during the build with another one that returns the correct value.
		return "UNKNOWN";
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
