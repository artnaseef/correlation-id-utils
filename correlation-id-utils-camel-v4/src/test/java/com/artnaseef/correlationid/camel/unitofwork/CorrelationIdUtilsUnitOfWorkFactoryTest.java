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

import com.artnaseef.correlationid.util.CorrelationIdUtils;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.Message;
import org.apache.camel.spi.InflightRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static org.junit.Assert.*;

public class CorrelationIdUtilsUnitOfWorkFactoryTest {

    private CorrelationIdUtilsUnitOfWorkFactory factory;

    private CorrelationIdUtils mockCorrelationIdUtils;
    private Exchange mockExchange;
    private Logger mockLog;

    private CamelContext mockContext;
    private ExtendedCamelContext mockExtendedCamelContext;
    private Message mockMessage;
    private InflightRepository mockInflightRepository;

    @Before
    public void setUp() throws Exception {
        this.factory = new CorrelationIdUtilsUnitOfWorkFactory();
        this.mockCorrelationIdUtils = Mockito.mock(CorrelationIdUtils.class);
        this.mockExchange = Mockito.mock(Exchange.class);
        this.mockLog = Mockito.mock(Logger.class);

        Mockito.when(this.mockCorrelationIdUtils.getCorrelationIdHeaderName()).thenReturn("x-header-x");

        this.prepareForInheritedTest();
    }

    @Test
    public void testGetSetLog() {
        assertNotNull(this.factory.getLog());
        assertNotSame(this.mockLog, this.factory.getLog());

        this.factory.setLog(this.mockLog);
        assertSame(this.mockLog, this.factory.getLog());
    }

    @Test
    public void testGetSetCorrelationIdUtils() {
        assertNull(this.factory.getCorrelationIdUtils());

        this.factory.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        assertSame(this.mockCorrelationIdUtils, this.factory.getCorrelationIdUtils());
    }

    @Test
    public void createUnitOfWork() {
        this.factory.setCorrelationIdUtils(this.mockCorrelationIdUtils);
        CorrelationIdUtilsUnitOfWork result =
                (CorrelationIdUtilsUnitOfWork) this.factory.createUnitOfWork(this.mockExchange);

        assertNotNull(result);
        assertSame(this.mockCorrelationIdUtils, result.getCorrelationIdUtils());
    }

//========================================
// Internals
//----------------------------------------

    /**
     * Due to use of inherited functionality, that is too complex to avoid, prepare the test data and interactions
     *  necessary to satisfy the base Camel code.  With luck, that won't change too often, or won't impact these tests.
     */
    private void prepareForInheritedTest () {
        this.mockContext = Mockito.mock(CamelContext.class);
        this.mockExtendedCamelContext = Mockito.mock(ExtendedCamelContext.class);
        this.mockMessage = Mockito.mock(Message.class);
        this.mockInflightRepository = Mockito.mock(InflightRepository.class);

        Mockito.when(this.mockContext.getCamelContextExtension()).thenReturn(this.mockExtendedCamelContext);

        Mockito.when(this.mockExchange.getIn()).thenReturn(this.mockMessage);
        Mockito.when(this.mockExchange.getMessage()).thenReturn(this.mockMessage);

        Mockito.when(this.mockExchange.getContext()).thenReturn(this.mockContext);
        Mockito.when(this.mockContext.getInflightRepository()).thenReturn(mockInflightRepository);
    }
}