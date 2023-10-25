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
import org.mockito.Mockito;
import org.slf4j.MDC;

import static org.junit.Assert.*;

public class CorrelationIdUtilsUowCallbackTest {

    private CorrelationIdUtilsUowCallback callback;

    private AsyncCallback mockAsyncCallback;

    @Before
    public void setUp() throws Exception {
        this.mockAsyncCallback = Mockito.mock(AsyncCallback.class);
    }

    @Test
    public void testDoneAsync() {
        //
        // Setup Test Data and Interactions
        //
        MDC.put("x-header-x", "x-upd-correlation-id-x");

        //
        // Execute
        //
        this.callback = new CorrelationIdUtilsUowCallback(this.mockAsyncCallback, "x-header-x", "x-orig-correlation-id-x");
        this.callback.done(false);

        //
        // Verify the Results
        //
        Mockito.verify(this.mockAsyncCallback).done(false);
        assertEquals("x-orig-correlation-id-x", MDC.get("x-header-x"));
    }

    @Test
    public void testDoneAsyncNoOriginalCorrelationId() {
        //
        // Setup Test Data and Interactions
        //
        MDC.put("x-header-x", "x-upd-correlation-id-x");

        //
        // Execute
        //
        this.callback = new CorrelationIdUtilsUowCallback(this.mockAsyncCallback, "x-header-x", null);
        this.callback.done(false);

        //
        // Verify the Results
        //
        Mockito.verify(this.mockAsyncCallback).done(false);
        assertNull(MDC.get("x-header-x"));
    }

    @Test
    public void testDoneSync() {
        //
        // Setup Test Data and Interactions
        //
        MDC.put("x-header-x", "x-upd-correlation-id-x");

        //
        // Execute
        //
        this.callback = new CorrelationIdUtilsUowCallback(this.mockAsyncCallback, "x-header-x", "x-orig-correlation-id-x");
        this.callback.done(true);

        //
        // Verify the Results
        //
        Mockito.verify(this.mockAsyncCallback).done(true);
        assertEquals("x-upd-correlation-id-x", MDC.get("x-header-x"));
    }

    @Test
    public void testNoDelegate() {
        //
        // Setup Test Data and Interactions
        //
        MDC.put("x-header-x", "x-upd-correlation-id-x");

        //
        // Execute
        //
        this.callback = new CorrelationIdUtilsUowCallback(null, "x-header-x", "x-orig-correlation-id-x");
        this.callback.done(true);

        // Really nothing to explicitly validate
    }
}
