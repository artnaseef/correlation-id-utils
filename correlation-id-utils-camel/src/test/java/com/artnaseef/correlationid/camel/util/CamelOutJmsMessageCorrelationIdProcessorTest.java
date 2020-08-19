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

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class CamelOutJmsMessageCorrelationIdProcessorTest {

    private CamelOutJmsMessageCorrelationIdProcessor processor;

    private Exchange mockExchange;
    private Message mockInMessage;

    private Map<String, Object> testHeaders;
    private Map<String, Object> testExchangeProperties;

    @Before
    public void setUp() throws Exception {
        this.processor = new CamelOutJmsMessageCorrelationIdProcessor();

        this.mockExchange = Mockito.mock(Exchange.class);
        this.mockInMessage = Mockito.mock(Message.class);

        this.testHeaders = new TreeMap<>();
        this.testExchangeProperties = new TreeMap<>();

        Mockito.when(this.mockExchange.getIn()).thenReturn(this.mockInMessage);
        Mockito.when(this.mockInMessage.getHeaders()).thenReturn(this.testHeaders);
        Mockito.when(this.mockExchange.getProperties()).thenReturn(this.testExchangeProperties);
    }

    @Test
    public void testProcessPopulateCorrelationId() {
        //
        // Setup Test Data and Interactions
        //
        this.testExchangeProperties
                .put(CamelMessageCorrelationIdCommonUtils.CORRELATION_ID_EXCHANGE_PROPERTY, "x-correlation-id-x");

        //
        // Execute
        //
        this.processor.process(this.mockExchange);

        //
        // Verify the Results
        //
        assertEquals("x-correlation-id-x",
                this.testHeaders.get(CamelOutJmsMessageCorrelationIdProcessor.JMSCorrelationID_HEADER));
    }

    @Test
    public void testProcessNoCorrelationId() {
        //
        // Execute
        //
        this.processor.process(this.mockExchange);

        //
        // Verify the Results
        //
        assertEquals(0, testHeaders.size());
    }
}