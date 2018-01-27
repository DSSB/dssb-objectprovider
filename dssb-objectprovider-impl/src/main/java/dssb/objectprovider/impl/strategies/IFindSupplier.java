package dssb.objectprovider.impl.strategies;

import dssb.failable.Failable.Supplier;
import dssb.objectprovider.api.IProvideObject;

@FunctionalInterface
public interface IFindSupplier {
    
    public <TYPE, THROWABLE extends Throwable> Supplier<TYPE, THROWABLE> find(
            Class<TYPE>    clss,
            IProvideObject objectProvider);
    
}
