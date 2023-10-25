/*
 * Copyright (c) 2023 Arthur Naseef
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

import org.apache.camel.AsyncCallback;
import org.slf4j.MDC;

/**
 *
 */
public class CorrelationIdUtilsUowCallback implements AsyncCallback {

  private final AsyncCallback delegate;
  private final String correlationIdHeader;
  private final String originalCorrelationId;

  public CorrelationIdUtilsUowCallback(AsyncCallback delegate, String correlationIdHeader,
                                       String originalCorrelationId) {
    this.delegate = delegate;
    this.correlationIdHeader = correlationIdHeader;
    this.originalCorrelationId = originalCorrelationId;
  }

  @Override
  public void done(boolean doneSync) {
    if (!doneSync) {
      // Restore the thread original correlation Id
      if (this.originalCorrelationId != null) {
        MDC.put(this.correlationIdHeader, this.originalCorrelationId);
      } else {
        MDC.remove(this.correlationIdHeader);
      }
    }

    if (this.delegate != null) {
      this.delegate.done(doneSync);
    }
  }
}
