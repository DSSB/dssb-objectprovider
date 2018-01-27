package dssb.objectprovider.impl.strategies;

import static dssb.objectprovider.impl.strategies.common.NullSupplier;

import dssb.failable.Failable.Supplier;
import dssb.objectprovider.api.IProvideObject;
import dssb.objectprovider.impl.annotations.DefaultToNull;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ extensions.class })
public class NullSupplierFinder implements IFindSupplier {
    
    public static final String ANNOTATION_NAME = DefaultToNull.class.getSimpleName();
    
    @SuppressWarnings("unchecked")
    @Override
    public <TYPE, THROWABLE extends Throwable> Supplier<TYPE, THROWABLE> find(
            Class<TYPE>    theGivenClass,
            IProvideObject objectProvider) {
        return theGivenClass.getAnnotations().hasAnnotation(ANNOTATION_NAME)
                ? (Supplier<TYPE, THROWABLE>)NullSupplier
                : null;
    }
    
}
