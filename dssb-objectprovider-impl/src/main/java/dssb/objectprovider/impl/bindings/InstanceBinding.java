package dssb.objectprovider.impl.bindings;

import dssb.objectprovider.api.IProvideObject;
import dssb.objectprovider.impl.IBind;

public class InstanceBinding<TYPE> implements IBind<TYPE> {
    
    private final TYPE instance;
    
    public InstanceBinding(TYPE instance) {
        this.instance = instance;
    }
    
    @Override
    public TYPE get(IProvideObject objectProvider) {
        return this.instance;
    }
    
}
