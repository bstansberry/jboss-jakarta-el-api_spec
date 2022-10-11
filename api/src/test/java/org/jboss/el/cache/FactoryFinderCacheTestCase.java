package org.jboss.el.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactoryFinderCacheTestCase {

    @Test
    public void testServiceFileWithComment() {
        String className = FactoryFinderCache.loadImplementationClassName("org.jboss.el.cache.TestService-comment",
                this.getClass().getClassLoader());
        Assertions.assertEquals("org.jboss.el.cache.TestServiceImpl", className);
    }

    @Test
    public void testEmptyServiceFile() {
        String className = FactoryFinderCache.loadImplementationClassName("org.jboss.el.cache.TestService-empty",
                this.getClass().getClassLoader());
        Assertions.assertNull(className);
    }

    @Test
    public void testServiceFile() {
        String className = FactoryFinderCache.loadImplementationClassName("org.jboss.el.cache.TestService",
                this.getClass().getClassLoader());
        Assertions.assertEquals("org.jboss.el.cache.TestServiceImpl", className);
    }

    @Test
    public void testSanitizeMethod() {
        Assertions.assertEquals("ClassName", FactoryFinderCache.sanitize("ClassName"));
        Assertions.assertEquals("ClassName", FactoryFinderCache.sanitize(" ClassName "));
        Assertions.assertEquals("ClassName", FactoryFinderCache.sanitize("\tClassName\t"));
        Assertions.assertEquals("ClassName", FactoryFinderCache.sanitize(" \tClassName\t # comment..."));
        Assertions.assertEquals("", FactoryFinderCache.sanitize("# only comment..."));
        Assertions.assertEquals("", FactoryFinderCache.sanitize(""));
        Assertions.assertNull(FactoryFinderCache.sanitize(null));
    }
}
