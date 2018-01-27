package dssb.objectprovider.impl;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

public class FactoryMethodTest {
    
    private ObjectProvider provider = new ObjectProvider();
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Default {
        
    }
    
    public static class BasicFactoryMethod {
        
        private static int counter = 0;
        
        public static final BasicFactoryMethod instance = new BasicFactoryMethod("instance");
        
        private String string;
        
        private BasicFactoryMethod(String string) {
            this.string = string;
        }
        
        @Default
        public static BasicFactoryMethod newInstance() {
            counter++;
            return new BasicFactoryMethod("factory");
        }
    }
    
    @Test
    public void testThat_classWithFactoryMethodDefaultAnnotationHasTheInstanceAsTheValue() {
        int prevCounter = BasicFactoryMethod.counter;
        
        assertEquals("factory", provider.get(BasicFactoryMethod.class).string);
        assertEquals(prevCounter + 1, BasicFactoryMethod.counter);
        
        assertEquals("factory", provider.get(BasicFactoryMethod.class).string);
        assertEquals(prevCounter + 2, BasicFactoryMethod.counter);
    }
    
}
