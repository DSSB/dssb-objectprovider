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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import dssb.failable.Failable.Supplier;
import dssb.objectprovider.api.IProvideObject;
import dssb.utils.common.Nulls;
import lombok.val;
import lombok.experimental.ExtensionMethod;


// TODO - Change this to composite to inherit
/**
 * Abstract class for supplier finders that get value from constructor or method.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
@ExtensionMethod({ Nulls.class, extensions.class })
public abstract class MethodSupplierFinder implements IFindSupplier {
    
    protected Object[] getMethodParameters(Method method, IProvideObject objectProvider) {
        val paramsArray = method.getParameters();
        val params = new Object[paramsArray.length];
        for (int i = 0; i < paramsArray.length; i++) {
            val param             = paramsArray[i];
            val paramType         = param.getType();
            val parameterizedType = param.getParameterizedType();
            boolean isNullable    = param.getAnnotations().hasAnnotation("Nullable");
            Object  paramValue    = getParameterValue(paramType, parameterizedType, isNullable, objectProvider);
            params[i] = paramValue;
        }
        return params;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object getParameterValue(Class paramType, Type type, boolean isNullable, IProvideObject objectProvider) {
        if (type instanceof ParameterizedType) {
            val parameterizedType = (ParameterizedType)type;
            val actualType        = (Class)parameterizedType.getActualTypeArguments()[0];
            
            if (paramType == Supplier.class)
                return new Supplier() {
                    @Override
                    public Object get() throws Throwable {
                        return objectProvider.get(actualType);
                    }
                };
            
            if (paramType == java.util.function.Supplier.class)
                return new java.util.function.Supplier() {
                    @Override
                    public Object get() {
                        return objectProvider.get(actualType);
                    }
                };
            
            if (paramType == Optional.class)
                return getOptionalValueOrNullWhenFailAndNullable(isNullable, actualType, objectProvider);
        }
        
        if (isNullable)
            return getValueOrNullWhenFail(paramType, objectProvider);
        
        val paramValue = objectProvider.get(paramType);
        return paramValue;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Object getOptionalValueOrNullWhenFailAndNullable(boolean isNullable, Class actualType, IProvideObject objectProvider) {
        try {
            val paramValue = objectProvider.get(actualType);
            return Optional.ofNullable(paramValue);
        } catch (Exception e) {
            return isNullable ? null : Optional.empty();
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Object getValueOrNullWhenFail(Class paramType, IProvideObject objectProvider) {
        try {
            return objectProvider.get(paramType);
        } catch (Exception e) {
            return null;
        }
    }
    
}
