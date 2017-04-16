package org.hibernate.tool.hbm2x.xml;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XMLPrettyPrinterStrategyFactoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        System.clearProperty(XMLPrettyPrinterStrategyFactory.PROPERTY_STRATEGY_IMPL);
    }

    @Test
    public void testStrategyKey() {
        assertEquals("org.hibernate.tool.hbm2x.xml.XMLPrettyPrinterStrategy", XMLPrettyPrinterStrategyFactory.PROPERTY_STRATEGY_IMPL);
    }

    @Test
    public void testDefaultStrategyIsTrAX() {
        XMLPrettyPrinterStrategy strategy = XMLPrettyPrinterStrategyFactory.newXMLPrettyPrinterStrategy();
        assertTrue(strategy instanceof TrAXPrettyPrinterStrategy);
    }

    @Test
    public void testNonDefaultStrategyFromSystemPropertySucceeds() {
        System.setProperty(XMLPrettyPrinterStrategyFactory.PROPERTY_STRATEGY_IMPL, "org.hibernate.tool.hbm2x.xml.DOM3LSPrettyPrinterStrategy");

        XMLPrettyPrinterStrategy strategy = XMLPrettyPrinterStrategyFactory.newXMLPrettyPrinterStrategy();
        assertTrue(strategy instanceof DOM3LSPrettyPrinterStrategy);
    }

    @Test
    public void testStrategyFromSystemPropertyThrowsExceptionWhenClassNotFound() {
        System.setProperty(XMLPrettyPrinterStrategyFactory.PROPERTY_STRATEGY_IMPL, "org.hibernate.tool.hbm2x.xml.BlahBlahStrategy");

        thrown.expect(RuntimeException.class);
        thrown.expectCause(instanceOf(ClassNotFoundException.class));

        XMLPrettyPrinterStrategyFactory.newXMLPrettyPrinterStrategy();
    }

    @Test
    public void testStrategyFromSystemPropertyThrowsExceptionWhenNoDefaultConstructor() {
        System.setProperty(XMLPrettyPrinterStrategyFactory.PROPERTY_STRATEGY_IMPL, "org.hibernate.tool.hbm2x.xml.NoDefaultConstructorStrategy");

        thrown.expect(RuntimeException.class);
        thrown.expectCause(instanceOf(InstantiationException.class));

        XMLPrettyPrinterStrategyFactory.newXMLPrettyPrinterStrategy();
    }

}