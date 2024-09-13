/*
 * Copyright The JBoss Jakarta EL API Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.el;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class BeanELResolverTestCase {
    public static class Greeter {
        public String greet(final String... args) {
            return "Hello " + Arrays.toString(args);
        }
        public String greet2(final String prefix, final String... args) {
            return "Hello " + prefix + ": " + Arrays.toString(args);
        }
    }

    @Test
    public void testVarArgs() {
        final BeanELResolver resolver = new BeanELResolver();
        final ELContext context = new StandardELContext(ExpressionFactory.newInstance());
        final Greeter base = new Greeter();
        final String method = "greet";
        final Class[] paramTypes = new Class[] { String[].class };
        final Object[] params = new Object[] { new String[] { "testVarArgs" } };
        final String result = (String) resolver.invoke(context, base, method, paramTypes, params);
        assertEquals("Hello [testVarArgs]", result);
    }

    @Test
    public void testVarArgs2() {
        final BeanELResolver resolver = new BeanELResolver();
        final ELContext context = new StandardELContext(ExpressionFactory.newInstance());
        final Greeter base = new Greeter();
        final String method = "greet2";
        final Class[] paramTypes = new Class[] { String.class, String[].class };
        final Object[] params = new Object[] { "prefix", new String[] { "testVarArgs2" } };
        final String result = (String) resolver.invoke(context, base, method, paramTypes, params);
        assertEquals("Hello prefix: [testVarArgs2]", result);
    }
    
    /**
     * original test from the bugfix
    */
    @Test
    public void testBug56425() {
        ELProcessor processor = new ELProcessor();
        processor.defineBean("string", "a-b-c-d");
        assertEquals("a_b_c_d", processor.eval("string.replace(\"-\",\"_\")"));
    }
    
    /**
     * test the bugfix following the pattern of other tests
     */
    @Test
    public void testBug56425_2() {
        final BeanELResolver resolver = new BeanELResolver();
        final ELContext context = new StandardELContext(ExpressionFactory.newInstance());
        final String base = "a-b-c-d";
        final String method = "replace";
        final Class[] paramTypes = new Class[] { String.class, String.class };
        final Object[] params = new Object[] { "-", "_" };
        final String result = (String) resolver.invoke(context, base, method, paramTypes, params);
        assertEquals("a_b_c_d", result);
	}
}
