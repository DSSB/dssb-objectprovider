package dssb.objectprovider.impl;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.unmodifiableList;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dssb.failable.Failable.Supplier;
import dssb.objectprovider.api.IProvideObject;
import dssb.objectprovider.api.ProvideObjectException;
import dssb.objectprovider.impl.exception.AbstractClassCreationException;
import dssb.objectprovider.impl.exception.CyclicDependencyDetectedException;
import dssb.objectprovider.impl.exception.ObjectCreationException;
import dssb.objectprovider.impl.strategies.ConstructorSupplierFinder;
import dssb.objectprovider.impl.strategies.DefaultInterfaceSupplierFinder;
import dssb.objectprovider.impl.strategies.DefautImplementationSupplierFinder;
import dssb.objectprovider.impl.strategies.EnumValueSupplierFinder;
import dssb.objectprovider.impl.strategies.FactoryMethodSupplierFinder;
import dssb.objectprovider.impl.strategies.IFindSupplier;
import dssb.objectprovider.impl.strategies.NullSupplierFinder;
import dssb.objectprovider.impl.strategies.SingletonFieldFinder;
import dssb.utils.common.Nulls;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;

/**
 * This utility class can create an object using Get.
 * 
 * @author NawaMan
 */
@ExtensionMethod({ Nulls.class })
public class ObjectProvider implements IProvideObject {
    
    // Stepping stone
//    public static final ObjectProvider instance = new ObjectProvider();
    
    // TODO - Add default factory.
    // TODO - Should create interface with all default method.
    // TODO - Should check for @NotNull
    
    @SuppressWarnings("rawtypes")
    private static final Supplier NoSupplier = ()->null;
    
    
    private static final List<IFindSupplier> classLevelfinders = Arrays.asList(
            new DefautImplementationSupplierFinder(),
            new NullSupplierFinder(),
            new EnumValueSupplierFinder(),
            new DefaultInterfaceSupplierFinder()
    );
    private static final List<IFindSupplier> elementLevelfinders = Arrays.asList(
            new SingletonFieldFinder(),
            new FactoryMethodSupplierFinder(),
            new ConstructorSupplierFinder()
    );
    
    @SuppressWarnings("rawtypes")
    private static final ThreadLocal<Set<Class>> beingCreateds = ThreadLocal.withInitial(()->new HashSet<>());
    
    @SuppressWarnings("unchecked")
    private static final List<IFindSupplier> noAdditionalSuppliers = (List<IFindSupplier>)EMPTY_LIST;
    
    private static final Bindings noBinding = new Bindings.Builder().build();
    
    private IProvideObject       parent;
    private List<IFindSupplier>  finders;
    private IHandleLocateFailure locateFailureHandler;
    
    private Bindings binidings;
    
    @SuppressWarnings("rawtypes")
    private Map<Class, Supplier> suppliers = new ConcurrentHashMap<Class, Supplier>();
    
    private List<IFindSupplier>  additionalSupplierFinders;
    
    public ObjectProvider() {
        this(null, null, null, null);
    }
    
    @SuppressWarnings("rawtypes")
    public ObjectProvider(
            IProvideObject       parent,
            List<IFindSupplier>  additionalSupplierFinders,
            Bindings             bingings,
            IHandleLocateFailure locateFailureHandler) {
        this.parent               = parent;
        this.finders              = combineFinders(additionalSupplierFinders);
        this.locateFailureHandler = locateFailureHandler;
        this.binidings            = bingings.or(noBinding);
        
        // Supportive
        this.additionalSupplierFinders = additionalSupplierFinders;
    }
    
    // TODO - Pipeable
    @Setter
    @AllArgsConstructor
    @Accessors(fluent=true,chain=true)
    public static class Builder {
        private IProvideObject       parent;
        private List<IFindSupplier>  additionalSupplierFinders;
        private Bindings             bingings;
        private IHandleLocateFailure locateFailureHandler;
        
        public Builder() {
            this(null, null, null, null);
        }
        
