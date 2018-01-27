package dssb.objectprovider.impl;

import org.junit.Ignore;
import org.junit.Test;

import dssb.objectprovider.impl.exception.CyclicDependencyDetectedException;

public class CyclicDependencyDetectionTest {
    
    private ObjectProvider provider = new ObjectProvider();
    
    public static class Cyclic1 {
        
        public Cyclic1(Cyclic1 another) {
        }
    }
    
    @Ignore("Entanglement")
    @Test(expected=CyclicDependencyDetectedException.class)
    public void testThat_whenDefaultConstructorAskForItself_expectCyclicDependencyDetectedException() {
        provider.get(Cyclic1.class);
    }
    
}
