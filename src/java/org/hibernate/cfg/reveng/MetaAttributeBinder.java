package org.hibernate.cfg.reveng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.dom4j.Element;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.tool.hbm2x.MetaAttributeHelper;

public class MetaAttributeBinder {

	static class SimpleMetaAttribute {
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

	/**
	 * Merges a Multimap with inherited maps.
	 * Values specified always overrules/replaces the inherited values.
	 * 
	 * @param specific
	 * @param general
	 * @return a MultiMap with all values from local and extra values
	 * from inherited
	 */
	public static MultiMap mergeMetaMaps(Map specific, Map general) {
		MultiHashMap result = new MultiHashMap();
		MetaAttributeHelper.copyMultiMap(result, specific);
		
		if (general != null) {
			for (Iterator iter = general.keySet().iterator();
				iter.hasNext();
				) {
				String key = (String) iter.next();
	
				if (!specific.containsKey(key) ) {
					// inheriting a meta attribute only if it is inheritable
					Collection ml = (Collection) general.get(key);
					for (Iterator iterator = ml.iterator();
						iterator.hasNext();
						) {
						MetaAttributeBinder.SimpleMetaAttribute element = (MetaAttributeBinder.SimpleMetaAttribute) iterator.next();
						if (element.inheritable) {
							result.put(key, element);
						}
					}
				}
			}
		}
	
		return result;
	
	}

	public static MetaAttribute toRealMetaAttribute(String name, List values) {
		MetaAttribute attribute = new MetaAttribute(name);
		for (Iterator iter = values.iterator(); iter.hasNext();) {
			MetaAttributeBinder.SimpleMetaAttribute element = (MetaAttributeBinder.SimpleMetaAttribute) iter.next();
			attribute.addValue(element.value);
		}
		
		return attribute;
	}


	/**
	 * Method loadAndMergeMetaMap.
	 * @param classElement
	 * @param inheritedMeta
	 * @return MultiMap
	 */
	public static MultiMap loadAndMergeMetaMap(
		Element classElement,
		MultiMap inheritedMeta) {
		return MetaAttributeBinder.mergeMetaMaps(loadMetaMap(classElement), inheritedMeta);
	}


	/**
	 * Load meta attributes from jdom element into a MultiMap.
	 * 
	 * @param element
	 * @return MultiMap
	 */
	 protected static MultiMap loadMetaMap(Element element) {
		MultiMap result = new MultiHashMap();
		List metaAttributeList = new ArrayList();
		metaAttributeList.addAll(element.elements("meta") );

		for (Iterator iter = metaAttributeList.iterator(); iter.hasNext();) {
			Element metaAttrib = (Element) iter.next();
			// does not use getTextNormalize() or getTextTrim() as that would remove the formatting in new lines in items like description for javadocs.
			String attribute = metaAttrib.attributeValue("attribute");
			String value = metaAttrib.getText();
			String inheritStr= metaAttrib.attributeValue("inherit");
			boolean inherit = true;
			if(inheritStr!=null) {
				inherit = Boolean.valueOf(inheritStr).booleanValue(); 
			}			
			
			MetaAttributeBinder.SimpleMetaAttribute ma = new MetaAttributeBinder.SimpleMetaAttribute(value, inherit);
			result.put(attribute, ma);
		}
		return result;

	}

}
