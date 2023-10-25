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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.MDC;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CamelInJmsMessageCorrelationIdProcessorTest {

    private CamelInJmsMessageCorrelationIdProcessor processor;

    private CamelMessageCorrelationIdCommonUtils mockCommonUtils;
    private CorrelationIdUtils mockCorrelationIdUtils;
    private Exchange mockExchange;
    private Message mockInMessage;

    private Map<String, Object> testHeaders = new TreeMap<>();
    private Map<String, Object> testExchangeProperties = new TreeMap<>();

    @Before
    public void setUp() throws Exception {
        this.processor = new CamelInJmsMessageCorrelationIdProcessor();

        this.mockCommonUtils = Mockito.mock(CamelMessageCorrelationIdCommonUtils.class);
        this.mockCorrelationIdUtils = Mockito.mock(CorrelationIdUtils.class);
        this.mockExchange = Mockito.mock(Exchange.class);
        this.mockInMessage = Mockito.mock(Message.class);

        this.testHeaders = new TreeMap<>();
        this.testExchangeProperties = new TreeMap<>();

        Mockito.when(this.mockExchange.getIn()).thenReturn(this.mockInMessage);
        Mockito.when(this.mockExchange.getProperties()).thenReturn(this.testExchangeProperties);
        Mockito.when(this.mockInMessage.getHeaders()).thenReturn(testHeaders);
    }

    @After
    public void tearDown() throws Exception {
        MDC.clear();
    }

    @Test
    public void testProcessNoCorrelationId() throws Exception {
        //
        // Execute
        //
        this.processor.setCommonUtils(this.mockCommonUtils);
        this.processor.process(this.mockExchange);

        //
        // Verify the Results
        //
        Mockito.verify(this.mockCommonUtils)
                .extractOrSetCorrelationId(testExchangeProperties, this.testExchangeProperties, null);
        assertTrue(this.testHeaders.isEmpty());
        assertTrue(this.testExchangeProperties.isEmpty());
    }

    @Test
    public void testProcessInCorrelationIdHeader() throws Exception {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(this.mockCorrelationIdUtils.getCorrelationIdHeaderName()).thenReturn("x-correlation-id-header-x");
        this.testHeaders.put(CamelInJmsMessageCorrelationIdProcessor.JMSCorrelationID_HEADER, "x-correlation-id-x");

        //
        // Execute
        //
        this.processor.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.processor.process(this.mockExchange);

        //
        // Verify the Results
        //
        assertEquals("x-correlation-id-x",
                this.testExchangeProperties.get(CamelMessageCorrelationIdCommonUtils.CORRELATION_ID_EXCHANGE_PROPERTY));
        assertEquals("x-correlation-id-x", MDC.get("x-correlation-id-header-x"));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testAltSupplier() throws Exception {
        //
        // Setup Test Data and Interactions
        //
        Function<Exchange, String> supplierFunction = (exchange) -> "x-alt-src-correlation-id-x";

        Mockito.when(
                this.mockExchange
                        .getProperty(CamelCorrelationIdConstants.ALT_CORRELATION_ID_SUPPLIER_PROP, Function.class)
        )
                .thenReturn(supplierFunction);


        //
        // Execute
        //
        this.processor.setCommonUtils(this.mockCommonUtils);
        this.processor.process(this.mockExchange);


        //
        // Verify the Results
        //
        ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.verify(this.mockCommonUtils)
                .extractOrSetCorrelationId(
                        Mockito.same(this.testHeaders),
                        Mockito.same(this.testExchangeProperties),
                        supplierCaptor.capture()
                );

        // Verify the supplier used passes through our supplierFunction results.
        String supplierValue = (String) supplierCaptor.getValue().get();
        assertEquals("x-alt-src-correlation-id-x", supplierValue);
    }

    @Test
    public void testGetSetCommonUtils() {
        assertNull(this.processor.getCommonUtils());

        this.processor.setCommonUtils(this.mockCommonUtils);
        assertSame(this.mockCommonUtils, this.processor.getCommonUtils());
    }

    @Test
    public void testGetSetCorrelationIdUtils() {
        assertNull(this.processor.getCorrelationIdUtils());

        this.processor.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        assertSame(this.mockCorrelationIdUtils, this.processor.getCorrelationIdUtils());
    }
}
