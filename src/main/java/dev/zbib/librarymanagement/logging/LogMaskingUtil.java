package dev.zbib.librarymanagement.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogMaskingUtil {
    private static final Logger logger = LogManager.getLogger(LogMaskingUtil.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private static final String MASK = "********";

    private static final Set<String> SENSITIVE_FIELDS = Set.of("password",
            "token",
            "secret",
            "authorization",
            "apiKey",
            "creditCard",
            "ssn");

    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "***";
        }
        return parts[0].substring(0,
                1) + "***@" + parts[1].substring(0,
                1) + "***" + parts[1].substring(parts[1].lastIndexOf('.'));
    }

    public static Object maskSensitiveData(Object value) {
        if (value == null) {
            return null;
        }

        try {
            String json = OBJECT_MAPPER.writeValueAsString(value);
            ObjectNode node = (ObjectNode) OBJECT_MAPPER.readTree(json);

            node.fields()
                    .forEachRemaining(entry -> {
                        String fieldName = entry.getKey()
                                .toLowerCase();
                        if (SENSITIVE_FIELDS.contains(fieldName)) {
                            node.put(entry.getKey(),
                                    MASK);
                        } else if (fieldName.contains("email")) {
                            node.put(entry.getKey(),
                                    maskEmail(entry.getValue()
                                            .asText()));
                        }
                    });

            return node;
        } catch (Exception e) {
            String stringValue = value.toString()
                    .toLowerCase();
            return SENSITIVE_FIELDS.stream()
                    .anyMatch(stringValue::contains) ? MASK : value;
        }
    }
}