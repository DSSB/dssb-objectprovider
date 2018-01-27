package dssb.objectprovider.impl;

@FunctionalInterface
public interface IHandleProvideFailure {
    
    public <T> T handle(Class<T> theGivenClass);
    
}
