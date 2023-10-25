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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CamelCorrelationUtilManagerTest {

    private CamelCorrelationUtilManager camelCorrelationUtilManager;

    @Before
    public void setUp() throws Exception {
        this.camelCorrelationUtilManager = new CamelCorrelationUtilManager();
    }

    @Test
    public void testGetSetCamelInHttpMessageCorrelationIdHandler() {
        assertNull(this.camelCorrelationUtilManager.getCamelInHttpMessageCorrelationIdHandler());

        CamelInHttpMessageCorrelationIdHandler testHandler = new CamelInHttpMessageCorrelationIdHandler();
        this.camelCorrelationUtilManager.setCamelInHttpMessageCorrelationIdHandler(testHandler);

        assertSame(testHandler, this.camelCorrelationUtilManager.getCamelInHttpMessageCorrelationIdHandler());
    }

    @Test
    public void testGetSetCamelInJmsMessageCorrelationIdProcessor() {
        assertNull(this.camelCorrelationUtilManager.getCamelInJmsMessageCorrelationIdProcessor());

        CamelInJmsMessageCorrelationIdProcessor testProcessor = new CamelInJmsMessageCorrelationIdProcessor();
        this.camelCorrelationUtilManager.setCamelInJmsMessageCorrelationIdProcessor(testProcessor);

        assertSame(testProcessor, this.camelCorrelationUtilManager.getCamelInJmsMessageCorrelationIdProcessor());
    }

    @Test
    public void testGetSetCamelOutMessageHeaderCorrelationIdHandler() {
        assertNull(this.camelCorrelationUtilManager.getCamelOutMessageHeaderCorrelationIdHandler());

        CamelOutMessageHeaderCorrelationIdHandler testHandler = new CamelOutMessageHeaderCorrelationIdHandler();
        this.camelCorrelationUtilManager.setCamelOutMessageHeaderCorrelationIdHandler(testHandler);

        assertSame(testHandler, this.camelCorrelationUtilManager.getCamelOutMessageHeaderCorrelationIdHandler());
    }

    @Test
    public void testGetSetCamelOutJmsMessageCorrelationIdProcessor() {
        assertNull(this.camelCorrelationUtilManager.getCamelOutJmsMessageCorrelationIdProcessor());

        CamelOutJmsMessageCorrelationIdProcessor testProcessor = new CamelOutJmsMessageCorrelationIdProcessor();
        this.camelCorrelationUtilManager.setCamelOutJmsMessageCorrelationIdProcessor(testProcessor);

        assertSame(testProcessor, this.camelCorrelationUtilManager.getCamelOutJmsMessageCorrelationIdProcessor());
    }
}