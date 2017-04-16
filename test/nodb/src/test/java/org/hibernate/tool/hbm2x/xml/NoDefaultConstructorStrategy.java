package org.hibernate.tool.hbm2x.xml;

class NoDefaultConstructorStrategy implements XMLPrettyPrinterStrategy {

    public NoDefaultConstructorStrategy(String foo) {
    }

    @Override
    public String prettyPrint(String xml) throws Exception {
        return xml;
    }
}