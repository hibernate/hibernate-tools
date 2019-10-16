package org.hibernate.tool.hbm2x;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections4.MultiValuedMap;
import org.hibernate.cfg.reveng.SimpleMetaAttribute;
import org.hibernate.mapping.MetaAttribute;

/**
 * Helper for loading, merging  and accessing <meta> tags.
 * 
 * @author max
 *
 * 
 */
public final class MetaAttributeHelper {
 
	private MetaAttributeHelper() {
		//noop
	}
	
	/**
	 * @param collection
	 * @param string
	 */
	public static String getMetaAsString(Collection<?> meta, String seperator) {
		if(meta==null || meta.isEmpty() ) {
	        return "";
	    }
		StringBuffer buf = new StringBuffer();
		
			for (Iterator<?> iter = meta.iterator(); iter.hasNext();) {				
				buf.append(iter.next() );
				if(iter.hasNext() ) buf.append(seperator);
			}
		return buf.toString();
	}

	public static String getMetaAsString(MetaAttribute meta, String seperator) {
		if(meta==null) {
			return null;
		} 
		else {
			return getMetaAsString(meta.getValues(),seperator);
		}
	}

	static	boolean getMetaAsBool(Collection<?> c, boolean defaultValue) {
			if(c==null || c.isEmpty() ) {
				return defaultValue;
			} 
			else {
				return Boolean.valueOf(c.iterator().next().toString() ).booleanValue();
			}
		}

	public static String getMetaAsString(org.hibernate.mapping.MetaAttribute c) {		
		return c==null?"":getMetaAsString(c.getValues() );
	}
	
	static String getMetaAsString(Collection<?> c) {
		return getMetaAsString(c, "");
	}

    public static void copyMultiMap(
    		MultiValuedMap<String, SimpleMetaAttribute> destination, 
    		MultiValuedMap<String, SimpleMetaAttribute> specific) {
        for (Iterator<String> keyIterator = specific.keySet().iterator(); keyIterator.hasNext(); ) {
            String key = keyIterator.next();
            Collection<SimpleMetaAttribute> c = specific.get(key);
            for (Iterator<SimpleMetaAttribute> valueIterator = c.iterator(); valueIterator.hasNext();) { 
                destination.put(key, valueIterator.next() );
            }
        }
    }
    
	public static boolean getMetaAsBool(org.hibernate.mapping.MetaAttribute metaAttribute, boolean defaultValue) {
		return getMetaAsBool(metaAttribute==null?null:metaAttribute.getValues(), defaultValue);
	}

	

}
