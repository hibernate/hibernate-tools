package org.hibernate.tool.hbm2x.xml;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class TrAXPrettyPrinterStrategy extends AbstractXMLPrettyPrinterStrategy {
    private int indent = 4;
    private boolean omitXmlDeclaration;

    @Override
    public String prettyPrint(String xml) throws Exception {
        final Document document = newDocument(xml, "UTF-8");
        removeWhitespace(document);

        final Transformer transformer = newTransformer(document);

        final StringWriter stringWriter = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    protected Transformer newTransformer(final Document document) throws TransformerConfigurationException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        //transformerFactory.setAttribute("indent-number", getIndent());
        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, isOmitXmlDeclaration() ? "yes" : "no");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(getIndent()));

        final DocumentType doctype = document.getDoctype();
        if (doctype != null) {
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
        }
        return transformer;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public boolean isOmitXmlDeclaration() {
        return omitXmlDeclaration;
    }

    public void setOmitXmlDeclaration(boolean omitXmlDeclaration) {
        this.omitXmlDeclaration = omitXmlDeclaration;
    }
}
