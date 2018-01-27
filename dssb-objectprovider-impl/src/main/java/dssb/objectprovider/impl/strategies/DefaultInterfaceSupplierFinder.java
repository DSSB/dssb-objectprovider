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
import dssb.utils.common.Nulls;
import lombok.experimental.ExtensionMethod;

/**
 * This class find object of interface will all default methods.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
@ExtensionMethod({ Nulls.class, extensions.class })
public class DefaultInterfaceSupplierFinder implements IFindSupplier {

    @SuppressWarnings("unchecked")
    @Override
    public <TYPE, THROWABLE extends Throwable> Supplier<TYPE, THROWABLE> find(
            Class<TYPE>    theGivenClass,
            IProvideObject objectProvider) {
        boolean isDefaultInterface
                =  theGivenClass.isInterface()
                && theGivenClass.getAnnotations().hasAnnotation("DefaultInterface");
        // TODO Implement this.
        return isDefaultInterface
                ? NullSupplier
                : null;
    }
    
}
