package com.artnaseef.correlationid.cxf;

import com.artnaseef.correlationid.util.CorrelationIdUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.artnaseef.correlationid.cxf.CxfCorrelationIdInInterceptor.CXF_CORRELATION_ID_STORE_NAME;

public class CxfCorrelationIdOutInterceptor extends AbstractPhaseInterceptor {

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(CxfCorrelationIdOutInterceptor.class);

    private Logger log = DEFAULT_LOGGER;


    private CorrelationIdUtils correlationIdUtils;

    public CxfCorrelationIdOutInterceptor() {
        super(Phase.MARSHAL);
    }

    public CxfCorrelationIdOutInterceptor(String phase) {
        super(phase);
    }

//========================================
// Getters and Setters
//----------------------------------------

    public CorrelationIdUtils getCorrelationIdUtils() {
        return correlationIdUtils;
    }

    public void setCorrelationIdUtils(CorrelationIdUtils correlationIdUtils) {
        this.correlationIdUtils = correlationIdUtils;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

//========================================
// Message Processing
//----------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public void handleMessage(Message message) throws Fault {
        String correlationId =
                this.correlationIdUtils.messageCorrelationIdFilterHandler(
                        () -> this.extractInMessageCorrelationId(message),
                        null
                );

        String correlationIdheaderName = this.correlationIdUtils.getCorrelationIdHeaderName();

        Map<String, List<String>> headers = (Map) message.get(Message.PROTOCOL_HEADERS);

        // Copy-and-pasted from the CXF docs jax-rs-filters page.
        if (headers == null) {
            headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
            message.put(Message.PROTOCOL_HEADERS, headers);
        }

        headers.put(correlationIdheaderName, Collections.singletonList(correlationId));
    }

    public String extractInMessageCorrelationId(Message message) {
        Message inMessage = message.getExchange().getInMessage();

        if (inMessage != null) {
            return (String) inMessage.get(CXF_CORRELATION_ID_STORE_NAME);
        }

        return null;
    }
}
