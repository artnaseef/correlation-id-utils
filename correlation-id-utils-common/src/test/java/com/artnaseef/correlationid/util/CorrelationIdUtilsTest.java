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

package com.artnaseef.correlationid.util;

import com.fasterxml.uuid.NoArgGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class CorrelationIdUtilsTest {

    private CorrelationIdUtils correlationIdUtils;

    private NoArgGenerator mockUuidGenerator;
    private Logger mockLogger;
    private Consumer<String> mockCorrelationIdConsumer;
    private UUID testUuid;

    @Before
    public void setUp() throws Exception {
        //
        // Setup Test Data
        //
        this.correlationIdUtils = new CorrelationIdUtils();

        this.mockUuidGenerator = Mockito.mock(NoArgGenerator.class);
        this.mockLogger = Mockito.mock(Logger.class);
        this.mockCorrelationIdConsumer = Mockito.mock(Consumer.class);

        this.testUuid = UUID.fromString("1111-2222-3333-2222-1111");
    }

    @After
    public void cleanup() {
        //
        // Clear the MDC to prevent corrupting subsequent tests
        //
        MDC.clear();
    }


    @Test
    public void testHandlerGenerateNewNullSource() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(this.mockUuidGenerator.generate()).thenReturn(this.testUuid);
        this.correlationIdUtils.setUuidGenerator(this.mockUuidGenerator);

        //
        // Execute
        //
        String result = this.correlationIdUtils.messageCorrelationIdFilterHandler(null, this.mockCorrelationIdConsumer);

        //
        // Verify the Results
        //
        assertEquals(this.testUuid.toString(), result);
        assertEquals(this.testUuid.toString(), MDC.get(CorrelationIdUtils.DEFAULT_CORRELATION_ID_HEADER_NAME));
        Mockito.verify(this.mockCorrelationIdConsumer).accept(this.testUuid.toString());
    }

    @Test
    public void testHandlerGenerateNewWithSource() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(this.mockUuidGenerator.generate()).thenReturn(this.testUuid);
        this.correlationIdUtils.setUuidGenerator(this.mockUuidGenerator);

        //
        // Execute
        //
        this.correlationIdUtils.init();
        String result = this.correlationIdUtils.messageCorrelationIdFilterHandler(() -> null, null);

        //
        // Verify the Results
        //
        assertEquals(this.testUuid.toString(), result);
        assertEquals(this.testUuid.toString(), MDC.get(CorrelationIdUtils.DEFAULT_CORRELATION_ID_HEADER_NAME));
    }

    @Test
    public void testHandlerSourceProvidedId() {
        //
        // Execute
        //
        this.correlationIdUtils.init();
        String result = this.correlationIdUtils.messageCorrelationIdFilterHandler(() -> "999-888-999", null);

        //
        // Verify the Results
        //
        assertEquals("999-888-999", result);
        assertEquals("999-888-999", MDC.get(CorrelationIdUtils.DEFAULT_CORRELATION_ID_HEADER_NAME));
    }

    @Test
    public void testHandlerUsesCorrelationId() {
        //
        // Setup Test Data and Interactions
        //
        MDC.put(CorrelationIdUtils.DEFAULT_CORRELATION_ID_HEADER_NAME, "x-mdc-correlation-id-x");

        //
        // Execute
        //
        this.correlationIdUtils.setLog(this.mockLogger);
        this.correlationIdUtils.init();
        String result = this.correlationIdUtils.messageCorrelationIdFilterHandler(null, null);

        //
        // Verify the Results
        //
        assertEquals("x-mdc-correlation-id-x", result);
        Mockito.verify(this.mockLogger).debug("obtained correlation ID from MDC: id={}", "x-mdc-correlation-id-x");
    }

    @Test
    public void testAlternateCorrelationIdHeader() {
        //
        // Setup Test Data and Interactions
        //
        MDC.put(CorrelationIdUtils.DEFAULT_CORRELATION_ID_HEADER_NAME, "x-default-correlation-id-x");
        MDC.put("x-alt-header-name-x", "x-correlation-id-x");

        //
        // Execute
        //
        this.correlationIdUtils.setCorrelationIdHeaderName("x-alt-header-name-x");
        this.correlationIdUtils.init();
        String result = this.correlationIdUtils.messageCorrelationIdFilterHandler(null, null);

        //
        // Verify the Results
        //
        assertEquals("x-correlation-id-x", result);
    }

    @Test
    public void testGetSetCorrelationIdHeaderName() {
        assertEquals(CorrelationIdUtils.DEFAULT_CORRELATION_ID_HEADER_NAME,
                this.correlationIdUtils.getCorrelationIdHeaderName());

        this.correlationIdUtils.setCorrelationIdHeaderName("x-header-name-x");
        assertEquals("x-header-name-x", this.correlationIdUtils.getCorrelationIdHeaderName());
    }

    @Test
    public void testGetSetInitUuidGenerator() {
        assertNull(this.correlationIdUtils.getUuidGenerator());
        this.correlationIdUtils.init();
        assertNotNull(this.correlationIdUtils.getUuidGenerator());

        this.correlationIdUtils.setUuidGenerator(this.mockUuidGenerator);
        assertSame(this.mockUuidGenerator, this.correlationIdUtils.getUuidGenerator());
    }

    @Test
    public void testGetSetLog() {
        assertNotNull(this.correlationIdUtils.getLog());
        assertNotSame(this.mockLogger, this.correlationIdUtils.getLog());

        this.correlationIdUtils.setLog(this.mockLogger);
        assertSame(this.mockLogger, this.correlationIdUtils.getLog());
    }
}
