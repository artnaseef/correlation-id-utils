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

import com.artnaseef.correlationid.camel.util.common.CamelMessageCorrelationIdCommonUtils;
import com.artnaseef.correlationid.util.CorrelationIdUtils;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

/**
 * Processor that populates the JMS Correlation ID from the camel route's correlation ID, if set.
 */
public class CamelOutJmsMessageCorrelationIdProcessor implements Processor {

  public static final String JMSCorrelationID_HEADER = "JMSCorrelationID";

//========================================
// Processing
//----------------------------------------

  /**
   * Extract the correlation ID from the exchange properties and copy out to the JMS message header.
   *
   * @param exchange Camel exchange to process
   */
  @Override
  public void process(Exchange exchange) {
    Map<String, Object> messageHeaders = exchange.getIn().getHeaders();
    Map<String, Object> exchangeProperties = exchange.getProperties();

    Object
        correlationIdValue =
        exchangeProperties.get(CamelMessageCorrelationIdCommonUtils.CORRELATION_ID_EXCHANGE_PROPERTY);

    if (correlationIdValue != null) {
      messageHeaders.put(JMSCorrelationID_HEADER, correlationIdValue);
    }
  }
}
