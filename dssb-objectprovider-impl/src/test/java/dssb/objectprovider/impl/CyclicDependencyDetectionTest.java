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

import org.junit.Ignore;
import org.junit.Test;

import dssb.objectprovider.impl.exception.CyclicDependencyDetectedException;

public class CyclicDependencyDetectionTest {
    
    private ObjectProvider provider = new ObjectProvider();
    
    public static class Cyclic1 {
        
        public Cyclic1(Cyclic1 another) {
        }
    }
    
    @Ignore("Entanglement")
    @Test(expected=CyclicDependencyDetectedException.class)
    public void testThat_whenDefaultConstructorAskForItself_expectCyclicDependencyDetectedException() {
        provider.get(Cyclic1.class);
    }
    
}
