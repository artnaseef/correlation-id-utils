/*
 * Copyright (c) 2020 Arthur Naseef
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.artnaseef.correlationid.camel.unitofwork;

import org.apache.camel.AsyncCallback;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CorrelationIdUtilsUowCallbackFactoryTest {

    private CorrelationIdUtilsUowCallbackFactory factory;

     private AsyncCallback mockAsyncCallback;

    @Before
    public void setUp() throws Exception {
        this.factory = new CorrelationIdUtilsUowCallbackFactory();
    }

    @Test
    public void testCreate() {
        CorrelationIdUtilsUowCallback result =
                this.factory.create(this.mockAsyncCallback, "x-header-x", "x-orig-correlation-id-x");

        assertNotNull(result);
    }
}