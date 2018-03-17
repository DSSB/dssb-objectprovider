//  ========================================================================
//  Copyright (c) 2017 Direct Solution Software Builders (DSSB).
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
package dssb.objectprovider.impl.strategies;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dssb.objectprovider.api.IProvideObject;
import dssb.objectprovider.impl.annotations.Inject;
import dssb.objectprovider.impl.utils.AnnotationUtils;
import dssb.objectprovider.impl.utils.ConstructorUtils;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import nawaman.failable.Failable.Supplier;
import nawaman.failable.Failables;
import nawaman.nullablej.NullableJ;

/**
 * This class get an object by invoking a constructor.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
@ExtensionMethod({
    NullableJ.class,
    ConstructorUtils.class,
    AnnotationUtils.class
})
public class ConstructorSupplierFinder extends MethodSupplierFinder implements IFindSupplier {
    
    /** The name of the Inject annotation */
    public static final String INJECT = Inject.class.getSimpleName();
    
    @Override
    public <TYPE, THROWABLE extends Throwable> Supplier<TYPE, THROWABLE>
            find(
                Class<TYPE>    theGivenClass,
                IProvideObject objectProvider) {
        val constructor = findConstructor(theGivenClass);
        if (constructor._isNull())
            return null;
        
        @SuppressWarnings({"unchecked"})
        val supplier = (Supplier<TYPE, THROWABLE>) Failables.of(()->{
            val value = callConstructor(constructor, objectProvider);
            return value;
        });
        return supplier;
    }
    
    private <T> Constructor<T> findConstructor(Class<T> clzz) {
        Constructor<T> foundConstructor
                = clzz.findConstructorWithAnnotation(INJECT)
                ._orGet(sensibleDefaultConstructorOf(clzz));
        
        if(foundConstructor._isPublic())
            return foundConstructor;
        
        return null;
    }
    
    @SuppressWarnings("unused")
    private <T> java.util.function.Supplier<Constructor<T>>
            sensibleDefaultConstructorOf(Class<T> clzz) {
        return ()->
                clzz.hasOnlyOneConsructor()
                ? clzz.getOnlyConstructor()
                : clzz.getNoArgConstructor();
    }
    
    private <T> Object callConstructor(Constructor<T> constructor, IProvideObject objectProvider)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        // TODO - Change to use method handle.
        val paramsArray = constructor.getParameters();
        val paramValues = getParameters(paramsArray, objectProvider);
        val instance    = constructor.newInstance(paramValues);
        return (T)instance;
    }
    
}
