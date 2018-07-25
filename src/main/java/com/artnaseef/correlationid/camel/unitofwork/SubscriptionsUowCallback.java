package com.artnaseef.correlationid.camel.unitofwork;

import org.apache.camel.AsyncCallback;
import org.slf4j.MDC;

/**
 *
 */
public class SubscriptionsUowCallback implements AsyncCallback {

  private final AsyncCallback delegate;
  private final String correlationIdHeader;
  private final String originalCorrelationId;

  public SubscriptionsUowCallback(AsyncCallback delegate, String correlationIdHeader,
                                  String originalCorrelationId) {
    this.delegate = delegate;
    this.correlationIdHeader = correlationIdHeader;
    this.originalCorrelationId = originalCorrelationId;
  }

  @Override
  public void done(boolean doneSync) {
    if (!doneSync) {
      // Restore the thread original correlation Id
      MDC.put(this.correlationIdHeader, this.originalCorrelationId);
    }

    if (this.delegate != null) {
      this.delegate.done(doneSync);
    }
  }
}
