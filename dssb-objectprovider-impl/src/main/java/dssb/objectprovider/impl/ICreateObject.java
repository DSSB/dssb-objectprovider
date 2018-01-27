package dssb.objectprovider.impl;

import dssb.objectprovider.api.IProvideObject;

@FunctionalInterface
public interface ICreateObject<TYPE> {
    
    public TYPE create(IProvideObject objectProvider);
    
}