        public ObjectProvider build() {
            return new ObjectProvider(parent, additionalSupplierFinders, bingings, locateFailureHandler);
        }
    }
    
    private static List<IFindSupplier> combineFinders(List<IFindSupplier> additionalSupplierFinders) {
        val finderList = new ArrayList<IFindSupplier>();
        finderList.addAll(classLevelfinders);
        finderList.addAll(additionalSupplierFinders.or(noAdditionalSuppliers));
        finderList.addAll(elementLevelfinders);
        return unmodifiableList(finderList);
    }
    
    public ObjectProvider withNewCache() {
        return new ObjectProvider(parent, additionalSupplierFinders, binidings, locateFailureHandler);
    }
    
    public ObjectProvider withSharedCache() {
        return new ObjectProvider(parent, additionalSupplierFinders, binidings, locateFailureHandler);
    }
    
    public ObjectProvider wihtLocateFailureHandler(IHandleLocateFailure locateFailureHandler) {
        return new ObjectProvider(parent, additionalSupplierFinders, binidings, locateFailureHandler);
    }
    
    public ObjectProvider wihtBindings(Bindings binidings) {
        return new ObjectProvider(parent, additionalSupplierFinders, binidings, locateFailureHandler);
    }
    
    /**
     * Create an instance of the given class.
     * 
     * @param theGivenClass
     * @return the created value.
     * @throws LocateObjectException when there is a problem locating the object.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public <TYPE> TYPE get(Class<TYPE> theGivenClass) throws ProvideObjectException {
        val set = beingCreateds.get();
        if (set.contains(theGivenClass))
            throw new CyclicDependencyDetectedException(theGivenClass);
        
        try {
            set.add(theGivenClass);
            
            try {
                val supplier = getSupplierFor(theGivenClass);
                val instance = supplier.get();
                return theGivenClass.cast(instance);
            } catch (ObjectCreationException e) {
                throw e;
            } catch (Throwable e) {
                throw new ObjectCreationException(theGivenClass, e);
            }
        } finally {
            set.remove(theGivenClass);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    <TYPE, THROWABLE extends Throwable> Supplier<TYPE, THROWABLE> getSupplierFor(
            Class<TYPE> theGivenClass) {
        
        Supplier supplier = suppliers.get(theGivenClass);
        if (supplier.isNull()) {
            supplier = newSupplierFor(theGivenClass);
            supplier = supplier.or(NoSupplier);
            suppliers.put(theGivenClass, supplier);
        }
        return supplier;
    }
    
    @SuppressWarnings({ "rawtypes" })
    private <T> Supplier newSupplierFor(Class<T> theGivenClass) {
        val binding = this.binidings.getBinding(theGivenClass);
        if (binding.isNotNull())
            return ()->binding.get(this);
        
        if (ObjectProvider.class.isAssignableFrom(theGivenClass))
            return ()->this;
        
        val parentProvider = (IProvideObject)this.parent.or(this);
        for (val finder : finders) {
            val supplier = finder.find(theGivenClass, parentProvider);
            if (supplier.isNotNull())
                return supplier;
        }
        
        if (IProvideObject.class.isAssignableFrom(theGivenClass))
            return ()->this;
        
        return ()->handleLoateFailure(theGivenClass);
    }
    
    private <T> Object handleLoateFailure(Class<T> theGivenClass) {
        if (this.locateFailureHandler.isNotNull()) {
            return callHandler(theGivenClass);
        } else {
            return defaultHandling(theGivenClass);
        }
    }
    private <T> Object callHandler(Class<T> theGivenClass) {
        T value = this.locateFailureHandler.handle(theGivenClass);
        return value;
    }
    private <T> Object defaultHandling(Class<T> theGivenClass) {
        if (theGivenClass.isInterface()
         || Modifier.isAbstract(theGivenClass.getModifiers()))
            throw new AbstractClassCreationException(theGivenClass);
        
        return null;
    }
    
}
