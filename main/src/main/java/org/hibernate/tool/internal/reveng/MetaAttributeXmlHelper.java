package org.hibernate.tool.internal.reveng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.hibernate.tool.internal.reveng.MetaAttributeHelper.SimpleMetaAttribute;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
		ArrayList<Element> metaNodes = getChildElements(element, "meta");
		for (Element metaNode : metaNodes) {
			metaAttributeList.add(metaNode);
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

	private static ArrayList<Element> getChildElements(Element parent, String tagName) {
		ArrayList<Element> result = new ArrayList<Element>();
		NodeList nodeList = parent.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element) {
				if (tagName.equals(((Element)node).getTagName())) {
					result.add((Element)node);
				}
			}
		}
		return result;
	}
	
}
