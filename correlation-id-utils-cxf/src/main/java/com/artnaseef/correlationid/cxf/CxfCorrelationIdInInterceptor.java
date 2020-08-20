package com.artnaseef.correlationid.cxf;

import com.artnaseef.correlationid.util.CorrelationIdUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CxfCorrelationIdInInterceptor extends AbstractPhaseInterceptor {

    public static final String CXF_CORRELATION_ID_STORE_NAME =
            CxfCorrelationIdInInterceptor.class.getName() + ".CORRELATION-ID";

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(CxfCorrelationIdInInterceptor.class);

    private Logger log = DEFAULT_LOGGER;


    private CorrelationIdUtils correlationIdUtils;

    public CxfCorrelationIdInInterceptor() {
        super(Phase.PRE_INVOKE);
    }

    public CxfCorrelationIdInInterceptor(String phase) {
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
        String correlationIdheaderName = this.correlationIdUtils.getCorrelationIdHeaderName();

        //
        // Use the utility to drive the extraction and updating of correlation ID.
        //
        String correlationId =
            this.correlationIdUtils.messageCorrelationIdFilterHandler(
                    () -> this.extractCorrelationId(message, correlationIdheaderName),
                    (newCorrelationId) -> this.updateCorrelationId(message, correlationIdheaderName, newCorrelationId)
            );

        //
        // Store the correlation-ID on the message so it can be reliably retrieved from there later.
        //
        message.put(CXF_CORRELATION_ID_STORE_NAME, correlationId);
    }

    @SuppressWarnings("unchecked")
    private String extractCorrelationId(Message message, String correlationIdHeaderName) {
        Map<String, List<String>> headers = (Map) message.get(Message.PROTOCOL_HEADERS);
        String incomingCorrelationId = null;

        if (headers != null) {
            List<String> values = headers.get(correlationIdHeaderName);
            if (values != null) {
                // Just use the first value and ignore any additional ones
                incomingCorrelationId = values.get(0);

                if (values.size() > 1) {
                    // Log the extra correlation IDs so it is possible to map them
                    this.log.info(
                            "Extracted multiple correlation IDs for message; using first one: " +
                            "effective-correlation-id={}; all-correlation-ids={}",
                            incomingCorrelationId, values);
                }
            }
        }

        return incomingCorrelationId;
    }

    @SuppressWarnings("unchecked")
    private void updateCorrelationId(Message message, String correlationIdHeaderName, String newCorrelationId){
        Map<String, List<String>> headers = (Map) message.get(Message.PROTOCOL_HEADERS);

        if (headers == null) {
            headers = new HashMap<>();
            message.put(Message.PROTOCOL_HEADERS, headers);
        }

        headers.put(correlationIdHeaderName, Collections.singletonList(newCorrelationId));
    }
}
