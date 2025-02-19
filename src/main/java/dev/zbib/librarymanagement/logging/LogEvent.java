package dev.zbib.librarymanagement.logging;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEvent {
    private static final DateTimeFormatter UTC_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    .withZone(ZoneOffset.UTC);

    @JsonSerialize(using = InstantSerializer.class)
    private final Instant timestamp;
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
    private final ErrorDetails error;

    @Getter
    @Builder
    public static class ErrorDetails {
        private final String type;
        private final String message;
        private final String stackTrace;
        private final Map<String, Object> metadata;
    }

    public static class LogEventBuilder {
        public LogEventBuilder timestamp() {
            this.timestamp = Instant.now();
            return this;
        }
    }
}