/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2004-2020 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.ant;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;

/**
 * @author max
 * 
 */
public class Hbm2JavaExporterTask extends ExporterTask {

	boolean ejb3 = true;

	boolean jdk5 = false;

	public Hbm2JavaExporterTask(HibernateToolTask parent) {
		super( parent );
	}

	public void setEjb3(boolean b) {
		ejb3 = b;
	}

	public void setJdk5(boolean b) {
		jdk5 = b;
	}

	protected Exporter configureExporter(Exporter exp) {
		super.configureExporter( exp );
        exp.getProperties().setProperty("ejb3", ""+ejb3);
        exp.getProperties().setProperty("jdk5", ""+jdk5);
		return exp;
	}

	protected Exporter createExporter() {
		return ExporterFactory.createExporter(ExporterType.JAVA);
	}

	public String getName() {
		return "hbm2java (Generates a set of .java files)";
	}
}
