package dev.zbib.librarymanagement.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Set<String> SENSITIVE_FIELDS = Set.of("password",
            "token",
            "secret");

    @Around("@annotation(loggableOperation)")
    public Object logOperation(ProceedingJoinPoint joinPoint, LoggableOperation loggableOperation) throws Throwable {
        final long startTime = System.currentTimeMillis();
        final String methodName = joinPoint.getSignature()
                .getName();
        final String className = joinPoint.getTarget()
                .getClass()
                .getSimpleName();
        final String operationType = loggableOperation.operationType();
        final LogLevel logLevel = loggableOperation.level();

        Map<String, Object> logData = new LinkedHashMap<>();
        logData.put("timestamp",
                LocalDateTime.now()
                        .format(DateTimeFormatter.ISO_DATE_TIME));
        logData.put("class",
                className);
        logData.put("method",
                methodName);
        logData.put("operationType",
                operationType);
        logData.put("description",
                loggableOperation.description());

        addRequestInformation(logData);
        addUserInformation(logData);
        if (loggableOperation.includeParameters()) {
            addMethodParameters(joinPoint,
                    logData,
                    loggableOperation.maskSensitiveData());
        }

        try {

            log(logLevel,
                    "Starting operation: {}",
                    logData);

            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            logData.put("executionTime",
                    executionTime + "ms");
            logData.put("status",
                    "SUCCESS");

            if (loggableOperation.includeResult() && result != null) {
                logData.put("result",
                        maskSensitiveData(objectMapper.writeValueAsString(result)));
            }
            log(logLevel,
                    "Completed operation: {}",
                    logData);

            return result;

        } catch (Exception e) {

            logData.put("status",
                    "ERROR");
            logData.put("errorType",
                    e.getClass()
                            .getSimpleName());
            logData.put("errorMessage",
                    e.getMessage());
            logData.put("executionTime",
                    (System.currentTimeMillis() - startTime) + "ms");


            logger.error("Operation failed: {}",
                    logData,
                    e);
            throw e;
        }
    }

    private void addRequestInformation(Map<String, Object> logData) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                logData.put("requestMethod",
                        request.getMethod());
                logData.put("requestURI",
                        request.getRequestURI());
                logData.put("clientIP",
                        request.getRemoteAddr());
            }
        } catch (Exception e) {
            logger.debug("Could not add request information to log",
                    e);
        }
    }

    private void addUserInformation(Map<String, Object> logData) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            logData.put("username",
                    authentication.getName());
        }
    }

    private void addMethodParameters(ProceedingJoinPoint joinPoint, Map<String, Object> logData, boolean maskSensitive) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] parameterValues = joinPoint.getArgs();

            Map<String, Object> parameters = new HashMap<>();
            for (int i = 0; i < parameterNames.length; i++) {
                String paramName = parameterNames[i];
                Object paramValue = parameterValues[i];

                if (paramValue != null) {
                    if (maskSensitive) {
                        parameters.put(paramName,
                                maskSensitiveData(paramValue));
                    } else {
                        parameters.put(paramName,
                                paramValue);
                    }
                }
            }

            if (!parameters.isEmpty()) {
                logData.put("parameters",
                        parameters);
            }
        } catch (Exception e) {
            logger.debug("Could not add method parameters to log",
                    e);
        }
    }

    private Object maskSensitiveData(Object value) {
        if (value == null) {
            return null;
        }

        try {
            String stringValue = value.toString();
            if (SENSITIVE_FIELDS.stream()
                    .anyMatch(field -> stringValue.toLowerCase()
                            .contains(field.toLowerCase()))) {
                return "********";
            }
            return value;
        } catch (Exception e) {
            return value;
        }
    }

    private void log(LogLevel level, String message, Object... args) {
        switch (level) {
            case TRACE -> logger.trace(message,
                    args);
            case DEBUG -> logger.debug(message,
                    args);
            case INFO -> logger.info(message,
                    args);
            case WARN -> logger.warn(message,
                    args);
            case ERROR -> logger.error(message,
                    args);
        }
    }
}