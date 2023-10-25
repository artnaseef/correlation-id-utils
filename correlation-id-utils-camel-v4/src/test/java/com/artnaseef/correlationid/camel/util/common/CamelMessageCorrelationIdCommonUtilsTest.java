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

package com.artnaseef.correlationid.camel.util.common;

import com.artnaseef.correlationid.util.CorrelationIdUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.artnaseef.correlationid.camel.util.common.CamelMessageCorrelationIdCommonUtils.CORRELATION_ID_EXCHANGE_PROPERTY;
import static org.junit.Assert.*;

public class CamelMessageCorrelationIdCommonUtilsTest {
    private CamelMessageCorrelationIdCommonUtils commonUtils;

    private CorrelationIdUtils mockCorrelationIdUtils;
    private Logger mockLog;

    private Map<String, Object> testHeaders;
    private Map<String, Object> testExchangeProperties;

    @Before
    public void setUp() throws Exception {
        this.commonUtils = new CamelMessageCorrelationIdCommonUtils();

        this.mockCorrelationIdUtils = Mockito.mock(CorrelationIdUtils.class);
        this.mockLog = Mockito.mock(Logger.class);

        this.testHeaders = new TreeMap<>();
        this.testExchangeProperties = new TreeMap<>();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testExtractCorrelationIdFromExchangeProperty() {
        //
        // Setup Test Data and Interactions
        //
        this.testExchangeProperties.put(CORRELATION_ID_EXCHANGE_PROPERTY, "x-exc-prop-correlation-id-x");

        //
        // Execute
        //
        this.commonUtils.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.commonUtils.extractOrSetCorrelationId(this.testHeaders, this.testExchangeProperties, null);

        //
        // Verify the Results
        //
        ArgumentCaptor<Supplier> correlationIdSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        ArgumentCaptor<Consumer> updatedCorrelationIdConsumer = ArgumentCaptor.forClass(Consumer.class);
        Mockito.verify(this.mockCorrelationIdUtils)
                .messageCorrelationIdFilterHandler(correlationIdSupplierCaptor.capture(), updatedCorrelationIdConsumer.capture());

        // Run the supplier to verify it returns the expected correlation ID
        assertEquals("x-exc-prop-correlation-id-x", correlationIdSupplierCaptor.getValue().get());

        // Run the update consumer to verify it updates the exchange properties
        updatedCorrelationIdConsumer.getValue().accept("x-updated-correlation-id-x");
        assertEquals("x-updated-correlation-id-x", this.testExchangeProperties.get(CORRELATION_ID_EXCHANGE_PROPERTY));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testExtractCorrelationIdFromHeader() {
        //
        // Setup Test Data and Interactions
        //
        String testCorrelationId = "x-hdr-prop-correlation-id-x";
        this.testHeaders.put("x-header-x", testCorrelationId);
        Mockito.when(this.mockCorrelationIdUtils.getCorrelationIdHeaderName()).thenReturn("x-header-x");

        //
        // Execute
        //
        this.commonUtils.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.commonUtils.setLog(this.mockLog);
        this.commonUtils.extractOrSetCorrelationId(this.testHeaders, this.testExchangeProperties, null);

        //
        // Verify the Results
        //
        assertEquals("x-hdr-prop-correlation-id-x", this.testExchangeProperties.get(CORRELATION_ID_EXCHANGE_PROPERTY));
        ArgumentCaptor<Supplier> correlationIdSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.verify(this.mockCorrelationIdUtils)
                .messageCorrelationIdFilterHandler(correlationIdSupplierCaptor.capture(), Mockito.any(Consumer.class));

        // Run the supplier to verify it returns the expected correlation ID
        assertEquals("x-hdr-prop-correlation-id-x", correlationIdSupplierCaptor.getValue().get());

        // Verify the expected log message
        Mockito.verify(this.mockLog)
                .debug("have correlation id from the incoming message headers: id={}", testCorrelationId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testExtractCorrelationIdFromAltSource() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(this.mockCorrelationIdUtils.getCorrelationIdHeaderName()).thenReturn("x-header-x");
        String testCorrelationId = "x-alt-prop-correlation-id-x";
        Supplier<String> altSourceSupplier = () -> testCorrelationId;

        //
        // Execute
        //
        this.commonUtils.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.commonUtils.setLog(this.mockLog);
        this.commonUtils.extractOrSetCorrelationId(this.testHeaders, this.testExchangeProperties, altSourceSupplier);

        //
        // Verify the Results
        //
        assertEquals("x-alt-prop-correlation-id-x", this.testExchangeProperties.get(CORRELATION_ID_EXCHANGE_PROPERTY));
        ArgumentCaptor<Supplier> correlationIdSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.verify(this.mockCorrelationIdUtils)
                .messageCorrelationIdFilterHandler(correlationIdSupplierCaptor.capture(), Mockito.any(Consumer.class));

        // Run the supplier to verify it returns the expected correlation ID
        assertEquals("x-alt-prop-correlation-id-x", correlationIdSupplierCaptor.getValue().get());

        // Verify the expected log message
        Mockito.verify(this.mockLog)
                .debug("have correlation id from alternate source: id={}", testCorrelationId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testExtractCorrelationIdNoSource() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(this.mockCorrelationIdUtils.getCorrelationIdHeaderName()).thenReturn("x-header-x");

        //
        // Execute
        //
        this.commonUtils.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        this.commonUtils.setLog(this.mockLog);
        this.commonUtils.extractOrSetCorrelationId(this.testHeaders, this.testExchangeProperties, null);

        //
        // Verify the Results
        //
        ArgumentCaptor<Supplier> correlationIdSupplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.verify(this.mockCorrelationIdUtils)
                .messageCorrelationIdFilterHandler(correlationIdSupplierCaptor.capture(), Mockito.any(Consumer.class));

        // Run the supplier to verify it returns the expected correlation ID
        assertNull(correlationIdSupplierCaptor.getValue().get());

        // Verify the expected log message
        Mockito.verify(this.mockLog)
                .debug("did not locate a Correlation ID from the incoming message nor camel exchange");
    }

    @Test
    public void testGetSetCorrelationIdUtils() {
        assertNull(this.commonUtils.getCorrelationIdUtils());

        this.commonUtils.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        assertSame(this.mockCorrelationIdUtils, this.commonUtils.getCorrelationIdUtils());
    }

    @Test
    public void testGetSetLog() {
        assertNotNull(this.commonUtils.getLog());
        assertNotSame(this.mockLog, this.commonUtils.getLog());

        this.commonUtils.setLog(this.mockLog);
        assertSame(this.mockLog, this.commonUtils.getLog());
    }
}
