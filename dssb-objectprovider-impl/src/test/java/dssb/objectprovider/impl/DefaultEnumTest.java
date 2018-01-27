package dssb.objectprovider.impl;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

public class DefaultEnumTest {
    
    private ObjectProvider provider = new ObjectProvider();
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Default {
        
    }
    
    public static enum MyEnum1 { Value1, Value2; }
    
    @Test
    public void testThat_enumValue_isTheFirstValue() {
        assertEquals(MyEnum1.Value1, provider.get(MyEnum1.class));
    }
    
    public static enum MyEnum2 { Value1, @Default Value2; }
    
    @Test
    public void testThat_enumValueWithDefaultAnnotation() {
        assertEquals(MyEnum2.Value2, provider.get(MyEnum2.class));
    }
    
}
