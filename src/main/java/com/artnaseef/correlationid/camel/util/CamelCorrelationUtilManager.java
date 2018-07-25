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
