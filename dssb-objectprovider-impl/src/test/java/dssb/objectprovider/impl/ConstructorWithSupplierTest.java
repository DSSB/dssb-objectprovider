package dssb.objectprovider.impl;

import static org.junit.Assert.assertEquals;

import java.util.function.Supplier;

import org.junit.Test;

import dssb.objectprovider.impl.bindings.InstanceBinding;
import lombok.val;

public class ConstructorWithSupplierTest {
    
    private ObjectProvider provider = new ObjectProvider();
    
    public static class Company {
        private Supplier<Integer> revenueSupplier;
        public Company(Supplier<Integer> revenueSupplier) {
            this.revenueSupplier = revenueSupplier;
        }
        public int revenue() {
            return revenueSupplier.get();
        }
    }
    
    @Test
    public void testThat_withSupplierAsParameter_aSupplierToGetIsGiven() {
        val bindings = new Bindings.Builder()
                .bind(Integer.class, new InstanceBinding<>(10000))
                .build();
        provider = provider.wihtBindings(bindings);
        
        val company = provider.get(Company.class);
        
        assertEquals(10000, company.revenue());
    }
    
}
