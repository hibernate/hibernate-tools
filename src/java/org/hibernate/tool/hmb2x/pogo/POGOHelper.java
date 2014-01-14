package org.hibernate.tool.hmb2x.pogo;

/**
 * 
 * @author Rand McNeely
 *
 */
public class POGOHelper {
    public String transformStaticArrayInitializer(String val) {
        return val.replaceAll("\\{", "[").replaceAll("\\}", "]");
    }
    
    public String joinStatements(String val) {
        String lines[] = val.split("\n");
        StringBuilder buff = new StringBuilder();
        for (String line : lines) {
            buff.append(line).append(" ");
        }
        return buff.toString();
    }

    public String convertFieldModifer(String fieldModifier) {
        return "private".equals(fieldModifier) ? "" : fieldModifier;
    }
}
