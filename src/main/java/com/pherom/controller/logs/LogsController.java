package com.pherom.controller.logs;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.pherom.request.RequestManager;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogsController {

    public static final String ENDPOINT = "/logs";

    @GetMapping(path = ENDPOINT + "/level")
    public String getLoggerLevel(@RequestParam(value = "logger-name") String loggerName, HttpServletRequest request) {
        long start = System.currentTimeMillis();
        Level loggerLevel;
        String response;
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.exists(loggerName);
        if (logger != null) {
            loggerLevel = logger.getLevel();
            response = loggerLevel.levelStr;
        }
        else {
            response = "Error: No logger with the specified name was found";
        }

        long end = System.currentTimeMillis();
        RequestManager.getInstance().incrementIncomingRequest(request, end - start);
        return response;
    }

    @PutMapping(path = ENDPOINT + "/level")
    public String setLoggerLevel(@RequestParam(value = "logger-name") String loggerName,
                               @RequestParam(value = "logger-level") String loggerLevel,
                                 HttpServletRequest request) {
        long start = System.currentTimeMillis();
        String response;
        ch.qos.logback.classic.Logger logger;
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger = loggerContext.exists(loggerName);
        if (logger != null) {
            if (loggerLevel.equalsIgnoreCase(Level.DEBUG.levelStr)
            || loggerLevel.equalsIgnoreCase((Level.INFO.levelStr))
            || loggerLevel.equalsIgnoreCase(Level.ERROR.levelStr)) {
                logger.setLevel(Level.toLevel(loggerLevel));
                response = logger.getLevel().levelStr;
            }
            else {
                response = "Error: The specified logger level is unsupported";
            }
        }
        else {
            response = "Error: No logger with the specified name was found";
        }
        long end = System.currentTimeMillis();
        RequestManager.getInstance().incrementIncomingRequest(request, end - start);
        return response;
    }

}
