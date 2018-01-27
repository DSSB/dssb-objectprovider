package dssb.objectprovider.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dssb.objectprovider.api.IProvideObject;

public class DefaultLocatorTest {
    
    @Test
    public void testDefaultLocator() {
        assertTrue(ObjectProvider.class.isInstance(IProvideObject.defaultProvider().get()));
    }
    
}
