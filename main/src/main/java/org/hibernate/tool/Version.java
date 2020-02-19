package org.hibernate.tool;

import java.util.logging.Logger;

final public class Version {

	public static final String VERSION = "5.4.12.Final";
	
	private static final Version instance = new Version();
	
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
