/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2020 Red Hat, Inc.
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

package org.hibernate.tool.VersionTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.security.CodeSource;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class TestCase {
	
	@Test
	public void testVersion() throws Exception {
		assertEquals(
				org.hibernate.tool.api.version.Version.CURRENT_VERSION,
				extractVersion(getPomXml()));
	}
	
	private String getPomXml() throws Exception {
		CodeSource codeSource = 
				org.hibernate.tool.api.version.Version.class.getProtectionDomain().getCodeSource();
		String path = codeSource.getLocation().getPath();
		String result = null;
		if (path.endsWith(".jar")) {
			JarFile jarFile = new JarFile(new File(path));
			JarEntry jarEntry = jarFile
					.getJarEntry(
							"META-INF/maven/org.hibernate.tool/hibernate-tools-orm/pom.xml");
			InputStream stream = jarFile.getInputStream(jarEntry);
			result = readFromInputStream(stream);
			jarFile.close();
		} else {
			path = path.substring(0, path.indexOf("target/classes/")) + "pom.xml";
			InputStream stream = new FileInputStream(path);
			result = readFromInputStream(stream);
			stream.close();
		}
		return result;		
	}
	
	private String readFromInputStream(InputStream stream) throws Exception {
		StringBuffer buffer = new StringBuffer();
		int b = -1;
		while ((b = stream.read()) != -1) {
			buffer.append((char)b);
		}
		return buffer.toString();		
	}
	
	private String extractVersion(String pomXml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(pomXml)));
		Element root = document.getDocumentElement();
		NodeList nodes = root.getChildNodes();
		Node versionNode = null;
		Node parentNode = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (parentNode == null && "parent".equals(node.getNodeName())) {
				parentNode = node;
			}
			if ("version".equals(node.getNodeName())) {
				versionNode = node;
				break;
			}
		}
		if (versionNode == null) {
			nodes = parentNode.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if ("version".equals(node.getNodeName())) {
					versionNode = node;
					break;
				}
			}
		}
		return versionNode.getTextContent();		
	}

}
