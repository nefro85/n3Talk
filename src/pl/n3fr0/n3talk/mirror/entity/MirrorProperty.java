package pl.n3fr0.n3talk.mirror.entity;

import java.util.List;
import java.util.Map;

public interface MirrorProperty {
    String getName();
    Object getValue();
    boolean isList();
    boolean isMap();
    
    static class Tool {

        public static boolean isList(Class clazz) {
            if (clazz != null) {
                for (Class c : clazz.getInterfaces()) {
                    if (c.equals(List.class)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static boolean isMap(Object object) {
            if (object != null) {
                for (Class c : object.getClass().getInterfaces()) {
                    if (c.equals(Map.class)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
