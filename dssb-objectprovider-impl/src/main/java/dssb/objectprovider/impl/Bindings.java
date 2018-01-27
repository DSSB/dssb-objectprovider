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
package dssb.objectprovider.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import dssb.utils.common.Nulls;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;

// TODO - Pipeable
/**
 * Collections of bindings.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
@ExtensionMethod({ Nulls.class })
public class Bindings {
    
    @SuppressWarnings("rawtypes")
    private final Map<Class, IBind> bindings;
    
    Bindings(@SuppressWarnings("rawtypes") Map<Class, IBind> bindings) {
        this.bindings = Collections.unmodifiableMap(new HashMap<>(bindings));
    }
    
    @SuppressWarnings("unchecked")
    public <TYPE> IBind<TYPE> getBinding(Class<TYPE> clzz) {
        return (IBind<TYPE>)this.bindings.get(clzz);
    }
    
    public static class Builder {
        
        private final Map<Class, IBind> bindings = new HashMap<>();
        
        public <TYPE> Builder bind(@NonNull Class<TYPE> clzz, IBind<TYPE> binding) {
            if (binding.isNotNull())
                this.bindings.put(clzz, binding);
            return this;
        }
        
        public Bindings build() {
            return new Bindings(bindings);
        }
        
    }
    
}
