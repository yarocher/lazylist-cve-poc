package poc.cve.lazylist.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ReflectionUtil {
    public static <T> T newInstance(String className, Class[] types, Object... args) {
        try {
            Constructor constructor = Class.forName(className).getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return (T)constructor.newInstance(args);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T newInstance(String className, Object... args) {
        try {
            Constructor constructor = Class.forName(className).getDeclaredConstructor(args2types(args));
            constructor.setAccessible(true);
            return (T)constructor.newInstance(args);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T newInstance(Class clazz, Object... args) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor(args2types(args));
            constructor.setAccessible(true);
            return (T)constructor.newInstance(args);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    public static void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getStaticField(String className, String fieldName) {
        try {
            Class clazz = Class.forName(className);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(clazz);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    public static Class[] args2types(Object[] args) {
        Class[] types = new Class[args.length];
        for (int i = 0; i < types.length; i++) {
            if (args[i] != null) {
                types[i] = args[i].getClass();
            }
            else {
                // Class.class by default
                types[i] = Class.class;
            }
        }
        return types;
    }
}
