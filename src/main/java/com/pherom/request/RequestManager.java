package com.pherom.request;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class RequestManager {

    private static Logger logger = LoggerFactory.getLogger("request-logger");

    private static RequestManager instance = null;

    public static RequestManager getInstance() {
        if (instance == null)
            instance = new RequestManager();
        return instance;
    }

    private int incomingRequestCounter = 1;

    public void incrementIncomingRequest(HttpServletRequest request, long durationInMS) {
        MDC.put("requestNumber", Integer.toString(incomingRequestCounter));
        logger.info(String.format("Incoming request | #%d | resource: %s | HTTP Verb %s", incomingRequestCounter, request.getRequestURI(), request.getMethod()));
        logger.debug(String.format("request #%d duration: %dms", incomingRequestCounter, durationInMS));
        incomingRequestCounter++;
    }

}
