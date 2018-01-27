package dssb.objectprovider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Optional;

import org.junit.Test;

public class SingletonFieldOfOptionalTest {
    
    private ObjectProvider provider = new ObjectProvider();
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Default {
        
    }
    
    public static class OptionalSingleton {
        @Default
        public static final Optional<OptionalSingleton> instance = Optional.of(new OptionalSingleton());
        
        private OptionalSingleton() {}
    }
    
    @Test
    public void testThat_optionalSingletonClassWithDefaultAnnotationHasTheInstanceAsTheValue() {
        OptionalSingleton value = provider.get(OptionalSingleton.class);
        assertEquals(OptionalSingleton.instance.get(), value);
    }
    
    
    public static class EmptyOptionalSingleton {
        @Default
        public static final Optional<EmptyOptionalSingleton> instance = Optional.empty();
        
        private EmptyOptionalSingleton() {}
    }
    
    @Test
    public void testThat_optionalSingletonClassWithDefaultAnnotationHasTheInstanceAsTheValue_empty() {
        EmptyOptionalSingleton value = provider.get(EmptyOptionalSingleton.class);
        assertEquals(EmptyOptionalSingleton.instance.orElse(null), value);
        assertNull(value);
    }
    
}
