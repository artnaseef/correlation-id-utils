package com.artnaseef.correlationid.camel.util;

import com.google.common.base.Strings;

import com.artnaseef.correlationid.camel.util.common.CamelMessageCorrelationIdCommonUtils;
import com.artnaseef.correlationid.util.CorrelationIdUtils;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.MDC;

import java.util.Map;

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
      // Fallback to the non-JMS logic
      this.commonUtils.extractOrSetCorrelationId(messageHeaders, exchangeProperties, null);
    }
  }
}
