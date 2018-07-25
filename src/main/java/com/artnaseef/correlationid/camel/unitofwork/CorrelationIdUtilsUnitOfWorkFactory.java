package com.artnaseef.correlationid.camel.unitofwork;

import com.artnaseef.correlationid.util.CorrelationIdUtils;

import org.apache.camel.Exchange;
import org.apache.camel.spi.UnitOfWork;
import org.apache.camel.spi.UnitOfWorkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class CorrelationIdUtilsUnitOfWorkFactory implements UnitOfWorkFactory {

  private static final Logger DEFAULT_LOGGER = LoggerFactory
      .getLogger(CorrelationIdUtilsUnitOfWorkFactory.class);

  private Logger log = DEFAULT_LOGGER;
  private CorrelationIdUtils correlationIdUtils;

//========================================
// Getters and Setters
//----------------------------------------

  public Logger getLog() {
    return log;
  }

  public void setLog(Logger log) {
    this.log = log;
  }

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
  public UnitOfWork createUnitOfWork(Exchange exchange) {
    return new CorrelationIdUtilsUnitOfWork(exchange, this.correlationIdUtils);
  }
}
