package dssb.objectprovider.impl.bindings;

import dssb.objectprovider.api.IProvideObject;
import dssb.objectprovider.impl.IBind;
import lombok.val;

public class TypeBinding<TYPE> implements IBind<TYPE> {
    
    private final Class<? extends TYPE> referedType;
    
    public TypeBinding(Class<? extends TYPE> referedType) {
        this.referedType = referedType;
    }
    
    @Override
    public TYPE get(IProvideObject objectProvider) {
        val value = (TYPE)objectProvider.get(referedType);
        return value;
    }
    
}
