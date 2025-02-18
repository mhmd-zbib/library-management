package dev.zbib.librarymanagement.logging;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEvent {
    private static final DateTimeFormatter UTC_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneOffset.UTC);

    private final String timestamp;
    private final String correlationId;
    private final String service;
    private final String method;
    private final String operationType;
    private final String description;
    private final Map<String, String> request;
    private final Map<String, String> user;
    private final Map<String, Object> parameters;
    private final String status;
    private final String executionTime;
    private final Object result;
    private final String errorType;
    private final String errorMessage;

    public static class LogEventBuilder {
        public LogEventBuilder timestamp() {
            this.timestamp = UTC_FORMATTER.format(Instant.now());
            return this;
        }
    }
}