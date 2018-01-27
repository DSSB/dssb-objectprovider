package dssb.objectprovider.impl;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

public class SingletonFieldTest {
    
    private ObjectProvider provider = new ObjectProvider();
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Default {
        
    }
    
    public static class BasicSingleton {
        @Default
        public static final BasicSingleton instance = new BasicSingleton("instance");
        
        private String string;
        
        private BasicSingleton(String string) {
            this.string = string;
        }
        
        @Default
        public static BasicSingleton newInstance() {
            return new BasicSingleton("factory");
        }
    }
    
    @Test
    public void testThat_singletonClassWithDefaultAnnotationHasTheInstanceAsTheValue_withFieldMorePreferThanFactory() {
        assertEquals(BasicSingleton.instance, provider.get(BasicSingleton.class));
        assertEquals("instance", provider.get(BasicSingleton.class).string);
    }
    
}
