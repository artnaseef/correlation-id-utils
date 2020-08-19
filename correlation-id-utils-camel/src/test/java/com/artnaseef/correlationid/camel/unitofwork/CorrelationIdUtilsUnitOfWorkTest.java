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

import com.artnaseef.correlationid.camel.util.common.CamelMessageCorrelationIdCommonUtils;
import com.artnaseef.correlationid.util.CorrelationIdUtils;
import org.apache.camel.AsyncCallback;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.spi.InflightRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.MDC;

import static org.junit.Assert.*;

public class CorrelationIdUtilsUnitOfWorkTest {

    private CorrelationIdUtilsUnitOfWork unitOfWork;

    private CorrelationIdUtils mockCorrelationIdUtils;
    private Exchange mockExchange;
    private Processor mockProcessor;
    private AsyncCallback mockAsyncCallback;
    private CorrelationIdUtilsUowCallbackFactory mockUowCallbackFactory;
    private CorrelationIdUtilsUowCallback mockUowCallback;
    private Logger mockLog;

    private CamelContext mockContext;
    private Message mockMessage;
    private InflightRepository mockInflightRepository;

    @Before
    public void setUp() throws Exception {
        this.mockCorrelationIdUtils = Mockito.mock(CorrelationIdUtils.class);
        this.mockExchange = Mockito.mock(Exchange.class);
        this.mockProcessor = Mockito.mock(Processor.class);
        this.mockAsyncCallback = Mockito.mock(AsyncCallback.class);
        this.mockUowCallbackFactory = Mockito.mock(CorrelationIdUtilsUowCallbackFactory.class);
        this.mockUowCallback = Mockito.mock(CorrelationIdUtilsUowCallback.class);
        this.mockLog = Mockito.mock(Logger.class);

        Mockito.when(this.mockCorrelationIdUtils.getCorrelationIdHeaderName()).thenReturn("x-header-x");

        this.prepareForInheritedTest();

        this.unitOfWork = new CorrelationIdUtilsUnitOfWork(this.mockExchange, this.mockCorrelationIdUtils);
    }

    @After
    public void tearDown() throws Exception {
        MDC.clear();
    }

    @Test
    public void testGetSetLog() {
        assertNotNull(this.unitOfWork.getLog());
        assertNotSame(this.mockLog, this.unitOfWork.getLog());

        this.unitOfWork.setLog(this.mockLog);
        assertSame(this.mockLog, this.unitOfWork.getLog());
    }

    @Test
    public void testGetSetCorrelationIdUtilsUowCallbackFactory() {
        assertNotNull(this.unitOfWork.getUowCallbackFactory());
        assertNotSame(this.mockUowCallbackFactory, this.unitOfWork.getUowCallbackFactory());

        this.unitOfWork.setUowCallbackFactory(this.mockUowCallbackFactory);
        assertSame(this.mockUowCallbackFactory, this.unitOfWork.getUowCallbackFactory());
    }

    @Test
    public void testFullProcess() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(
                this.mockUowCallbackFactory.create(this.mockAsyncCallback, "x-header-x", "x-orig-correlation-id-x")
        ).thenReturn(this.mockUowCallback);

        Mockito.when(
                this.mockExchange.getProperty(CamelMessageCorrelationIdCommonUtils.CORRELATION_ID_EXCHANGE_PROPERTY, String.class)
        ).thenReturn("x-exc-correlation-id-x");

        MDC.put("x-header-x", "x-orig-correlation-id-x");

        this.unitOfWork = new CorrelationIdUtilsUnitOfWork(this.mockExchange, this.mockCorrelationIdUtils);

        //
        // Execute and Verify
        //

        // newInstance
        this.unitOfWork.setUowCallbackFactory(this.mockUowCallbackFactory);
        CorrelationIdUtilsUnitOfWork unitOfWork = (CorrelationIdUtilsUnitOfWork)
                this.unitOfWork.newInstance(this.mockExchange);
        assertSame(this.mockCorrelationIdUtils, unitOfWork.getCorrelationIdUtils());

