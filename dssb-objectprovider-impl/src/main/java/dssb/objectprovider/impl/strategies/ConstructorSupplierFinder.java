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
import java.lang.reflect.Modifier;

import dssb.failable.Failable.Supplier;
import dssb.objectprovider.api.IProvideObject;
import dssb.objectprovider.impl.annotations.Inject;
import dssb.objectprovider.impl.exception.ObjectCreationException;
import dssb.utils.common.Nulls;
import lombok.val;
import lombok.experimental.ExtensionMethod;

/**
 * This class get an object by invoking a constructor.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
@ExtensionMethod({ Nulls.class, extensions.class })
public class ConstructorSupplierFinder extends MethodSupplierFinder implements IFindSupplier {
    
    public static final String INJECT = Inject.class.getSimpleName();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <TYPE, THROWABLE extends Throwable> Supplier<TYPE, THROWABLE> find(
            Class<TYPE>    theGivenClass,
            IProvideObject objectProvider) {
        val constructor = findConstructor(theGivenClass);
        if (constructor.isNotNull()) {
            val supplier = new Supplier() {
                public Object get() throws Throwable {
                    return callConstructor(theGivenClass, constructor, objectProvider);
                }
            };
            return (Supplier<TYPE, THROWABLE>) supplier;
        }
        return null;
    }
    
    @SuppressWarnings({ "rawtypes" })
    private <T> Constructor findConstructor(Class<T> clzz) {
        Constructor foundConstructor = findConstructorWithInject(clzz);
        if (foundConstructor.isNull()) {
            foundConstructor = hasOnlyOneConsructor(clzz)
                    ? getOnlyConstructor(clzz)
                    : getNoArgConstructor(clzz);
        }
        
        if (foundConstructor.isNull()
         || !Modifier.isPublic(foundConstructor.getModifiers()))
            return null;
        
        return foundConstructor;
    }
    
    @SuppressWarnings("rawtypes")
    private <T> Object callConstructor(Class<T> theGivenClass, Constructor constructor, IProvideObject objectProvider)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        val params   = getParameters(constructor, objectProvider);
        val instance = constructor.newInstance(params);
        // TODO - Change to use method handle later.
        return theGivenClass.cast(instance);
    }
    
    @SuppressWarnings({ "rawtypes" })
    private Object[] getParameters(Constructor constructor, IProvideObject objectProvider) {
        val paramsArray = constructor.getParameters();
        val params = new Object[paramsArray.length];
        for (int i = 0; i < paramsArray.length; i++) {
            val param             = paramsArray[i];
            val paramType         = param.getType();
            val parameterizedType = param.getParameterizedType();
            // TODO - The method itself can be set to @Nullable or @NotNull
            //          the at the parameter level, we can flag it reversely.
            boolean isNullable    = param.getAnnotations().hasAnnotation("Nullable");
            Object paramValue     = getParameterValue(paramType, parameterizedType, isNullable, objectProvider);
            params[i] = paramValue;
        }
        return params;
    }
    
    public static <T> boolean hasOnlyOneConsructor(final java.lang.Class<T> clzz) {
        return clzz.getConstructors().length == 1;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> findConstructorWithInject(Class<T> clzz) {
        for(Constructor<?> constructor : clzz.getConstructors()) {
            if (!Modifier.isPublic(constructor.getModifiers()))
                continue;
            
            if (extensions.hasAnnotation(constructor.getAnnotations(), INJECT))
                return (Constructor<T>)constructor;
        }
        return null;
    }
    
    public static <T> Constructor<T> getNoArgConstructor(Class<T> clzz) {
        try {
            return clzz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e) {
            throw new ObjectCreationException(clzz);
        }
    }
    
    public static <T> Constructor<?> getOnlyConstructor(Class<T> clzz) {
        return clzz.getConstructors()[0];
    }
    
}
