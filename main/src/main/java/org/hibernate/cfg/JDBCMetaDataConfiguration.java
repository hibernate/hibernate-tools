/*
 * Created on 26-Nov-2004
 *
 */
package org.hibernate.cfg;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author max
 *
 */
public class JDBCMetaDataConfiguration extends Configuration {

    private static final Logger log = LoggerFactory.getLogger(JDBCMetaDataConfiguration.class);
	
	private boolean ignoreconfigxmlmapppings = true;
	// set to true and fk's that are part of a primary key will just be mapped as the raw value and as a readonly property. if false, it will be <many-to-one-key-property
	
    protected void parseMappingElement(Element subelement, String name) {
        if(!ignoreconfigxmlmapppings ) {  
        	//FIXME the method is private
           // super.parseMappingElement(subelement, name);
        } 
        else {
            log.info("Ignoring " + name + " mapping");
        }
    }

}
