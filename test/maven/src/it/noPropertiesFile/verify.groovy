/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 *
 * Copyright 2018-2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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