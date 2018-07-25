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

/**
 * Manager of the camel correlation id utilities to simplify injection and use within routes that use more than one.
 */
public class CamelCorrelationUtilManager {
  private CamelInHttpMessageCorrelationIdHandler camelInHttpMessageCorrelationIdHandler;
  private CamelInJmsMessageCorrelationIdProcessor camelInJmsMessageCorrelationIdProcessor;
  private CamelOutMessageHeaderCorrelationIdHandler camelOutMessageHeaderCorrelationIdHandler;
  private CamelOutJmsMessageCorrelationIdProcessor camelOutJmsMessageCorrelationIdProcessor;

  public CamelInHttpMessageCorrelationIdHandler getCamelInHttpMessageCorrelationIdHandler() {
    return camelInHttpMessageCorrelationIdHandler;
  }

  public void setCamelInHttpMessageCorrelationIdHandler(
      CamelInHttpMessageCorrelationIdHandler camelInHttpMessageCorrelationIdHandler) {
    this.camelInHttpMessageCorrelationIdHandler = camelInHttpMessageCorrelationIdHandler;
  }

  public CamelInJmsMessageCorrelationIdProcessor getCamelInJmsMessageCorrelationIdProcessor() {
    return camelInJmsMessageCorrelationIdProcessor;
  }

  public void setCamelInJmsMessageCorrelationIdProcessor(
      CamelInJmsMessageCorrelationIdProcessor camelInJmsMessageCorrelationIdProcessor) {
    this.camelInJmsMessageCorrelationIdProcessor = camelInJmsMessageCorrelationIdProcessor;
  }

  public CamelOutMessageHeaderCorrelationIdHandler getCamelOutMessageHeaderCorrelationIdHandler() {
    return camelOutMessageHeaderCorrelationIdHandler;
  }

  public void setCamelOutMessageHeaderCorrelationIdHandler(
      CamelOutMessageHeaderCorrelationIdHandler camelOutMessageHeaderCorrelationIdHandler) {
    this.camelOutMessageHeaderCorrelationIdHandler = camelOutMessageHeaderCorrelationIdHandler;
  }

  public CamelOutJmsMessageCorrelationIdProcessor getCamelOutJmsMessageCorrelationIdProcessor() {
    return camelOutJmsMessageCorrelationIdProcessor;
  }

  public void setCamelOutJmsMessageCorrelationIdProcessor(
      CamelOutJmsMessageCorrelationIdProcessor camelOutJmsMessageCorrelationIdProcessor) {
    this.camelOutJmsMessageCorrelationIdProcessor = camelOutJmsMessageCorrelationIdProcessor;
  }
}
