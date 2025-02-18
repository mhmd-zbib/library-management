package dev.zbib.librarymanagement.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Around("@annotation(loggableOperation)")
    public Object logOperation(ProceedingJoinPoint joinPoint, LoggableOperation loggableOperation) throws Throwable {
        String correlationId = UUID.randomUUID().toString();
        long startTime = System.nanoTime();

        try (MDC.MDCCloseable mdc = MDC.putCloseable("correlationId", correlationId)) {
            return executeWithLogging(joinPoint, loggableOperation, startTime, correlationId);
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

        log.info("Operation started - {}", formatLogEvent(eventBuilder.build()));

        try {
            Object result = joinPoint.proceed();
            LogEvent successEvent = eventBuilder
                    .status("SUCCESS")
                    .executionTime(formatExecutionTime(startTime))
                    .result(loggableOperation.includeResult() ? LogMaskingUtil.maskSensitiveData(result) : null)
                    .build();

            log.info("Operation completed - {}", formatLogEvent(successEvent));

            return result;
        } catch (Exception e) {
            LogEvent errorEvent = eventBuilder
                    .status("ERROR")
                    .errorType(e.getClass().getSimpleName())
                    .errorMessage(e.getMessage())
                    .executionTime(formatExecutionTime(startTime))
                    .build();

            log.error("Operation failed - {}", formatLogEvent(errorEvent), e);
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
            log.debug("Failed to get request information", e);
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

            Map<String, Object> parameters = new HashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                if (paramValues[i] != null) {
                    parameters.put(paramNames[i],
                            maskSensitive ? LogMaskingUtil.maskSensitiveData(paramValues[i]) : paramValues[i]);
                }
            }
            return parameters;
        } catch (Exception e) {
            log.debug("Failed to process method parameters", e);
            return Collections.emptyMap();
        }
    }

    private String formatLogEvent(LogEvent event) {
        try {
            return OBJECT_MAPPER.writeValueAsString(event);
        } catch (Exception e) {
            log.warn("Failed to format log event", e);
            return event.toString();
        }
    }

    private String formatExecutionTime(long startTimeNanos) {
        return String.format("%.2fms", (System.nanoTime() - startTimeNanos) / 1_000_000.0);
    }
}