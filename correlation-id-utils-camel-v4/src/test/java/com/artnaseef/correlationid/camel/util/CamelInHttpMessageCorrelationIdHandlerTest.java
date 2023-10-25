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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class CamelInHttpMessageCorrelationIdHandlerTest {

    private CamelInHttpMessageCorrelationIdHandler camelInHttpMessageCorrelationIdHandler;

    private CorrelationIdUtils mockCorrelationIdUtils;
    private CamelMessageCorrelationIdCommonUtils mockCommonUtils;
    private Exchange mockExchange;
    private Message mockInMessage;

    private Map<String, Object> testHeaders;
    private Map<String, Object> testExchangeProperties;

    @Before
    public void setUp() throws Exception {
        this.camelInHttpMessageCorrelationIdHandler = new CamelInHttpMessageCorrelationIdHandler();

        this.mockCorrelationIdUtils = Mockito.mock(CorrelationIdUtils.class);
        this.mockCommonUtils = Mockito.mock(CamelMessageCorrelationIdCommonUtils.class);
        this.mockExchange = Mockito.mock(Exchange.class);
        this.mockInMessage = Mockito.mock(Message.class);

        this.testHeaders = new TreeMap<>();
        this.testExchangeProperties = new TreeMap<>();

        Mockito.when(this.mockExchange.getIn()).thenReturn(this.mockInMessage);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testProcessNoAltSupplier() throws Exception {
        //
        // Setup Test Data and Interactions
        //

        //
        // Execute
        //
        this.camelInHttpMessageCorrelationIdHandler.setCommonUtils(this.mockCommonUtils);
        this.camelInHttpMessageCorrelationIdHandler.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.camelInHttpMessageCorrelationIdHandler.process(this.mockExchange);

        //
        // Verify the Results
        //
        Mockito.verify(this.mockCommonUtils)
                .extractOrSetCorrelationId(
                        testHeaders,
                        this.testExchangeProperties,
                        null
                );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testProcessWithAltSupplier() throws Exception {
        //
        // Setup Test Data and Interactions
        //
        Function<Exchange, String> altSupplierFunction = (exchange) -> "x-alt-correlation-id-x";
        Mockito.when(
                this.mockExchange.getProperty(CamelCorrelationIdConstants.ALT_CORRELATION_ID_SUPPLIER_PROP, Function.class)
        )
                .thenReturn(altSupplierFunction);

        //
        // Execute
        //
        this.camelInHttpMessageCorrelationIdHandler.setCommonUtils(this.mockCommonUtils);
        this.camelInHttpMessageCorrelationIdHandler.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.camelInHttpMessageCorrelationIdHandler.process(this.mockExchange);

        //
        // Verify the Results
        //
        ArgumentCaptor<Supplier> supplierArgumentCaptor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.verify(this.mockCommonUtils)
                .extractOrSetCorrelationId(
                        Mockito.eq(testHeaders),
                        Mockito.eq(this.testExchangeProperties),
                        supplierArgumentCaptor.capture()
                );

        String result = (String) supplierArgumentCaptor.getValue().get();
        assertEquals("x-alt-correlation-id-x", result);
    }

    @Test
    public void testGetSetCorrelationIdUtils() {
        assertNull(this.camelInHttpMessageCorrelationIdHandler.getCorrelationIdUtils());

        this.camelInHttpMessageCorrelationIdHandler.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        assertSame(this.mockCorrelationIdUtils, this.camelInHttpMessageCorrelationIdHandler.getCorrelationIdUtils());
    }

    @Test
    public void getCommonUtils() {
        assertNull(this.camelInHttpMessageCorrelationIdHandler.getCommonUtils());

        this.camelInHttpMessageCorrelationIdHandler.setCommonUtils(this.mockCommonUtils);
        assertSame(this.mockCommonUtils, this.camelInHttpMessageCorrelationIdHandler.getCommonUtils());
    }
}