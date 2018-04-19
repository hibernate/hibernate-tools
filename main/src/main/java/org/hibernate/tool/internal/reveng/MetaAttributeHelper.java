package org.hibernate.tool.internal.reveng;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.tool.internal.util.MultiMapUtil;

public class MetaAttributeHelper {

	/**
	 * Merges a Multimap with inherited maps.
	 * Values specified always overrules/replaces the inherited values.
	 * 
	 * @param specific
	 * @param general
	 * @return a MultiMap with all values from local and extra values
	 * from inherited
	 */
	public static MultiMap mergeMetaMaps(MultiMap specific, MultiMap general) {
		MultiValueMap result = new MultiValueMap();
		MultiMapUtil.copyMultiMap(result, specific);
		
		if (general != null) {
			for (Iterator<?> iter = general.keySet().iterator();iter.hasNext();) {
				Object key = iter.next();
	
				if (!specific.containsKey(key) ) {
					// inheriting a meta attribute only if it is inheritable
					Collection<?> ml = (Collection<?>)general.get(key);
					for (Iterator<?> iterator = ml.iterator(); iterator.hasNext();) {
						SimpleMetaAttribute element = (SimpleMetaAttribute) iterator.next();
						if (element.inheritable) {
							result.put(key, element);
						}
					}
				}
			}
		}
	
		return result;
	
	}

	public static MetaAttribute toRealMetaAttribute(String name, List<?> values) {
		MetaAttribute attribute = new MetaAttribute(name);
		for (Iterator<?> iter = values.iterator(); iter.hasNext();) {
			SimpleMetaAttribute element = (SimpleMetaAttribute) iter.next();
			attribute.addValue(element.value);
		}
		
		return attribute;
	}


	 public static class SimpleMetaAttribute {
		String value;
		boolean inheritable = true;
		public SimpleMetaAttribute(String value, boolean inherit) {
			this.value = value;
			this.inheritable = inherit;
		}
		public String toString() {
			return value;
		}
	}
}
