package org.hibernate.tool.hbm2x.xml;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import org.hibernate.tool.test.support.ResultCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DOM3LSPrettyPrinterStrategyTest {

    private static final String XML =
            "<!DOCTYPE hibernate-mapping PUBLIC\n" +
                    "\t\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n" +
                    "\t\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n" +
                    "<hibernate-mapping package=\"org.hibernate.tool.hbm2x\"><class name=\"Foo\">\n" +
                    "<id name=\"id\" type=\"integer\"><generator class=\"assigned\"/></id>\n" +
                    "<property name=\"name\" type=\"string\" not-null=\"true\" length=\"100\"/>\n" +
                    "</class></hibernate-mapping>";

    private static final String DEFAULT_RESULT =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n" +
                    "                                   \"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n" +
                    "<hibernate-mapping package=\"org.hibernate.tool.hbm2x\">\n" +
                    "    <class name=\"Foo\">\n" +
                    "        <id name=\"id\" type=\"integer\">\n" +
                    "            <generator class=\"assigned\"/>\n" +
                    "        </id>\n" +
                    "        <property length=\"100\" name=\"name\" not-null=\"true\" type=\"string\"/>\n" +
                    "    </class>\n" +
                    "</hibernate-mapping>\n";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DOM3LSPrettyPrinterStrategy strategy;

    @Spy
    private DOM3LSPrettyPrinterStrategy strategySpy;

    @Mock
    private Document documentMock;

    @Before
    public void setup() {
        this.strategy = new DOM3LSPrettyPrinterStrategy();
    }

    @Test
    public void testGetDomImplementationLSSucceeds() throws Exception {
        Document document = strategy.newDocument(XML, "UTF-8");

        DOMImplementationLS domImplementationLS = strategy.getDomImplementationLS(document);

        assertFalse("DOMImplementationLS should not be null", domImplementationLS == null);
    }

    @Test
    public void testGetDomImplementationLSThrowsRuntimeExceptionWhenNoLS3_0Feature() {
        DOMImplementation domImplementation = mock(DOMImplementation.class);
        when(domImplementation.hasFeature("LS", "3.0")).thenReturn(false);
        when(documentMock.getImplementation()).thenReturn(domImplementation);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("DOM 3.0 LS and/or DOM 2.0 Core not supported.");

        strategy.getDomImplementationLS(documentMock);
    }

    @Test
    public void testGetDomImplementationLSThrowsRuntimeExceptionWhenNoCore2_0Feature() {
        DOMImplementation domImplementation = mock(DOMImplementation.class);
        when(domImplementation.hasFeature("core", "2.0")).thenReturn(false);
        when(documentMock.getImplementation()).thenReturn(domImplementation);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("DOM 3.0 LS and/or DOM 2.0 Core not supported.");

        strategy.getDomImplementationLS(documentMock);
    }

    @Test
    public void testNewSerializerSucceeds() throws Exception {
        Document document = strategy.newDocument(XML, "UTF-8");
        DOMImplementationLS domImplementationLS = strategy.getDomImplementationLS(document);

        LSSerializer serializer = strategy.newLSSerializer(domImplementationLS);

        assertFalse("LSSerializer shouldn't be null", serializer == null);
    }

    @Test
    public void testNewSerializerThrowsExceptionWhenPrettyPrintParameterNotSettable() throws Exception {
        DOMConfiguration domConfig = mock(DOMConfiguration.class);
        when(domConfig.canSetParameter(eq("format-pretty-print"), anyBoolean())).thenReturn(false);

        LSSerializer lsSerializer = mock(LSSerializer.class);
        when(lsSerializer.getDomConfig()).thenReturn(domConfig);

        DOMImplementationLS domImplementationLS = mock(DOMImplementationLS.class);
        when(domImplementationLS.createLSSerializer()).thenReturn(lsSerializer);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("DOMConfiguration 'format-pretty-print' parameter isn't settable.");

        strategy.newLSSerializer(domImplementationLS);
    }

    @Test
    public void testNewLSOutputSucceeds() throws Exception {
        Document document = strategy.newDocument(XML, "UTF-8");
        DOMImplementationLS domImplementationLS = strategy.getDomImplementationLS(document);

        LSOutput lsOutput = strategy.newLSOutput(domImplementationLS);

        assertFalse("LSOutput shouldn't be null", lsOutput == null);
        assertEquals("UTF-8", lsOutput.getEncoding());
    }

    @Test
    public void testPrettyPrintUsingDefaults() throws Exception {
        ResultCaptor<Document> documentCaptor = givenDocumentResultCaptor();
        ResultCaptor<DOMImplementationLS> implementationCaptor = givenDomImplementationLSResultCaptor();

        String result = strategySpy.prettyPrint(XML);

        verifyCommonInteractions(documentCaptor, implementationCaptor);

        assertEquals(DEFAULT_RESULT, result);
    }

    private ResultCaptor<DOMImplementationLS> givenDomImplementationLSResultCaptor() {
        ResultCaptor<DOMImplementationLS> implementationCaptor = new ResultCaptor<>();
        doAnswer(implementationCaptor).when(strategySpy).getDomImplementationLS(any(Document.class));
        return implementationCaptor;
    }


    private ResultCaptor<Document> givenDocumentResultCaptor() throws Exception {
        ResultCaptor<Document> documentCaptor = new ResultCaptor<>();
        doAnswer(documentCaptor).when(strategySpy).newDocument(anyString(), anyString());
        return documentCaptor;
    }

    private void verifyCommonInteractions(ResultCaptor<Document> documentCaptor, ResultCaptor<DOMImplementationLS> implementationCaptor) throws Exception {
        verify(strategySpy).newDocument(XML, "UTF-8");

        Document document = documentCaptor.getResult();
        verify(strategySpy).getDomImplementationLS(document);

        DOMImplementationLS domImplementationLS = implementationCaptor.getResult();
        verify(strategySpy).newLSSerializer(domImplementationLS);
        verify(strategySpy).newLSOutput(domImplementationLS);
    }
}
