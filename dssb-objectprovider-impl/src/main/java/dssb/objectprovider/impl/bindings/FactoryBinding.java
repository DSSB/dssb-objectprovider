package dssb.objectprovider.impl.bindings;

import dssb.objectprovider.api.IProvideObject;
import dssb.objectprovider.impl.IBind;
import dssb.objectprovider.impl.ICreateObject;
import lombok.val;

public class FactoryBinding<TYPE> implements IBind<TYPE> {
    
    private final ICreateObject<TYPE> factory;
    
    public FactoryBinding(ICreateObject<TYPE> factory) {
        this.factory = factory;
    }
    
    @Override
    public TYPE get(IProvideObject objectProvider) {
        val value = this.factory.create(objectProvider);
        return value;
    }
    
}
