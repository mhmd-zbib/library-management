package dev.zbib.librarymanagement.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    @Around("@annotation(loggableOperation)")
    public Object logOperation(ProceedingJoinPoint joinPoint, LoggableOperation loggableOperation) throws Throwable {
        String correlationId = UUID.randomUUID().toString();
        long startTime = System.nanoTime();

        ThreadContext.put("correlationId", correlationId);
        try {
            if (loggableOperation.includeParameters()) {
                Map<String, Object> params = getMethodParameters(joinPoint, loggableOperation.maskSensitiveData());
                ThreadContext.put("parameters", OBJECT_MAPPER.writeValueAsString(params));
            }
            
            return executeWithLogging(joinPoint, loggableOperation, startTime, correlationId);
        } finally {
            ThreadContext.clearAll();
        }
    }

    private Object executeWithLogging(
            ProceedingJoinPoint joinPoint,
            LoggableOperation loggableOperation,
            long startTime,
            String correlationId) throws Throwable {
        LogEvent.LogEventBuilder eventBuilder = LogEvent.builder()
                .timestamp()
                .correlationId(correlationId)
                .service(joinPoint.getTarget().getClass().getSimpleName())
                .method(joinPoint.getSignature().getName())
                .operationType(loggableOperation.operationType())
                .description(loggableOperation.description())
                .request(getRequestInformation())
                .user(getUserInformation());

        if (loggableOperation.includeParameters()) {
            eventBuilder.parameters(getMethodParameters(joinPoint, loggableOperation.maskSensitiveData()));
        }

        logger.info("Operation started - {}", formatLogEvent(eventBuilder.build()));

        try {
            Object result = joinPoint.proceed();
            LogEvent successEvent = eventBuilder
                    .status("SUCCESS")
                    .executionTime(formatExecutionTime(startTime))
                    .result(loggableOperation.includeResult() ? LogMaskingUtil.maskSensitiveData(result) : null)
                    .build();

            logger.info("Operation completed - {}", formatLogEvent(successEvent));
            return result;
        } catch (Exception e) {
            LogEvent.ErrorDetails errorDetails = LogEvent.ErrorDetails.builder()
                    .type(e.getClass().getSimpleName())
                    .message(e.getMessage())
                    .stackTrace(getStackTraceAsString(e))
                    .metadata(extractErrorMetadata(e))
                    .build();

            LogEvent errorEvent = eventBuilder
                    .status("ERROR")
                    .error(errorDetails)
                    .executionTime(formatExecutionTime(startTime))
                    .build();

            logger.error("Operation failed - {}", formatLogEvent(errorEvent));
            throw e;
        }
    }

    private Map<String, String> getRequestInformation() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return Map.of(
                        "method", request.getMethod(),
                        "uri", request.getRequestURI(),
                        "clientIp", getClientIp(request),
                        "userAgent", Optional.ofNullable(request.getHeader("User-Agent")).orElse("Unknown")
                );
            }
        } catch (Exception e) {
            logger.debug("Failed to get request information", e);
        }
        return Collections.emptyMap();
    }

    private String getClientIp(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .or(() -> Optional.ofNullable(request.getHeader("X-Real-IP")))
                .orElse(request.getRemoteAddr());
    }

    private Map<String, String> getUserInformation() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(auth -> Map.of(
                        "username", auth.getName(),
                        "roles", auth.getAuthorities().toString()
                ))
                .orElse(Collections.emptyMap());
    }

    private Map<String, Object> getMethodParameters(ProceedingJoinPoint joinPoint, boolean maskSensitive) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] paramValues = joinPoint.getArgs();

            Map<String, Object> parameters = new LinkedHashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                if (paramValues[i] != null) {
                    Object value = maskSensitive ? 
                        LogMaskingUtil.maskSensitiveData(paramValues[i]) : 
                        paramValues[i];
                    parameters.put(paramNames[i], value);
                }
            }
            return parameters;
        } catch (Exception e) {
            logger.debug("Failed to process method parameters", e);
            return Collections.emptyMap();
        }
    }

    private String formatLogEvent(LogEvent event) {
        try {
            return OBJECT_MAPPER.writeValueAsString(event);
        } catch (Exception e) {
            logger.warn("Failed to format log event", e);
            return event.toString();
        }
    }

    private String formatExecutionTime(long startTimeNanos) {
        return String.format("%.2fms", (System.nanoTime() - startTimeNanos) / 1_000_000.0);
    }

    private String getStackTraceAsString(Exception e) {
        if (e == null) return null;
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            return sw.toString();
        } catch (IOException ex) {
            return "Failed to capture stack trace";
        }
    }

    private Map<String, Object> extractErrorMetadata(Exception e) {
        Map<String, Object> metadata = new HashMap<>();
        if (e instanceof DataIntegrityViolationException) {
            metadata.put("constraint", extractConstraintName(e.getMessage()));
        }

        return metadata;
    }

    private String extractConstraintName(String message) {
        if (message == null) return null;
        int constraintIndex = message.indexOf("constraint");
        if (constraintIndex != -1) {
            int startQuote = message.indexOf("[", constraintIndex);
            int endQuote = message.indexOf("]", startQuote);
            if (startQuote != -1 && endQuote != -1) {
                return message.substring(startQuote + 1, endQuote);
            }
        }
        return null;
    }
}