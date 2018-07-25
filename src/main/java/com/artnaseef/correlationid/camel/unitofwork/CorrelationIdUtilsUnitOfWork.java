/*
 * Copyright (c) 2018 Arthur Naseef
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

import com.google.common.base.Strings;

import com.artnaseef.correlationid.util.CorrelationIdUtils;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.MDCUnitOfWork;
import org.apache.camel.spi.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 *
 */
public class CorrelationIdUtilsUnitOfWork extends MDCUnitOfWork {

  private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(CorrelationIdUtilsUnitOfWork.class);
  private final CorrelationIdUtils correlationIdUtils;
  private final String origCorrelationId;
  private Logger log = DEFAULT_LOGGER;

//========================================
// Constructor
//----------------------------------------

  public CorrelationIdUtilsUnitOfWork(Exchange exchange, CorrelationIdUtils correlationIdUtils) {
    super(exchange);

    this.correlationIdUtils = correlationIdUtils;
    this.origCorrelationId = MDC.get(this.correlationIdUtils.getCorrelationIdHeaderName());
  }

//========================================
// Getters and Setters
//----------------------------------------

  public Logger getLog() {
    return log;
  }

  public void setLog(Logger log) {
    this.log = log;
  }

//========================================
// Processing
//----------------------------------------

  @Override
  public UnitOfWork newInstance(Exchange exchange) {
    return new CorrelationIdUtilsUnitOfWork(exchange, this.correlationIdUtils);
  }

  @Override
  public AsyncCallback beforeProcess(Processor processor, Exchange exchange,
                                     AsyncCallback callback) {

    String header = this.correlationIdUtils.getCorrelationIdHeaderName();

    this.populateMdc(exchange);

    return new SubscriptionsUowCallback(callback, header, this.origCorrelationId);
  }

  @Override
  public void afterProcess(Processor processor, Exchange exchange, AsyncCallback callback,
                           boolean doneSync) {

    if (!doneSync) {
      this.clearCorrelationIdMdc();
    }

    super.afterProcess(processor, exchange, callback, doneSync);
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    this.clearCorrelationIdMdc();
  }

//========================================
// Internal Methods
//----------------------------------------

  private void populateMdc(Exchange exchange) {
    String correlationId = exchange.getProperty(this.correlationIdUtils.getCorrelationIdHeaderName(),
                                                String.class);
    if (!Strings.isNullOrEmpty(correlationId)) {
      MDC.put(this.correlationIdUtils.getCorrelationIdHeaderName(), correlationId);
    }
  }

  private void clearCorrelationIdMdc() {
    if (this.origCorrelationId != null) {
      MDC.put(this.correlationIdUtils.getCorrelationIdHeaderName(), this.origCorrelationId);
    } else {
      MDC.remove(this.correlationIdUtils.getCorrelationIdHeaderName());
    }
  }
}
