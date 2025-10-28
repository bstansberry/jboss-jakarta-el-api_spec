package org.jboss.el.cache;

import static org.jboss.el.cache.ImportHandlerCache.hasClassloadingMiss;
import static org.jboss.el.cache.ImportHandlerCache.clearClassLoader;

import jakarta.el.ImportHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImportHandlerCacheTestCase {

    /** The classname fragment we pass into ImportHandler */
    private static final String NON_EXISTENT = "NONEXISTENT";
    /** The FQCN ImportHandler will cache if it can't be loaded */
    private static final String PACKAGED_NON_EXISTENT = "java.lang." + NON_EXISTENT;

    @Test
    public void testImportHandlerClassLoadingMissHandling() {
        ClassLoader originalTccl = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader testCL = ImportHandlerCache.class.getClassLoader();
            Thread.currentThread().setContextClassLoader(testCL);

            Assertions.assertFalse(hasClassloadingMiss(testCL, PACKAGED_NON_EXISTENT));

            ImportHandler importHandler = new ImportHandler();
            Assertions.assertNull(importHandler.resolveClass(NON_EXISTENT));

            Assertions.assertTrue(hasClassloadingMiss(testCL, PACKAGED_NON_EXISTENT));

            // Exercise the ImportHandler code path when there is a cached miss
            Assertions.assertNull(importHandler.resolveClass(NON_EXISTENT));
            Assertions.assertTrue(hasClassloadingMiss(testCL, PACKAGED_NON_EXISTENT));

            clearClassLoader(testCL);

            Assertions.assertFalse(hasClassloadingMiss(testCL, PACKAGED_NON_EXISTENT));

            importHandler = new ImportHandler();
            Assertions.assertNull(importHandler.resolveClass(NON_EXISTENT));

            Assertions.assertTrue(hasClassloadingMiss(testCL, PACKAGED_NON_EXISTENT));

        } finally {
            Thread.currentThread().setContextClassLoader(originalTccl);
        }
    }
}
