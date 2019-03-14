package org.hibernate.tools.test.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liqingsong on 2019/3/14
 */
public class EntityUtil {
    private static final Map<String, Object> defaultFieldValueMap = new ConcurrentHashMap<>();
    private static final Map<String, Object> defaultInstanceMap = new ConcurrentHashMap<>();
    private static final String KEY_SEPERATOR = "$";

    /**
     * Before persisting, set null field with default value.
     *
     * @param t
     * @param <T>
     */
    public static <T> void setDefVal(T t) {
        String className = t.getClass().getName();
        T initObject = (T) (defaultInstanceMap.get(className));

        if (Objects.isNull(initObject)) {
            try {
                initObject = (T) t.getClass().newInstance();
                defaultInstanceMap.put(className, initObject);
            } catch (InstantiationException e) {
                return;
            } catch (IllegalAccessException e) {
                return;
            }
        }

        for (Field f : t.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (null == f.get(t)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(className).append(KEY_SEPERATOR).append(f.getName());
                    Object defaultVal = defaultFieldValueMap.get(stringBuilder.toString());
                    if (Objects.isNull(defaultVal)) {
                        defaultVal = f.get(initObject);
                        defaultFieldValueMap.put(stringBuilder.toString(), defaultVal);
                    }
                    f.set(t, defaultVal);
                }
            } catch (IllegalAccessException e) {
            }
        }
    }
}
