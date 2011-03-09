package org.hibernate.tool.ide.completion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for completion.
 * Package private, not to be used externally.
 *  
 * @author leon, max.andersen@jboss.com
 */
class CompletionHelper {
    
    private CompletionHelper() {
    }
    
    public static String getCanonicalPath(List qts, String name) {
        Map alias2Type = new HashMap();
        for (Iterator iter = qts.iterator(); iter.hasNext();) {
			EntityNameReference qt = (EntityNameReference) iter.next();
            alias2Type.put(qt.getAlias(), qt.getEntityName());
        }
        if (qts.size() == 1) { 
            EntityNameReference visible = (EntityNameReference) qts.get(0);
            String alias = visible.getAlias();
            if (name.equals(alias)) {
                return visible.getEntityName();
            } else if (alias == null || alias.length() == 0 || alias.equals(visible.getEntityName())) {
                return visible.getEntityName() + "/" + name;
            }
        }
        return getCanonicalPath(new HashSet(), alias2Type, name);
    }
    
    
    private static String getCanonicalPath(Set resolved, Map alias2Type, String name) {
        if (resolved.contains(name)) {
            // To prevent a stack overflow
            return name;
        }
        resolved.add(name);
        String type = (String) alias2Type.get(name);
        if (type != null) {
            return name.equals(type) ? name : getCanonicalPath(resolved, alias2Type, type);
        }
        int idx = name.lastIndexOf('.');
        if (idx == -1) {
            return type != null ? type : name;
        }
        String baseName = name.substring(0, idx);
        String prop = name.substring(idx + 1);
        if (isAliasNown(alias2Type, baseName)) {
            return getCanonicalPath(resolved, alias2Type, baseName) + "/" + prop;
        } else {
            return name;
        }
    }
    
    private static boolean isAliasNown(Map alias2Type, String alias) {
        if (alias2Type.containsKey(alias)) {
            return true;
        }
        int idx = alias.lastIndexOf('.');
        if (idx == -1) {
            return false;
        }
        return isAliasNown(alias2Type, alias.substring(0, idx));
    }    
    
}
