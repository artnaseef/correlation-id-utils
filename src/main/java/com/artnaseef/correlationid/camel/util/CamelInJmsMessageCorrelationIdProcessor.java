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

package com.artnaseef.correlationid.camel.util;

import com.google.common.base.Strings;

import com.artnaseef.correlationid.camel.util.common.CamelMessageCorrelationIdCommonUtils;
import com.artnaseef.correlationid.util.CorrelationIdUtils;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.MDC;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Correlation ID handler for Camel routes.
 */
public class CamelInJmsMessageCorrelationIdProcessor implements Processor {

  public static final String JMSCorrelationID_HEADER = "JMSCorrelationID";

  private CorrelationIdUtils correlationIdUtils;
  private CamelMessageCorrelationIdCommonUtils commonUtils;

//========================================
// Getters and Setters
//----------------------------------------

  public CorrelationIdUtils getCorrelationIdUtils() {
    return correlationIdUtils;
  }

  public void setCorrelationIdUtils(CorrelationIdUtils correlationIdUtils) {
    this.correlationIdUtils = correlationIdUtils;
  }

  public CamelMessageCorrelationIdCommonUtils getCommonUtils() {
    return commonUtils;
  }

  public void setCommonUtils(
      CamelMessageCorrelationIdCommonUtils commonUtils) {
    this.commonUtils = commonUtils;
  }

//========================================
// Processing
//----------------------------------------

  @Override
  public void process(Exchange exchange) throws Exception {
    Map<String, Object> messageHeaders = exchange.getIn().getHeaders();
    Map<String, Object> exchangeProperties = exchange.getProperties();

    Object
        correlationIdObj =
        messageHeaders.get(JMSCorrelationID_HEADER);

    String correlationId;
    if (correlationIdObj != null) {
      correlationId = correlationIdObj.toString();
    } else {
      correlationId = null;
    }

    if (! Strings.isNullOrEmpty(correlationId)) {
      // Got it - put it in the MDC and exchange properties.
      MDC.put(this.correlationIdUtils.getCorrelationIdHeaderName(), correlationId);
      exchangeProperties.put(CamelMessageCorrelationIdCommonUtils.CORRELATION_ID_EXCHANGE_PROPERTY, correlationId);
    } else {
      @SuppressWarnings("unchecked")
      Function<Exchange, String>
          altCorrelationIdSupplier =
          exchange.getProperty(CamelCorrelationIdConstants.ALT_CORRELATION_ID_SUPPLIER_PROP, Function.class);

      Supplier<String> supplierWrapper = null;
      if (altCorrelationIdSupplier != null) {
        supplierWrapper = () -> altCorrelationIdSupplier.apply(exchange);
      }

      // Fallback to the non-JMS logic
      this.commonUtils.extractOrSetCorrelationId(messageHeaders, exchangeProperties, supplierWrapper);
    }
  }
}
