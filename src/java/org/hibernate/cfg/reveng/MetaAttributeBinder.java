package org.hibernate.cfg.reveng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
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
	public static MultiMap mergeMetaMaps(MultiMap specific, MultiMap general) {
		MultiValueMap result = new MultiValueMap();
		MetaAttributeHelper.copyMultiMap(result, specific);
		
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
		MultiMap result = new MultiValueMap();
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