        // beforeProcess
        AsyncCallback result = unitOfWork.beforeProcess(this.mockProcessor, this.mockExchange, this.mockAsyncCallback);
        assertSame(this.mockUowCallback, result);
        assertEquals("x-exc-correlation-id-x", MDC.get("x-header-x"));

        // afterProcess
        unitOfWork.afterProcess(this.mockProcessor, this.mockExchange, this.mockAsyncCallback, false);

        assertEquals("x-orig-correlation-id-x", MDC.get("x-header-x"));
    }

    @Test
    public void testFullProcessNoOrigId() {
        //
        // Setup Test Data and Interactions
        //
        Mockito.when(
                this.mockUowCallbackFactory.create(this.mockAsyncCallback, "x-header-x", null)
        ).thenReturn(this.mockUowCallback);

        Mockito.when(
                this.mockExchange.getProperty(CamelMessageCorrelationIdCommonUtils.CORRELATION_ID_EXCHANGE_PROPERTY, String.class)
        ).thenReturn("x-exc-correlation-id-x");

        MDC.remove("x-header-x");

        this.unitOfWork = new CorrelationIdUtilsUnitOfWork(this.mockExchange, this.mockCorrelationIdUtils);

        //
        // Execute and Verify
        //

        // newInstance
        this.unitOfWork.setUowCallbackFactory(this.mockUowCallbackFactory);
        CorrelationIdUtilsUnitOfWork unitOfWork = (CorrelationIdUtilsUnitOfWork)
                this.unitOfWork.newInstance(this.mockExchange);
        assertSame(this.mockCorrelationIdUtils, unitOfWork.getCorrelationIdUtils());

        // beforeProcess
        AsyncCallback result = unitOfWork.beforeProcess(this.mockProcessor, this.mockExchange, this.mockAsyncCallback);
        assertSame(this.mockUowCallback, result);
        assertEquals("x-exc-correlation-id-x", MDC.get("x-header-x"));

        // afterProcess
        unitOfWork.afterProcess(this.mockProcessor, this.mockExchange, this.mockAsyncCallback, false);

        assertNull(MDC.get("x-header-x"));
    }

    @Test
    public void testStopRestoreOrigCorrelationId() throws Exception {
        //
        // Setup Test Data and Interactions
        //
        MDC.put("x-header-x", "x-orig-correlation-id-x");

        //
        // Execute
        //
        this.unitOfWork = new CorrelationIdUtilsUnitOfWork(this.mockExchange, this.mockCorrelationIdUtils);
        MDC.put("x-header-x", "x-new-correlation-id-x");
        this.unitOfWork.stop();

        //
        // Verify the Results
        //
        assertEquals("x-orig-correlation-id-x", MDC.get("x-header-x"));
    }

    @Test
    public void testStopWithoutPriorCorrelationId() throws Exception {
        //
        // Setup Test Data and Interactions
        //
        MDC.remove("x-header-x");

        //
        // Execute
        //
        this.unitOfWork = new CorrelationIdUtilsUnitOfWork(this.mockExchange, this.mockCorrelationIdUtils);
        MDC.put("x-header-x", "x-new-correlation-id-x");
        this.unitOfWork.stop();

        //
        // Verify the Results
        //
        assertNull(MDC.get("x-header-x"));
    }

//========================================
// Internals
//----------------------------------------

    /**
     * Due to use of inherited functionality, that is too complex to avoid, prepare the test data and interactions
     *  necessary to satisfy the base Camel code.  With luck, that won't change too often, or won't impact these tests.
     */
    private void prepareForInheritedTest () {
        this.mockContext = Mockito.mock(ExtendedCamelContext.class);
        this.mockMessage = Mockito.mock(Message.class);
        this.mockInflightRepository = Mockito.mock(InflightRepository.class);

        Mockito.when(this.mockExchange.getIn()).thenReturn(this.mockMessage);
        Mockito.when(this.mockExchange.getMessage()).thenReturn(this.mockMessage);

        Mockito.when(this.mockExchange.getContext()).thenReturn(this.mockContext);
        Mockito.when(this.mockContext.getInflightRepository()).thenReturn(mockInflightRepository);
    }
}
