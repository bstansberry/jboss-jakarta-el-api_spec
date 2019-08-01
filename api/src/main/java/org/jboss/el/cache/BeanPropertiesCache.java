package org.jboss.el.cache;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Stuart Douglas
 */
public class BeanPropertiesCache {

    static private class BPSoftReference extends SoftReference<Object> {
        final Class<?> key;
        BPSoftReference(Class<?> key, Object beanProperties,
                        ReferenceQueue<Object> refQ) {
            super(beanProperties, refQ);
            this.key = key;
        }
    }

    public static class SoftConcurrentHashMap extends
            ConcurrentHashMap<Class<?>, Object> {

        private static final int CACHE_INIT_SIZE = 1024;
        private final ConcurrentHashMap<Class<?>, BPSoftReference> map =
                new ConcurrentHashMap<>(CACHE_INIT_SIZE);
        private final ReferenceQueue<Object> refQ = new ReferenceQueue<>();

        // Remove map entries that have been placed on the queue by GC.
        private void cleanup() {
            BPSoftReference BPRef = null;
            while ((BPRef = (BPSoftReference)refQ.poll()) != null) {
                map.remove(BPRef.key);
            }
        }

        protected void clear(ClassLoader classLoader) {
            Iterator<Map.Entry<Class<?>, BPSoftReference>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Class<?>, BPSoftReference> entry = it.next();
                if(entry.getKey().getClassLoader() == classLoader) {
                    it.remove();
                }
            }

        }

        @Override
        public Object put(Class<?> key, Object value) {
            cleanup();
            BPSoftReference prev =
                    map.put(key, new BPSoftReference(key, value, refQ));
            return prev == null? null: prev.get();
        }

        @Override
        public Object putIfAbsent(Class<?> key, Object value) {
            cleanup();
            BPSoftReference prev =
                    map.putIfAbsent(key, new BPSoftReference(key, value, refQ));
            return prev == null? null: prev.get();
        }

        @Override
        public Object get(Object key) {
            cleanup();
            BPSoftReference BPRef = map.get(key);
            if (BPRef == null) {
                return null;
            }
            if (BPRef.get() == null) {
                // value has been garbage collected, remove entry in map
                map.remove(key);
                return null;
            }
            return BPRef.get();
        }
    }

    /**
     * sfot references are horrible
     */
    private static final SoftConcurrentHashMap properties =
            new SoftConcurrentHashMap();


    /*
     * Get a public method form a public class or interface of a given method.
     * Note that if a PropertyDescriptor is obtained for a non-public class that
     * implements a public interface, the read/write methods will be for the
     * class, and therefore inaccessible.  To correct this, a version of the
     * same method must be found in a superclass or interface.
     **/

    public static Method getMethod(Class<?> type, Object base, Method m) {
        // If base is null, method MUST be static
        // If base is non-null, method may be static or non-static
        if (m == null ||
                (Modifier.isPublic(type.getModifiers()) &&
                        (canAccess(base, m) || base != null && canAccess(null, m)))) {
            return m;
        }
        Class<?>[] inf = type.getInterfaces();
        Method mp = null;
        for (int i = 0; i < inf.length; i++) {
            try {
                mp = inf[i].getMethod(m.getName(), m.getParameterTypes());
                mp = getMethod(mp.getDeclaringClass(), base, mp);
                if (mp != null) {
                    return mp;
                }
            } catch (NoSuchMethodException e) {
                // Ignore
            }
        }
        Class<?> sup = type.getSuperclass();
        if (sup != null) {
            try {
                mp = sup.getMethod(m.getName(), m.getParameterTypes());
                mp = getMethod(mp.getDeclaringClass(), base, mp);
                if (mp != null) {
                    return mp;
                }
            } catch (NoSuchMethodException e) {
                // Ignore
            }
        }
        return null;
    }


    static boolean canAccess(Object base, AccessibleObject accessibleObject) {
        try {
            return accessibleObject.canAccess(base);
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    public static SoftConcurrentHashMap getProperties() {
        return properties;
    }

    static void clear(ClassLoader classLoader) {
        properties.clear(classLoader);
    }
}
