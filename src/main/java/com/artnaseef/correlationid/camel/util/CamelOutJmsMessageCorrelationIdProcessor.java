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

  private CorrelationIdUtils correlationIdUtils;

//========================================
// Getters and Setters
//----------------------------------------

  public CorrelationIdUtils getCorrelationIdUtils() {
    return correlationIdUtils;
  }

  public void setCorrelationIdUtils(CorrelationIdUtils correlationIdUtils) {
    this.correlationIdUtils = correlationIdUtils;
  }

//========================================
// Processing
//----------------------------------------

  /**
   * Extract the correlation ID from the exchange properties and copy out to the JMS message header.
   *
   * @param exchange
   * @throws Exception
   */
  @Override
  public void process(Exchange exchange) throws Exception {
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
