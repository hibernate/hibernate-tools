import java.io.*;

println "start verify.groovy"

File entity = new File(basedir, "build.log")

if (!entity.isFile()) {
	
    throw new FileNotFoundException("Could not find generated JPA Entity: " + entity)
	
} else {
	
	boolean found = false
	String searchString = 
		"[INFO] Property file '" + 
		basedir.absolutePath + 
		"/src/main/resources/hibernate.properties' cannot be found, aborting..."	
	entity.eachLine { 
		line ->  if (line.startsWith(searchString)) found = true
	}
	return found
	
}


println "end verify.groovy"