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

import static dssb.objectprovider.impl.strategies.common.NullSupplier;
import static java.util.Arrays.stream;

import java.util.Objects;

import dssb.failable.Failable.Supplier;
import dssb.objectprovider.api.IProvideObject;
import dssb.utils.common.Nulls;
import lombok.val;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ Nulls.class, extensions.class })
public class DefautImplementationSupplierFinder implements IFindSupplier {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <TYPE, THROWABLE extends Throwable> Supplier<TYPE, THROWABLE> find(
            Class<TYPE>    theGivenClass,
            IProvideObject objectProvider) {
        if (theGivenClass.getAnnotations().hasAnnotation("DefaultImplementation")) {
            val defaultImplementationClass = findDefaultImplementation(theGivenClass);
            if (defaultImplementationClass.isNotNull()) {
                return new Supplier() {
                    @Override
                    public Object get() throws Throwable {
                        return objectProvider.get(defaultImplementationClass);
                    }
                };
            }
            return NullSupplier;
        }
        
        return null;
    }
    
    @SuppressWarnings("rawtypes")
    private static <T> Class findDefaultImplementation(Class<T> theGivenClass) {
        return stream(theGivenClass.getAnnotations())
            .map(Object::toString)
            .map(toString->toString.replaceAll("^(.*\\(value=)(.*)(\\))$", "$2"))
            .map(DefautImplementationSupplierFinder::findClass)
            .filter(Objects::nonNull)
            .filter(theGivenClass::isAssignableFrom)
            .findAny()
            .orElse(null);
    }
    
    /**
     * Find the {@code java.inject.Inject} class by name.
     * @return the class if found or {@code null} if not.
     */
    static Class<?> findClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
}
