package org.hibernate.tool.hbm2x.xml;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import org.hibernate.tool.test.support.ResultCaptor;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TrAXPrettyPrinterStrategyTest {

    private static final String XML =
            "<!DOCTYPE hibernate-mapping PUBLIC\n" +
                    "\t\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n" +
                    "\t\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n" +
                    "<hibernate-mapping package=\"org.hibernate.tool.hbm2x\"><class name=\"Foo\">\n" +
                    "<id name=\"id\" type=\"integer\"><generator class=\"assigned\"/></id>\n" +
                    "<property name=\"name\" type=\"string\" not-null=\"true\" length=\"100\"/>\n" +
                    "</class></hibernate-mapping>";

    private static final String DEFAULT_RESULT =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n" +
                    "<hibernate-mapping auto-import=\"true\" default-access=\"property\" default-cascade=\"none\" default-lazy=\"true\" package=\"org.hibernate.tool.hbm2x\">\n" +
                    "    <class dynamic-insert=\"false\" dynamic-update=\"false\" mutable=\"true\" name=\"Foo\" optimistic-lock=\"version\" polymorphism=\"implicit\" select-before-update=\"false\">\n" +
                    "        <id name=\"id\" type=\"integer\">\n" +
                    "            <generator class=\"assigned\"/>\n" +
                    "        </id>\n" +
                    "        <property generated=\"never\" lazy=\"false\" length=\"100\" name=\"name\" not-null=\"true\" optimistic-lock=\"true\" type=\"string\" unique=\"false\"/>\n" +
                    "    </class>\n" +
                    "</hibernate-mapping>\n";

    private static final String NO_XML_DECLARATION_RESULT =
            "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n" +
                    "<hibernate-mapping auto-import=\"true\" default-access=\"property\" default-cascade=\"none\" default-lazy=\"true\" package=\"org.hibernate.tool.hbm2x\">\n" +
                    "    <class dynamic-insert=\"false\" dynamic-update=\"false\" mutable=\"true\" name=\"Foo\" optimistic-lock=\"version\" polymorphism=\"implicit\" select-before-update=\"false\">\n" +
                    "        <id name=\"id\" type=\"integer\">\n" +
                    "            <generator class=\"assigned\"/>\n" +
                    "        </id>\n" +
                    "        <property generated=\"never\" lazy=\"false\" length=\"100\" name=\"name\" not-null=\"true\" optimistic-lock=\"true\" type=\"string\" unique=\"false\"/>\n" +
                    "    </class>\n" +
                    "</hibernate-mapping>\n";

    private static final String NON_DEFAULT_INDENT_RESULT =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n" +
                    "<hibernate-mapping auto-import=\"true\" default-access=\"property\" default-cascade=\"none\" default-lazy=\"true\" package=\"org.hibernate.tool.hbm2x\">\n" +
                    "        <class dynamic-insert=\"false\" dynamic-update=\"false\" mutable=\"true\" name=\"Foo\" optimistic-lock=\"version\" polymorphism=\"implicit\" select-before-update=\"false\">\n" +
                    "                <id name=\"id\" type=\"integer\">\n" +
                    "                        <generator class=\"assigned\"/>\n" +
                    "                </id>\n" +
                    "                <property generated=\"never\" lazy=\"false\" length=\"100\" name=\"name\" not-null=\"true\" optimistic-lock=\"true\" type=\"string\" unique=\"false\"/>\n" +
                    "        </class>\n" +
                    "</hibernate-mapping>\n";

    private TrAXPrettyPrinterStrategy strategy;

    @Spy
    private TrAXPrettyPrinterStrategy strategySpy;

    @Mock
    private Document documentMock;

    @Before
    public void setup() {
        this.strategy = new TrAXPrettyPrinterStrategy();
    }

    @Test
    public void testNewTransformerFactoryReturnsSAXTransformerFactory() {
        TransformerFactory transformerFactory = strategy.newTransformerFactory();
        assertTrue(transformerFactory instanceof SAXTransformerFactory);
    }

    @Test
    public void testNewTransformerDoesNotSetDocTypeOutputPropertiesWhenDocTypeNull() throws TransformerConfigurationException {
        when(documentMock.getDoctype()).thenReturn(null);

        Transformer transformer = strategy.newTransformer(documentMock);
        assertFalse("Transformer shouldn't be null", transformer == null);
        assertEquals(null, transformer.getOutputProperty(OutputKeys.DOCTYPE_PUBLIC));
        assertEquals(null, transformer.getOutputProperty(OutputKeys.DOCTYPE_SYSTEM));
    }

    @Test
    public void testNewTransformerSetsDocTypeOutputPropertiesWhenDocTypeSet() throws TransformerConfigurationException {
        DocumentType documentType = mock(DocumentType.class);
        when(documentType.getPublicId()).thenReturn("PUBLIC-ID");
        when(documentType.getSystemId()).thenReturn("SYSTEM-ID");
        when(documentMock.getDoctype()).thenReturn(documentType);

        Transformer transformer = strategy.newTransformer(documentMock);
        assertTrue("Transformer shouldn't be null", transformer != null);
        assertEquals("PUBLIC-ID", transformer.getOutputProperty(OutputKeys.DOCTYPE_PUBLIC));
        assertEquals("SYSTEM-ID", transformer.getOutputProperty(OutputKeys.DOCTYPE_SYSTEM));
    }

    @Test
    public void testPrettyPrintUsingDefaults() throws Exception {
        ResultCaptor<Document> documentCaptor = givenDocumentResultCaptor();
        ResultCaptor<Transformer> transformerCaptor = givenTransformerResultCaptor();

        String result = strategySpy.prettyPrint(XML);

        verifyCommonInteractions(documentCaptor);
        assertOutputProperties(transformerCaptor, "no", "4");

        assertEquals(DEFAULT_RESULT, result);
    }

    @Test
    public void testPrettyPrintOmittingXmlDeclaration() throws Exception {
        ResultCaptor<Document> documentCaptor = givenDocumentResultCaptor();
        ResultCaptor<Transformer> transformerCaptor = givenTransformerResultCaptor();

        strategySpy.setOmitXmlDeclaration(true);

        String result = strategySpy.prettyPrint(XML);

        verifyCommonInteractions(documentCaptor);
        assertOutputProperties(transformerCaptor, "yes", "4");

        assertEquals(NO_XML_DECLARATION_RESULT, result);
    }

    @Test
    public void testPrettyPrintWithNonDefaultIndent() throws Exception {
        ResultCaptor<Document> documentCaptor = givenDocumentResultCaptor();
        ResultCaptor<Transformer> transformerCaptor = givenTransformerResultCaptor();

        strategySpy.setIndent(8);

        String result = strategySpy.prettyPrint(XML);

        verifyCommonInteractions(documentCaptor);
        assertOutputProperties(transformerCaptor, "no", "8");

        assertEquals(NON_DEFAULT_INDENT_RESULT, result);
    }

    private ResultCaptor<Document> givenDocumentResultCaptor() throws Exception {
        ResultCaptor<Document> documentCaptor = new ResultCaptor<>();
        doAnswer(documentCaptor).when(strategySpy).newDocument(anyString(), anyString());
        return documentCaptor;
    }

    private ResultCaptor<Transformer> givenTransformerResultCaptor() throws TransformerConfigurationException {
        ResultCaptor<Transformer> transformerCaptor = new ResultCaptor<>();
        doAnswer(transformerCaptor).when(strategySpy).newTransformer(any(Document.class));
        return transformerCaptor;
    }

    private void verifyCommonInteractions(ResultCaptor<Document> documentCaptor) throws Exception {
        verify(strategySpy).newDocument(XML, "UTF-8");

        Document document = documentCaptor.getResult();
        verify(strategySpy).removeWhitespace(document);
        verify(strategySpy).newTransformer(document);
        verify(strategySpy).newTransformerFactory();
    }

    private void assertOutputProperties(ResultCaptor<Transformer> transformerCaptor, String expectedOmitXmlDeclaration, String expectedIndent) {
        Transformer transformer = transformerCaptor.getResult();

        assertTrue("Transform shouldn't be null", transformer != null);
        assertEquals("xml", transformer.getOutputProperty(OutputKeys.METHOD));
        assertEquals("UTF-8", transformer.getOutputProperty(OutputKeys.ENCODING));
        assertEquals("yes", transformer.getOutputProperty(OutputKeys.INDENT));
        assertEquals(expectedOmitXmlDeclaration, transformer.getOutputProperty(OutputKeys.OMIT_XML_DECLARATION));
        assertEquals(expectedIndent, transformer.getOutputProperty("{http://xml.apache.org/xslt}indent-amount"));
    }
}