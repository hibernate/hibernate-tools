package org.hibernate.tool.hbm2x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Callback class that all exporters are given to allow 
 * better feedback and processing of the output afterwards.
 * 
 * @author Max Rydahl Andersen
 *
 */
public class ArtifactCollector {

	final protected Map files = new HashMap();
	
	/** 
	 * Called to inform that a file has been created by the exporter.
	 */
	public void addFile(File file, String type) {		
		List existing = (List) files.get(type);
		if(existing==null) {
			existing = new ArrayList();
			files.put(type, existing);
		}
		existing.add(file);
	}

	public int getFileCount(String type) {
		List existing = (List) files.get(type);
		
		return (existing==null) ? 0 : existing.size();
	}

	public File[] getFiles(String type) {
		List existing = (List) files.get(type);
		
		if(existing==null) {
			return new File[0];
		} else {
			return (File[]) existing.toArray(new File[existing.size()]);
		}
	}
	
	public Set getFileTypes() {
		return files.keySet();
	}

	public void formatFiles() {
		
		formatXml( "xml" );
		formatXml( "hbm.xml" );
		formatXml( "cfg.xml" );
				
	}

	private void formatXml(String type) throws ExporterException {
		List list = (List) files.get(type);
		if(list!=null && !list.isEmpty()) {
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				File xmlFile = (File) iter.next();
				try {					
					XMLPrettyPrinter.prettyPrintFile(XMLPrettyPrinter.getDefaultTidy(), xmlFile, xmlFile, true);
				}
				catch (IOException e) {
					throw new ExporterException("Could not format XML file: " + xmlFile,e);
				}
			}
		}
	}
	
}
