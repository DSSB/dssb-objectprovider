package dssb.objectprovider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import dssb.objectprovider.api.IProvideObject;
import dssb.objectprovider.impl.bindings.FactoryBinding;
import dssb.objectprovider.impl.bindings.InstanceBinding;
import dssb.objectprovider.impl.bindings.TypeBinding;
import lombok.val;

public class BindingTest {
    
    @Test
    public void testInstanceBinding() {
        val expectedString = "I am a string.";
        
        val bindings = new Bindings.Builder()
                .bind(String.class, new InstanceBinding<>(expectedString))
                .build();
        val provider = new ObjectProvider.Builder().bingings(bindings).build();
        
        assertEquals(expectedString, provider.get(String.class));
    }
    
    public static class MyRunnable implements Runnable {
        @Override
        public void run() {}
    }
    
    @Test
    public void testTypeBinding() {
        val expectedClass = MyRunnable.class;
        
        val bindings = new Bindings.Builder()
                .bind(Runnable.class, new TypeBinding<>(MyRunnable.class))
                .build();
        val provider = new ObjectProvider.Builder().bingings(bindings).build();
        
        assertTrue(expectedClass.isInstance(provider.get(Runnable.class)));
    }
    
    public static class IntegerFactory implements ICreateObject<Integer> {
        private AtomicInteger integer = new AtomicInteger(0);
        @Override
        public Integer create(IProvideObject objectprovider) {
            return integer.getAndIncrement();
        }
        
    }
    
    @Test
    public void testFactoryBinding() {
        val bindings = new Bindings.Builder()
                .bind(Integer.class, new FactoryBinding<>(new IntegerFactory()))
                .build();
        val provider = new ObjectProvider.Builder().bingings(bindings).build();
        
        assertTrue(0 == provider.get(Integer.class));
        assertTrue(1 == provider.get(Integer.class));
        assertTrue(2 == provider.get(Integer.class));
    }
    
}
