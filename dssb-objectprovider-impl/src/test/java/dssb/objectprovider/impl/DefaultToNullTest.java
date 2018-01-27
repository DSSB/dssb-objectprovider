package dssb.objectprovider.impl;

import static org.junit.Assert.assertNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

public class DefaultToNullTest {
    
    private ObjectProvider provider = new ObjectProvider();
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DefaultToNull {
        
    }
    
    @DefaultToNull
    public static class NullValue {
        
    }
    
    @Test
    public void testThat_classAnnotatedWithDefaultToNull_hasDefaultValueOfNull() {
        assertNull(provider.get(NullValue.class));
    }
    
    
}
