package dssb.objectprovider.impl;

import dssb.objectprovider.api.IProvideObject;

@FunctionalInterface
public interface IBind<TYPE> {
    
    public TYPE get(IProvideObject objectProvider);
    
}
