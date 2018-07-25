package com.artnaseef.correlationid.camel.util;

import com.artnaseef.correlationid.camel.util.common.CamelMessageCorrelationIdCommonUtils;
import com.artnaseef.correlationid.util.CorrelationIdUtils;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Correlation ID handler for Camel routes.
 */
public class CamelInHttpMessageCorrelationIdHandler implements Processor {

  public static final String
      BODY_CORRELATION_ID_SUPPLIER_PROP =
      "com.artnaseef.correlationid.util.body-supplier";

  private CorrelationIdUtils correlationIdUtils;
  private CamelMessageCorrelationIdCommonUtils commonUtils;

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
    Function<Exchange, String>
        bodyCorrelationIdSupplier =
        exchange.getProperty(BODY_CORRELATION_ID_SUPPLIER_PROP, Function.class);

    Supplier<String> supplierWrapper = null;
    if (bodyCorrelationIdSupplier != null) {
      supplierWrapper = () -> bodyCorrelationIdSupplier.apply(exchange);
    }

    this.commonUtils.extractOrSetCorrelationId(messageHeaders, exchangeProperties, supplierWrapper);
  }
}
