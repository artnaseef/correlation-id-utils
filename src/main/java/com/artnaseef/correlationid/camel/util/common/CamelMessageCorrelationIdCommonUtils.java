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

package com.artnaseef.correlationid.camel.util.common;

import com.artnaseef.correlationid.util.CorrelationIdUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Supplier;

/**
 *
 */
public class CamelMessageCorrelationIdCommonUtils {

  public static final String CORRELATION_ID_EXCHANGE_PROPERTY = "com.artnaseef.correlationid.camel.correlation-id";
  private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(CamelMessageCorrelationIdCommonUtils.class);

  private Logger log = DEFAULT_LOGGER;

  private CorrelationIdUtils correlationIdUtils;

//========================================
// Getters and Setters
//----------------------------------------

  public Logger getLog() {
    return log;
  }

  public void setLog(Logger log) {
    this.log = log;
  }

  public CorrelationIdUtils getCorrelationIdUtils() {
    return correlationIdUtils;
  }

  public void setCorrelationIdUtils(
      CorrelationIdUtils correlationIdUtils) {
    this.correlationIdUtils = correlationIdUtils;
  }

//========================================
// Processing
//----------------------------------------

  /**
   * Given an incoming message's headers and exchange properties, extract the inbound correlation ID, if present, and
   * use for all logging and subsequent processing of the request.  If none is present, generate one and use it
   * instead.
   */
  public void extractOrSetCorrelationId(Map<String, Object> headers, Map<String, Object> exchangeProperties,
                                        Supplier<String> altCorrelationSource) {
    Object correlationIdObj = exchangeProperties.get(CORRELATION_ID_EXCHANGE_PROPERTY);

    if (correlationIdObj == null) {
      correlationIdObj = headers.get(this.correlationIdUtils.getCorrelationIdHeaderName());

      if (correlationIdObj == null) {
        // Check the alternate source, if one is defined

        if (altCorrelationSource != null) {
          correlationIdObj = altCorrelationSource.get();
        }

        if (correlationIdObj != null) {
          this.log.debug("have correlation id from alternate source: id={}", correlationIdObj);
        } else {
          this.log.debug("did not locate a Correlation ID from the incoming message nor camel exchange");
        }
      } else {
        this.log.debug("have correlation id from the incoming message headers: id={}", correlationIdObj);
      }

      // If a correlation ID was found, add it to the exchange properties now since the exchange property was missing.
      if (correlationIdObj != null) {
        exchangeProperties.put(CORRELATION_ID_EXCHANGE_PROPERTY, correlationIdObj);
      }
    } else {
      this.log.debug("have correlation id from the camel exchange: id={}", correlationIdObj);
    }

    String correlationId;
    if (correlationIdObj != null) {
      correlationId = correlationIdObj.toString();
    } else {
      correlationId = null;
    }

    this.correlationIdUtils.messageCorrelationIdFilterHandler(
        () -> correlationId,
        (newCorrelationId) -> exchangeProperties.put(CORRELATION_ID_EXCHANGE_PROPERTY, newCorrelationId)
    );
  }
}
