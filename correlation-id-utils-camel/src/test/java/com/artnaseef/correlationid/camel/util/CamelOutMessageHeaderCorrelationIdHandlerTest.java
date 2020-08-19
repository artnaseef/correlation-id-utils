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

package com.artnaseef.correlationid.camel.util;

import com.artnaseef.correlationid.camel.util.common.CamelMessageCorrelationIdCommonUtils;
import com.artnaseef.correlationid.util.CorrelationIdUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class CamelOutMessageHeaderCorrelationIdHandlerTest {

    private CamelOutMessageHeaderCorrelationIdHandler handler;

    private CorrelationIdUtils mockCorrelationIdUtils;
    private Exchange mockExchange;
    private Message mockInMessage;

    @Before
    public void setUp() throws Exception {
        this.handler = new CamelOutMessageHeaderCorrelationIdHandler();

        this.mockCorrelationIdUtils = Mockito.mock(CorrelationIdUtils.class);
        this.mockExchange = Mockito.mock(Exchange.class);
        this.mockInMessage = Mockito.mock(Message.class);

        Mockito.when(this.mockExchange.getIn()).thenReturn(this.mockInMessage);
    }

    @Test
    public void testProcessWithCorrelationId() throws Exception {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(this.mockCorrelationIdUtils.getCorrelationIdHeaderName())
                .thenReturn("x-correlation-id-header-name-x");
        Mockito.when(
                this.mockExchange.getProperty(CamelMessageCorrelationIdCommonUtils.CORRELATION_ID_EXCHANGE_PROPERTY, String.class)
        )
                .thenReturn("x-correlation-id-x");

        //
        // Execute
        //
        this.handler.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.handler.process(this.mockExchange);

        //
        // Verify the Results
        //
        Mockito.verify(this.mockInMessage).setHeader("x-correlation-id-header-name-x", "x-correlation-id-x");
    }

    @Test
    public void testProcessNoCorrelationId() throws Exception {
        //
        // Execute
        //
        this.handler.process(this.mockExchange);

        //
        // Verify the Results
        //
        Mockito.verifyNoMoreInteractions(this.mockInMessage);
    }

    @Test
    public void testGetSetCorrelationIdUtils() {
        assertNull(handler.getCorrelationIdUtils());

        handler.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        assertSame(this.mockCorrelationIdUtils, handler.getCorrelationIdUtils());
    }
}