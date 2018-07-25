package com.artnaseef.correlationid.camel.util;

import com.artnaseef.correlationid.camel.util.common.CamelMessageCorrelationIdCommonUtils;
import com.artnaseef.correlationid.util.CorrelationIdUtils;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Correlation ID handler for Camel routes.
 */
public class CamelOutMessageHeaderCorrelationIdHandler implements Processor {

  private CorrelationIdUtils correlationIdUtils;

//========================================
// Getters and Setters
//----------------------------------------

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

  @Override
  public void process(Exchange exchange) throws Exception {
    String correlationId = exchange.getProperty(CamelMessageCorrelationIdCommonUtils.CORRELATION_ID_EXCHANGE_PROPERTY,
                                                String.class);
    if (correlationId != null) {
      exchange.getIn().setHeader(this.correlationIdUtils.getCorrelationIdHeaderName(), correlationId);
    }
  }
}
