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

package com.artnaseef.correlationid.cxf;

import com.artnaseef.correlationid.util.CorrelationIdUtils;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.apache.cxf.message.Message.PROTOCOL_HEADERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class CxfCorrelationIdInInterceptorTest {

    private CxfCorrelationIdInInterceptor inInterceptor;

    private Exchange mockExchange;
    private Message mockMessage;
    private Message mockInMessage;
    private CorrelationIdUtils mockCorrelationIdUtils;
    private Logger mockLog;

    private Map<String, List<String>> testProtocolHeaders = new TreeMap<>();

    @Before
    public void setUp() throws Exception {
        this.inInterceptor = new CxfCorrelationIdInInterceptor();

        this.mockExchange = Mockito.mock(Exchange.class);
        this.mockMessage = Mockito.mock(Message.class);
        this.mockInMessage = Mockito.mock(Message.class);
        this.mockCorrelationIdUtils = Mockito.mock(CorrelationIdUtils.class);
        this.mockLog = Mockito.mock(Logger.class);

        Mockito.when(this.mockMessage.getExchange()).thenReturn(this.mockExchange);
        Mockito.when(this.mockMessage.get(PROTOCOL_HEADERS)).thenReturn(this.testProtocolHeaders);

        Mockito.when(this.mockCorrelationIdUtils.getCorrelationIdHeaderName()).thenReturn("x-header-x");
    }

    @Test
    public void testGetSetCorrelationIdUtils() {
        assertNull(this.inInterceptor.getCorrelationIdUtils());

        this.inInterceptor.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        assertSame(this.mockCorrelationIdUtils, this.inInterceptor.getCorrelationIdUtils());
    }

    @Test
    public void testGetSetLog() {
        assertNotNull(this.inInterceptor.getLog());
        assertNotSame(this.mockLog, this.inInterceptor.getLog());

        this.inInterceptor.setLog(this.mockLog);
        assertSame(this.mockLog, this.inInterceptor.getLog());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testMessageWithCorrelationHeader() {
        //
        // Setup Test Data and Interactions
        //
        this.testProtocolHeaders.put("x-header-x", Collections.singletonList("x-correlation-id-x"));

        Mockito.when(this.mockExchange.getInMessage()).thenReturn(this.mockInMessage);
        Mockito.when(this.mockInMessage.get(CxfCorrelationIdInInterceptor.CXF_CORRELATION_ID_STORE_NAME))
                .thenReturn("x-correlation-id-x");

        // NOTE: in practice, x-correlation-id2-x would be the same as x-correlation-id-x; using different values here
        //  to more thoroughly confirm operation.
        Mockito.when(this.mockCorrelationIdUtils
                .messageCorrelationIdFilterHandler(Mockito.any(Supplier.class), Mockito.any(Consumer.class))).thenReturn("x-correlation-id2-x");

        //
        // Execute
        //
        this.inInterceptor.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.inInterceptor.setLog(this.mockLog);
        this.inInterceptor.handleMessage(this.mockMessage);

        //
        // Verify the Results
        //
        ArgumentCaptor<Supplier> correlationIdSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        ArgumentCaptor<Consumer> updateConsumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        Mockito.verify(this.mockCorrelationIdUtils)
                .messageCorrelationIdFilterHandler(correlationIdSupplierCaptor.capture(), updateConsumerCaptor.capture());

        // Verify the message header was added
        Mockito.verify(this.mockMessage).put(CxfCorrelationIdInInterceptor.CXF_CORRELATION_ID_STORE_NAME, "x-correlation-id2-x");

        // Verify the correlation-id-supplier supplies the right value
        assertEquals("x-correlation-id-x", correlationIdSupplierCaptor.getValue().get());

        // Verify the update consumer properly consumes
        updateConsumerCaptor.getValue().accept("x-correlation-id-3-x");
        assertEquals(Collections.singletonList("x-correlation-id-3-x"), this.testProtocolHeaders.get("x-header-x"));

        // ALSO, Verify the correlation-id-supplier when the header returns multiple values
        List<String> idList = Arrays.asList("x-id1-x", "x-id2-x");
        this.testProtocolHeaders.put("x-header-x", idList);
        assertEquals("x-id1-x", correlationIdSupplierCaptor.getValue().get());
        Mockito.verify(this.mockLog)
                .info("Extracted multiple correlation IDs for message; using first one: " +
                      "effective-correlation-id={}; all-correlation-ids={}",
                      "x-id1-x", idList);

        // ALSO, Verify the update consumer adds headers to the message when it does not yet have them
        Mockito.when(this.mockMessage.get(PROTOCOL_HEADERS)).thenReturn(null);
        updateConsumerCaptor.getValue().accept("x-correlation-id-4-x");
        ArgumentCaptor<Map<String, List<String>>> headerCaptor = (ArgumentCaptor) ArgumentCaptor.forClass(Map.class);

        Mockito.verify(this.mockMessage).put(Mockito.eq(PROTOCOL_HEADERS), headerCaptor.capture());
        assertEquals("x-correlation-id-4-x", headerCaptor.getValue().get("x-header-x").get(0));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testMissingInMessage() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(this.mockExchange.getInMessage()).thenReturn(null);

        //
        // Execute
        //
        this.inInterceptor.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.inInterceptor.handleMessage(this.mockMessage);

        //
        // Verify the Results
        //
        ArgumentCaptor<Supplier> correlationIdSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.verify(this.mockCorrelationIdUtils)
                .messageCorrelationIdFilterHandler(correlationIdSupplierCaptor.capture(), Mockito.any(Consumer.class));

        // Verify the supplier uses the correlation ID stored in the CXF exchange
        assertNull(correlationIdSupplierCaptor.getValue().get());
    }

    /**
     * Test the alternate constructor.  It just passes through to the base class, so nothing to verify here.  Added
     * for code coverage.
     */
    @Test
    public void testAltConstructor() {
        new CxfCorrelationIdInInterceptor(Phase.PRE_INVOKE);
    }
}