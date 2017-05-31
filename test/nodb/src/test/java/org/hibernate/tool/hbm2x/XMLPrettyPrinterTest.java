package org.hibernate.tool.hbm2x;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.hibernate.tool.hbm2x.xml.XMLPrettyPrinterStrategy;
import org.hibernate.tool.hbm2x.xml.XMLPrettyPrinterStrategyFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XMLPrettyPrinter.class, XMLPrettyPrinterStrategyFactory.class})
public class XMLPrettyPrinterTest {

    private static final String FILE_NAME = "test.xml";
    private static final String FILE_CONTENT = "<test></test>";
    private static final String RESULT = "<test>\n</test>";

    private File file;

    @Mock
    private XMLPrettyPrinterStrategy strategyMock;

    @Mock
    private PrintWriter printWriterMock;

    @Before
    public void setup() {
        PowerMockito.mockStatic(Files.class);
        PowerMockito.mockStatic(XMLPrettyPrinterStrategyFactory.class);

        file = new File(FILE_NAME);
    }

    @Test
    public void testInteractions() throws Exception {
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        when(Files.readAllBytes(pathCaptor.capture())).thenReturn(FILE_CONTENT.getBytes());

        when(strategyMock.prettyPrint(anyString())).thenReturn(RESULT);
        when(XMLPrettyPrinterStrategyFactory.newXMLPrettyPrinterStrategy()).thenReturn(strategyMock);

        PowerMockito.whenNew(PrintWriter.class).withArguments(file).thenReturn(printWriterMock);

        XMLPrettyPrinter.prettyPrintFile(file);

        PowerMockito.verifyStatic();
        Files.readAllBytes(any(Path.class));

        Path path = pathCaptor.getValue();
        assertEquals(FILE_NAME, path.toFile().getName());

        verify(strategyMock).prettyPrint(FILE_CONTENT);

        PowerMockito.verifyNew(PrintWriter.class).withArguments(file);
        verify(printWriterMock).print(RESULT);
        verify(printWriterMock).flush();
        verify(printWriterMock).close();
        verifyNoMoreInteractions(printWriterMock);
    }

    @Test
    public void testThrowsRuntimeExceptionWhenStrategyThrowsException() throws Exception {
        when(Files.readAllBytes(any(Path.class))).thenReturn(FILE_CONTENT.getBytes());

        Throwable exception = new IOException("Oops");
        when(strategyMock.prettyPrint(anyString())).thenThrow(exception);
        when(XMLPrettyPrinterStrategyFactory.newXMLPrettyPrinterStrategy()).thenReturn(strategyMock);

        PowerMockito.whenNew(PrintWriter.class).withArguments(file).thenReturn(printWriterMock);

        try {
            XMLPrettyPrinter.prettyPrintFile(file);
            fail("Should throw RuntimeException when strategy fails");
        } catch (Exception ex) {
            assertTrue(ex instanceof RuntimeException);
            assertTrue(ex.getCause() instanceof IOException);
            assertEquals("Oops", ex.getCause().getMessage());
        }

        verifyZeroInteractions(printWriterMock);
    }

}
