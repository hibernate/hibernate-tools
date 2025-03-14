import java.io.*;

println "start verify.groovy"

File buildLog = new File(basedir, "build.log")

if (!buildLog.isFile()) {

	println "'" + buildLog.absolutePath + "' is not a file."

	throw new FileNotFoundException("Could not find build log file: '" + buildLog + "'")

} else {

	println "inspecting build log lines"

	boolean found = false
	String startString = "[INFO] Property file '"
	String endString = "src/main/resources/hibernate.properties' cannot be found, aborting..."
	buildLog.eachLine {
		line ->  if (line.startsWith(startString) && line.endsWith(endString)) found = true
	}
	return found

}


println "end verify.groovy"