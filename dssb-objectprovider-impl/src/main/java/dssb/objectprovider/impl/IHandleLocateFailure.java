package dssb.objectprovider.impl;

@FunctionalInterface
public interface IHandleLocateFailure {
    
    public <T> T handle(Class<T> theGivenClass);
    
}
