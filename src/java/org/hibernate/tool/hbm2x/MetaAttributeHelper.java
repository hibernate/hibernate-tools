package org.hibernate.tool.hbm2x;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MultiMap;

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
	public static String getMetaAsString(Collection meta, String seperator) {
		if(meta==null || meta.isEmpty() ) {
	        return "";
	    }
		StringBuffer buf = new StringBuffer();
		
			for (Iterator iter = meta.iterator(); iter.hasNext();) {				
				buf.append(iter.next() );
				if(iter.hasNext() ) buf.append(seperator);
			}
		return buf.toString();
	}

	public static String getMetaAsString(org.hibernate.mapping.MetaAttribute meta, String seperator) {
		if(meta==null) {
			return null;
		} 
		else {
			return getMetaAsString(meta.getValues(),seperator);
		}
	}

	static	boolean getMetaAsBool(Collection c, boolean defaultValue) {
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
	static String getMetaAsString(Collection c) {
		return getMetaAsString(c, "");
	}

    /**
     * Copies all the values from one MultiMap to another.
     * This method is needed because the (undocumented) behaviour of 
     * MultiHashMap.putAll in versions of Commons Collections prior to 3.0
     * was to replace the collection in the destination, whereas in 3.0
     * it adds the collection from the source as an _element_ of the collection
     * in the destination.  This method makes no assumptions about the implementation
     * of the MultiMap, and should work with all versions.
     * 
     * @param destination
     * @param specific
     */
     public static void copyMultiMap(MultiMap destination, Map specific) {
        for (Iterator keyIterator = specific.keySet().iterator(); keyIterator.hasNext(); ) {
            Object key = keyIterator.next();
            Collection c = (Collection) specific.get(key);
            for (Iterator valueIterator = c.iterator(); valueIterator.hasNext(); ) 
                destination.put(key, valueIterator.next() );
        }
    }

	public static boolean getMetaAsBool(org.hibernate.mapping.MetaAttribute metaAttribute, boolean defaultValue) {
		return getMetaAsBool(metaAttribute==null?null:metaAttribute.getValues(), defaultValue);
	}

	

}
