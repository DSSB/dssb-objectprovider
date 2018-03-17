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

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.Optional;

import dssb.objectprovider.api.IProvideObject;
import dssb.objectprovider.impl.utils.AnnotationUtils;
import lombok.val;
import lombok.experimental.ExtensionMethod;
import nawaman.failable.Failable.Supplier;
import nawaman.nullablej.NullableJ;
import nawaman.nullablej.nullable.Nullable;

/**
 * This class provides value from the singleton field
 * 
 * <ul>
 *  <li>the static final field</li>
 *  <li>the same type, the Optional of the type, the Nullable of the type or the Supplier of the type</li>
 *  <li>annotated with @Default</li>
 * </ul>
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
@ExtensionMethod({ NullableJ.class, AnnotationUtils.class })
public class SingletonFieldFinder implements IFindSupplier {

    @SuppressWarnings({ "unchecked" })
    @Override
    public <TYPE, THROWABLE extends Throwable> Supplier<TYPE, THROWABLE> find(
            Class<TYPE>    theGivenClass,
            IProvideObject objectProvider) {
        Supplier<TYPE, THROWABLE> fieldValue = findValueFromSingletonField(theGivenClass);
        if (fieldValue._isNotNull())
            return (Supplier<TYPE, THROWABLE>) fieldValue;
        
        return null;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T> Supplier findValueFromSingletonField(Class<T> theGivenClass) {
        return (Supplier)stream(theGivenClass.getDeclaredFields())
                .filter(field->Modifier.isStatic(field.getModifiers()))
                .filter(field->Modifier.isPublic(field.getModifiers()))
                .filter(field->AnnotationUtils.has(field.getAnnotations(), "Default"))
                .map(field->{
                    val type = field.getType();
                    if (theGivenClass.isAssignableFrom(type))
                        return (Supplier)(()->field.get(theGivenClass));
                    
                    if (Optional.class.isAssignableFrom(type)) {
                        val parameterizedType = (ParameterizedType)field.getGenericType();
                        val actualType        = (Class)parameterizedType.getActualTypeArguments()[0];
                        
                        if (theGivenClass.isAssignableFrom(actualType))
                            return (Supplier)(()->((Optional)field.get(theGivenClass)).orElse(null));
                    }
                    if (Nullable.class.isAssignableFrom(type)) {
                        val parameterizedType = (ParameterizedType)field.getGenericType();
                        val actualType        = (Class)parameterizedType.getActualTypeArguments()[0];
                        
                        if (theGivenClass.isAssignableFrom(actualType))
                            return (Supplier)(()->((Nullable)field.get(theGivenClass)).orElse(null));
                    }
                    
                    if (java.util.function.Supplier.class.isAssignableFrom(type)) {
                        val parameterizedType = (ParameterizedType)field.getGenericType();
                        val actualType        = (Class)parameterizedType.getActualTypeArguments()[0];
                        
                        if (theGivenClass.isAssignableFrom(actualType))
                            return (Supplier)()->((java.util.function.Supplier)field.get(theGivenClass)).get();
                    }
                    
                    return null;
                })
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }
    
}
