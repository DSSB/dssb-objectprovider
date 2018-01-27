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

import dssb.failable.Failable.Supplier;
import dssb.objectprovider.api.IProvideObject;
import dssb.objectprovider.impl.exception.ObjectCreationException;
import lombok.val;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ extensions.class })
public class EnumValueSupplierFinder implements IFindSupplier {

    @Override
    public <TYPE, THROWABLE extends Throwable> Supplier<TYPE, THROWABLE> find(
            Class<TYPE>    theGivenClass,
            IProvideObject objectProvider) {
        if (!theGivenClass.isEnum()) 
            return null;
        
        val enumValue = findDefaultEnumValue(theGivenClass);
        return ()->enumValue;
    }
    
    private static <T> T findDefaultEnumValue(Class<T> theGivenClass) {
        T[] enumConstants = theGivenClass.getEnumConstants();
        if (enumConstants.length == 0)
            return null;
        return stream(enumConstants)
                .filter(value->checkDefaultEnumValue(theGivenClass, value))
                .findAny()
                .orElse(enumConstants[0]);
    }
    
    @SuppressWarnings("rawtypes")
    private static <T> boolean checkDefaultEnumValue(Class<T> theGivenClass, T value) {
        val name = ((Enum)value).name();
        try {
            return theGivenClass.getField(name).getAnnotations().hasAnnotation("Default");
        } catch (NoSuchFieldException | SecurityException e) {
            throw new ObjectCreationException(theGivenClass, e);
        }
    }
    
}
