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
