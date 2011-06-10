package org.hibernate.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class Version {

	public static final String VERSION = "3.4.0.CR1";
	
	private static final Version instance = new Version();
	
	private static Logger log = LoggerFactory.getLogger( Version.class );

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
