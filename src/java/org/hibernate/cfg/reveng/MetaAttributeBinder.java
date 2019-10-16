package org.hibernate.cfg.reveng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.dom4j.Element;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.tool.hbm2x.MetaAttributeHelper;

public class MetaAttributeBinder {

	/**
	 * Merges a Multimap with inherited maps.
	 * Values specified always overrules/replaces the inherited values.
	 * 
	 * @param specific
	 * @param general
	 * @return a MultiMap with all values from local and extra values
	 * from inherited
	 */
	public static MultiValuedMap<String, SimpleMetaAttribute> mergeMetaMaps(
			MultiValuedMap<String, SimpleMetaAttribute> specific, 
			MultiValuedMap<String, SimpleMetaAttribute> general) {
		MultiValuedMap<String, SimpleMetaAttribute> result = new HashSetValuedHashMap<String, SimpleMetaAttribute>();
		MetaAttributeHelper.copyMultiMap(result, specific);
		
		if (general != null) {
			for (Iterator<String> iter = general.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();	
				if (!specific.containsKey(key) ) {
					// inheriting a meta attribute only if it is inheritable
					Collection<SimpleMetaAttribute> ml = general.get(key);
					for (Iterator<SimpleMetaAttribute> iterator = ml.iterator(); iterator.hasNext();) {
						SimpleMetaAttribute element = iterator.next();
						if (element.inheritable) {
							result.put(key, element);
						}
					}
				}
			}
		}
	
		return result;
	
	}

	public static MetaAttribute toRealMetaAttribute(String name, Collection<SimpleMetaAttribute> values) {
		MetaAttribute attribute = new MetaAttribute(name);
		for (Iterator<SimpleMetaAttribute> iter = values.iterator(); iter.hasNext();) {
			SimpleMetaAttribute element = iter.next();
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
	public static MultiValuedMap<String, SimpleMetaAttribute> loadAndMergeMetaMap(
			Element classElement,
			MultiValuedMap<String, SimpleMetaAttribute> inheritedMeta) {
		return MetaAttributeBinder.mergeMetaMaps(loadMetaMap(classElement), inheritedMeta);
	}


	/**
	 * Load meta attributes from jdom element into a MultiMap.
	 * 
	 * @param element
	 * @return MultiMap
	 */
	 protected static MultiValuedMap<String, SimpleMetaAttribute> loadMetaMap(Element element) {
		MultiValuedMap<String, SimpleMetaAttribute> result = new HashSetValuedHashMap<String, SimpleMetaAttribute>();
		List<Element> metaAttributeList = new ArrayList<Element>();
		for (Object obj : element.elements("meta")) {
			metaAttributeList.add((Element)obj);
		}

		for (Iterator<Element> iter = metaAttributeList.iterator(); iter.hasNext();) {
			Element metaAttrib = iter.next();
			// does not use getTextNormalize() or getTextTrim() as that would remove the formatting in new lines in items like description for javadocs.
			String attribute = metaAttrib.attributeValue("attribute");
			String value = metaAttrib.getText();
			String inheritStr= metaAttrib.attributeValue("inherit");
			boolean inherit = true;
			if(inheritStr!=null) {
				inherit = Boolean.valueOf(inheritStr).booleanValue(); 
			}			
			
			SimpleMetaAttribute ma = new SimpleMetaAttribute(value, inherit);
			result.put(attribute, ma);
		}
		return result;

	}

}
