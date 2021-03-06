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

package com.artnaseef.correlationid.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 */
public class CorrelationIdUtils {

  private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(CorrelationIdUtils.class);

  public static final String DEFAULT_CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";

  private Logger log = DEFAULT_LOGGER;

  private String correlationIdHeaderName = DEFAULT_CORRELATION_ID_HEADER_NAME;
  private NoArgGenerator uuidGenerator;

//========================================
// Getters and Setters
//----------------------------------------

  public String getCorrelationIdHeaderName() {
    return correlationIdHeaderName;
  }

  public void setCorrelationIdHeaderName(String correlationIdHeaderName) {
    this.correlationIdHeaderName = correlationIdHeaderName;
  }

  public NoArgGenerator getUuidGenerator() {
    return uuidGenerator;
  }

  public void setUuidGenerator(NoArgGenerator uuidGenerator) {
    this.uuidGenerator = uuidGenerator;
  }

  public Logger getLog() {
    return log;
  }

  public void setLog(Logger log) {
    this.log = log;
  }

//========================================
// Initialization
//----------------------------------------

  public void init() {
    if (this.uuidGenerator == null) {
      this.uuidGenerator = Generators.timeBasedGenerator(this.getHostMacAddress());
    }
  }

//========================================
// Processing
//----------------------------------------

  public String generateCorrelationId() {
    return this.uuidGenerator.generate().toString();
  }

  public String messageCorrelationIdFilterHandler(Supplier<String> messageCorrelationIdSupplier,
                                                  Consumer<String> messageCorrelationIdSetter) {

    String correlationId = null;

    // Try the correlation ID from the caller's supplier, if given
    if (messageCorrelationIdSupplier != null) {
      correlationId = messageCorrelationIdSupplier.get();
    }

    if (isNullOrEmpty(correlationId)) {
      // Try the MDC for the current request thread's correlation id
      correlationId = MDC.get(this.correlationIdHeaderName);

      if (isNullOrEmpty(correlationId)) {
        // Generate a new one
        correlationId = this.generateCorrelationId();

        this.log.debug(
            "failed to obtain correlation ID from MDC or message; creating a new one: id={}",
            correlationId);

      } else {
        this.log.debug("obtained correlation ID from MDC: id={}", correlationId);
      }

      // Send any new Correlation ID to the given setter; ignore if no setter was provided.
      if (messageCorrelationIdSetter != null) {
        messageCorrelationIdSetter.accept(correlationId);
      }
    } else {
      log.debug("inbound message already has correlation ID; using existing id: correlation-id={}",
                correlationId);
    }

    MDC.put(this.getCorrelationIdHeaderName(), correlationId);

    this.log.debug("using correlation id: id={}", correlationId);

    return correlationId;
  }

//========================================
// Internals
//----------------------------------------

  private EthernetAddress getHostMacAddress() {
    try {
      InetAddress address = InetAddress.getLocalHost();
      NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);

      if (networkInterface != null) {
        byte[] mac = networkInterface.getHardwareAddress();
        if (mac != null) {
          return new EthernetAddress(mac);
        }
      }
    } catch (Exception exc) {
      this.log.warn("Failed to determine machine ethernet address for UUID generation", exc);
    }

    return null;
  }

  private boolean isNullOrEmpty(String value) {
    if (value == null) {
      return true;
    }

    return value.isEmpty();
  }
}
