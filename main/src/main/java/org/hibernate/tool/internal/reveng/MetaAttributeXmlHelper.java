package org.hibernate.tool.internal.reveng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.hibernate.tool.internal.reveng.MetaAttributeHelper.SimpleMetaAttribute;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MetaAttributeXmlHelper {

	static MultiMap loadAndMergeMetaMap(
			Element classElement,
			MultiMap inheritedMeta) {
		return MetaAttributeHelper.mergeMetaMaps(
				loadMetaMap(classElement), 
				inheritedMeta);
	}

	static MultiMap loadMetaMap(Element element) {
		MultiMap result = new MultiValueMap();
		List<Element> metaAttributeList = new ArrayList<Element>();
		NodeList nodeList = element.getElementsByTagName("meta");
		for (int i = 0; i < nodeList.getLength(); i++) {
			metaAttributeList.add((Element)nodeList.item(i));
		}
		for (Iterator<Element> iter = metaAttributeList.iterator(); iter.hasNext();) {
			Element metaAttribute = iter.next();
			String attribute = metaAttribute.getAttribute("attribute");
			String value = metaAttribute.getTextContent();
			String inheritStr= null;
			if (metaAttribute.hasAttribute("inherit")) {
				inheritStr = metaAttribute.getAttribute("inherit");
			}
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
