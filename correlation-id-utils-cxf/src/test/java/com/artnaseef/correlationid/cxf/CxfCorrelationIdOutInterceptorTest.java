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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import static org.apache.cxf.message.Message.PROTOCOL_HEADERS;
import static org.junit.Assert.*;

public class CxfCorrelationIdOutInterceptorTest {

    private CxfCorrelationIdOutInterceptor outInterceptor;

    private Exchange mockExchange;
    private Message mockMessage;
    private Message mockInMessage;
    private CorrelationIdUtils mockCorrelationIdUtils;
    private Logger mockLog;

    private Map<String, List<String>> testProtocolHeaders = new TreeMap<>();

    @Before
    public void setUp() throws Exception {
        this.outInterceptor = new CxfCorrelationIdOutInterceptor();

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
        assertNull(this.outInterceptor.getCorrelationIdUtils());

        this.outInterceptor.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        assertSame(this.mockCorrelationIdUtils, this.outInterceptor.getCorrelationIdUtils());
    }

    @Test
    public void testGetSetLog() {
        assertNotNull(this.outInterceptor.getLog());
        assertNotSame(this.mockLog, this.outInterceptor.getLog());

        this.outInterceptor.setLog(this.mockLog);
        assertSame(this.mockLog, this.outInterceptor.getLog());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testMessageWithCorrelationHeader() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(this.mockExchange.getInMessage()).thenReturn(this.mockInMessage);
        Mockito.when(this.mockInMessage.get(CxfCorrelationIdInInterceptor.CXF_CORRELATION_ID_STORE_NAME))
                .thenReturn("x-correlation-id-x");

        // NOTE: in practice, x-correlation-id2-x would be the same as x-correlation-id-x; using different values here
        //  to more thoroughly confirm operation.
        Mockito.when(this.mockCorrelationIdUtils.messageCorrelationIdFilterHandler(Mockito.any(Supplier.class), Mockito.eq(null))).thenReturn("x-correlation-id2-x");

        //
        // Execute
        //
        this.outInterceptor.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.outInterceptor.handleMessage(this.mockMessage);

        //
        // Verify the Results
        //
        ArgumentCaptor<Supplier> correlationIdSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.verify(this.mockCorrelationIdUtils)
                .messageCorrelationIdFilterHandler(correlationIdSupplierCaptor.capture(), Mockito.eq(null));

        // Verify the supplier uses the correlation ID stored in the CXF exchange
        assertEquals("x-correlation-id-x", correlationIdSupplierCaptor.getValue().get());

        // Verify the message header was added
        assertEquals("x-correlation-id2-x", this.testProtocolHeaders.get("x-header-x").get(0));
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
        this.outInterceptor.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.outInterceptor.handleMessage(this.mockMessage);

        //
        // Verify the Results
        //
        ArgumentCaptor<Supplier> correlationIdSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.verify(this.mockCorrelationIdUtils)
                .messageCorrelationIdFilterHandler(correlationIdSupplierCaptor.capture(), Mockito.eq(null));

        // Verify the supplier uses the correlation ID stored in the CXF exchange
        assertNull(correlationIdSupplierCaptor.getValue().get());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMissingProtocolHeaders() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(this.mockMessage.get(PROTOCOL_HEADERS)).thenReturn(null);
        Mockito.when(this.mockCorrelationIdUtils.messageCorrelationIdFilterHandler(Mockito.any(Supplier.class), Mockito.eq(null))).thenReturn("x-correlation-id-x");

        //
        // Execute
        //
        this.outInterceptor.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.outInterceptor.setLog(this.mockLog);
        this.outInterceptor.handleMessage(this.mockMessage);

        // this.log.debug("cannot add correlation ID to protocol headers as they are missing from the message " +
        //         "(wrong phase?): correlation-id={}", correlationId);

        //
        // Verify the Results
        //
        Mockito.verify(this.mockLog)
                .debug("cannot add correlation ID to protocol headers as they are missing from the message " +
                       "(wrong phase?): correlation-id={}", "x-correlation-id-x");
    }

    /**
     * Test the alternate constructor.  It just passes through to the base class, so nothing to verify here.  Added
     * for code coverage.
     */
    @Test
    public void testAltConstructor() {
        new CxfCorrelationIdOutInterceptor(Phase.PRE_INVOKE);
    }
}