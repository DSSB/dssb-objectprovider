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

import static java.util.Arrays.stream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import dssb.failable.Failable.Supplier;
import dssb.objectprovider.api.IProvideObject;
import dssb.utils.common.UNulls;
import lombok.val;
import lombok.experimental.ExtensionMethod;

/**
 * This class returns object resulting from a factory method.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
@ExtensionMethod({ UNulls.class, extensions.class })
public class FactoryMethodSupplierFinder extends MethodSupplierFinder implements IFindSupplier {

    @SuppressWarnings({ "unchecked" })
    @Override
    public <TYPE, THROWABLE extends Throwable> Supplier<TYPE, THROWABLE> find(
            Class<TYPE>    theGivenClass,
            IProvideObject objectProvider) {
        val methodValue = findValueFromFactoryMethod(theGivenClass, objectProvider);
        if (methodValue.isNotNull())
            return (Supplier<TYPE, THROWABLE>)methodValue;
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private <T> Supplier<T, ? extends Throwable> findValueFromFactoryMethod(Class<T> theGivenClass, IProvideObject objectProvider) {
        return (Supplier<T, ? extends Throwable>)stream(theGivenClass.getDeclaredMethods())
                .filter(method->Modifier.isStatic(method.getModifiers()))
                .filter(method->Modifier.isPublic(method.getModifiers()))
                .filter(method->extensions.hasAnnotation(method.getAnnotations(), "Default"))
                .map(method->FactoryMethodSupplierFinder.this.getFactoryMethodValue(theGivenClass, method, objectProvider))
                .findAny()
                .orElse(null);
    }
    
    @SuppressWarnings("rawtypes")
    private <T> Supplier getFactoryMethodValue(Class<T> theGivenClass, Method method, IProvideObject objectProvider) {
        val type = method.getReturnType();
        if (theGivenClass.isAssignableFrom(type))
            return (Supplier)(()->basicFactoryMethodCall(theGivenClass, method, objectProvider));
        
        if (Optional.class.isAssignableFrom(type)) {
            val parameterizedType = (ParameterizedType)method.getGenericReturnType();
            val actualType        = (Class)parameterizedType.getActualTypeArguments()[0];
            
            if (theGivenClass.isAssignableFrom(actualType))
                return (Supplier)(()->optionalFactoryMethodCall(theGivenClass, method, objectProvider));
        }
        
        if (java.util.function.Supplier.class.isAssignableFrom(type)) {
            val parameterizedType = (ParameterizedType)method.getGenericReturnType();
            val actualType        = (Class)parameterizedType.getActualTypeArguments()[0];
            val getMethod         = getGetMethod();
            
            if (theGivenClass.isAssignableFrom(actualType))
                return (Supplier)()->supplierFactoryMethodCall(theGivenClass, method, getMethod, objectProvider);
        }
        
        return null;
    }
    
    private static Method getGetMethod() {
        try {
            // TODO - Change to use MethodHandler.
            return java.util.function.Supplier.class.getMethod("get", new Class[0]);
        } catch (NoSuchMethodException | SecurityException e) {
            // I am sure it is there.
            throw new RuntimeException(e);
        }
    }
    
    private <T> Object supplierFactoryMethodCall(
            Class<T> theGivenClass,
            Method method,
            Method getMethod,
            IProvideObject objectProvider) 
                    throws IllegalAccessException, InvocationTargetException {
        val params   = getMethodParameters(method, objectProvider);
        val result   = method.invoke(theGivenClass, params);
        val value    = getMethod.invoke(result);
        return value;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T> Object optionalFactoryMethodCall(Class<T> theGivenClass, Method method, IProvideObject objectProvider)
            throws IllegalAccessException, InvocationTargetException {
        val params = getMethodParameters(method, objectProvider);
        val value = method.invoke(theGivenClass, params);
        return ((Optional)value).orElse(null);
    }
    
    private <T> Object basicFactoryMethodCall(Class<T> theGivenClass, Method method, IProvideObject objectProvider)
            throws IllegalAccessException, InvocationTargetException {
        val params = getMethodParameters(method, objectProvider);
        val value = method.invoke(theGivenClass, params);
        return value;
    }
    
}
