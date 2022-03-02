package org.hibernate.tool;

import freemarker.log.Logger;

final public class Version {

	public static final String VERSION = "3.6.2.Final";
	
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
